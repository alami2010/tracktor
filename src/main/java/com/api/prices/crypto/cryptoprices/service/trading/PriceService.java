package com.api.prices.crypto.cryptoprices.service.trading;

import com.api.prices.crypto.cryptoprices.client.pojo.Currency;
import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformation;
import com.api.prices.crypto.cryptoprices.entity.CurrencyToTrack;
import com.api.prices.crypto.cryptoprices.entity.Signal;
import com.api.prices.crypto.cryptoprices.entity.StrategyRuleResult;
import com.api.prices.crypto.cryptoprices.service.alert.AlertService;
import com.api.prices.crypto.cryptoprices.service.data.ServerClientService;
import com.api.prices.crypto.cryptoprices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PriceService {

    public static final int MULTIPLY_BIG_MARGE = 3;
    public static final double MULTIPLY_SMALL_MARGE = 1.3;
    private static Logger logger = LoggerFactory.getLogger("stats");


    @Autowired
    private ServerClientService serverClientService;

    @Autowired
    private IndicatorTechnicalService indicatorTechnicalService;

    @Autowired
    private AlertService alertService;


    public void initMonitoringOfPrice() {


        logger.info("===> Monitoring price <=== ");


        String currenciesToTrack = serverClientService.getCurrencyToTracks().stream().map(currencyToTrack -> currencyToTrack.getName()).sorted().collect(Collectors.joining(","));
        CurrencyInformation currencyInfo = serverClientService.getOneCurrenciesInfo(currenciesToTrack);
        if (currencyInfo != null && currencyInfo.getData() != null) {

            Set<Map.Entry<String, Currency>> currencies = currencyInfo.getData().entrySet();

            double sumDouble1H = currencies.stream().mapToDouble(currency -> currency.getValue().getQuote().getUSD().getPercent_change_1h()).sum();
            double sumDouble24H = currencies.stream().mapToDouble(currency -> currency.getValue().getQuote().getUSD().getPercent_change_24h()).sum();
            double sumDouble7D = currencies.stream().mapToDouble(currency -> currency.getValue().getQuote().getUSD().getPercent_change_7d()).sum();

            logger.info("Somme  " + sumDouble1H + " , " + sumDouble24H + " , " + sumDouble7D);
            currencies.stream().forEach(currency -> checkPrice(currency));

            currencies.stream()
                    .filter(stringCurrencyEntry -> Utils.checkIfAplhaBrokerSupportCurrency(stringCurrencyEntry.getValue())).
                    map(getSignalIndicatorTechnique())
                    .forEach(strategyRuleResults -> System.out.println(strategyRuleResults));
        }
    }

    private Function<Map.Entry<String, Currency>, List<StrategyRuleResult>> getSignalIndicatorTechnique() {
        return currency -> indicatorTechnicalService.runStrategies(currency.getValue().getSymbol(), serverClientService.getTimeSeriesDaily().get(currency.getValue().getSymbol()), currency.getValue().getQuote().getUSD().getPrice());
    }


    private void checkPrice(Map.Entry<String, Currency> currency) {
        CurrencyToTrack currencyToTrack = getCurrencyToTrackByKey(serverClientService.getCurrencyToTracks(), currency.getKey());
        double priceCurrency = currency.getValue().getQuote().getUSD().getPrice();

        checkPrice(currencyToTrack, priceCurrency, priceCurrency >= currencyToTrack.getMax(), Signal.SIGNAL_SELL);
        checkPrice(currencyToTrack, priceCurrency, priceCurrency <= currencyToTrack.getMin(), Signal.SIGNAL_BUY);

        displayConsole(currency, currencyToTrack, priceCurrency);


    }

    private void displayConsole(Map.Entry<String, Currency> currency, CurrencyToTrack currencyToTrack, double priceCurrency) {

        String leftAlignFormat = "| %-5s |%-16s | %-10s | %-10s | %-10s | %-10s | %-10s |";
        logger.info(String.format(leftAlignFormat, StringUtils.capitalize(currencyToTrack.getName()), priceCurrency, currencyToTrack.getMin(), currencyToTrack.getMax(), currency.getValue().getQuote().getUSD().getPercent_change_1h(), currency.getValue().getQuote().getUSD().getPercent_change_24h(), currency.getValue().getQuote().getUSD().getPercent_change_7d()));
    }


    private CurrencyToTrack getCurrencyToTrackByKey(List<CurrencyToTrack> currencyToTracks, String key) {
        return currencyToTracks.stream().filter(currencyToTrack -> currencyToTrack.getName().equals(key)).findFirst().get();
    }


    private void checkPrice(CurrencyToTrack currencyToTrack, double priceCurrency, boolean isDecisionChecked, Signal signal) {
        if (isDecisionChecked) {
            CurrencyToTrack oldCurrencyToTrack = currencyToTrack.duplicate();
            updateCurrencyToTrack(currencyToTrack, signal, priceCurrency);
            alertService.alert(priceCurrency, oldCurrencyToTrack, currencyToTrack, signal);
        }
    }

    // todo se baser sur les valeur de time series :)
    private void updateCurrencyToTrack(CurrencyToTrack currencyToTrack, Signal signal, double priceCurrency) {
        double marge;
        switch (signal) {
            case SIGNAL_SELL:
                marge = priceCurrency - currencyToTrack.getMax();
                currencyToTrack.setMax(priceCurrency + marge * MULTIPLY_BIG_MARGE);
                currencyToTrack.setMin(currencyToTrack.getMin() + marge * MULTIPLY_SMALL_MARGE);
                break;
            case SIGNAL_BUY:
                marge = currencyToTrack.getMin() - priceCurrency;
                currencyToTrack.setMin(priceCurrency - marge * MULTIPLY_BIG_MARGE);
                currencyToTrack.setMax(currencyToTrack.getMax() - marge * MULTIPLY_SMALL_MARGE);
                break;
        }
        serverClientService.updateCurrency(currencyToTrack);
    }


}
