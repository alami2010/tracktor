package com.api.prices.crypto.cryptoprices.service.trading;

import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrency;
import com.api.prices.crypto.cryptoprices.entity.Signal;
import com.api.prices.crypto.cryptoprices.entity.StrategyRule;
import com.api.prices.crypto.cryptoprices.entity.StrategyRuleResult;
import com.api.prices.crypto.cryptoprices.service.data.ServerClientService;
import com.api.prices.crypto.cryptoprices.utils.StategyRuleBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicatorTechnicalService {
    private static Logger logger = LogManager.getLogger(IndicatorTechnicalService.class);


    //todo
    //Buy signals are given when the 21-day MA moves above the 40-day MA and when the 40-day MA is advancing.
    //Sell signals are given when the 21-day MA moves below the 40-day MA and when the 40-day MA is declining.

    @Autowired
    private IAlphaVantageClient alphaVantageClient;

    @Autowired
    private ServerClientService serverClientService;

    private Map<String, List<CryptoCurrency>> timeSeriesDaily;
    private Map<String, List<CryptoCurrency>> timeSeriesWeekly;


    public List<StrategyRuleResult> runStrategies(String currency, List<CryptoCurrency> cryptoCurrencyList, double currencyPrice) {
        TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName("YSF_HOPE").build();

        int countBars = populateBars(series, cryptoCurrencyList);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);


        List<StrategyRule> strategies = StategyRuleBuilder.generateRuleStrategies(currencyPrice);


        // return Optional.<Integer>empty();
        // return Optional.of(out);
        // todo optional in exception case
        List<StrategyRuleResult> strategiesResult = strategies.stream().map(strategyRule -> {
            try {

                return calculate(strategyRule, closePrice, countBars, currency);


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }).collect(Collectors.toList());

        return strategiesResult;
    }


    private StrategyRuleResult calculate(StrategyRule strategyRule, Indicator closePrice, int nbrBars, String currency) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] allConstructors = strategyRule.getIndicator().getDeclaredConstructors();
        Indicator indicator = (Indicator) allConstructors[0].newInstance(closePrice, strategyRule.getLength());
        Num result = (Num) indicator.getValue(nbrBars - 1);

        boolean test = strategyRule.getPredicate() != null && strategyRule.getPredicate().test(result.intValue());

        Signal signal = Signal.NO_SIGNAL;

        if (test) {
            System.out.println("DONE ALERT " + currency + " " + strategyRule.getRuleName() + "   " + result);
            signal = strategyRule.getSignal();

        }

        return new StrategyRuleResult(result, strategyRule.getRuleName().name(), signal);
    }


    private int populateBars(TimeSeries series, List<CryptoCurrency> cryptoCurrencySupplier) {
        cryptoCurrencySupplier.forEach(cryptoCurrencyHisotrian -> {


            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(cryptoCurrencyHisotrian.getDate().toInstant(), ZoneId.systemDefault());
            series.addBar(zonedDateTime, cryptoCurrencyHisotrian.getOpen(), cryptoCurrencyHisotrian.getHigh(), cryptoCurrencyHisotrian.getLow(), cryptoCurrencyHisotrian.getClose(), cryptoCurrencyHisotrian.getVolume());


        });
        return cryptoCurrencySupplier.size();
    }


}