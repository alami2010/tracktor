package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.*;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.entity.Strategy;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class TimeSeriesService {
    private static Logger logger = LogManager.getLogger(TimeSeriesService.class);


    @Autowired
    private IAlphaVantageClient alphaVantageClient;

    @Autowired
    private ServerClientService serverClientService;

    private Map<String, List<CryptoCurrency>> timeSeriesDaily;
    private Map<String, List<CryptoCurrency>> timeSeriesWeekly;

    // cette fonction doit tourné une seul fois par jour
    public void loadTimeSeries() {


        int nbrMonths = 1;

        Stream.of(AlphaVantageCurrency.values()).forEach(alphaVantageCurrency -> {

            try {

                String currency = alphaVantageCurrency.name();
                System.out.println(currency);


                List<CryptoCurrency> cryptoCurrenciesByDay = getCryptoCurrencies(currency, CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);
                List<CryptoCurrency> cryptoCurrenciesByWeek = getCryptoCurrencies(currency, CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);

                Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplierDays = getValidTimeSeries(nbrMonths, cryptoCurrenciesByDay);
                Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplierWeeks = getValidTimeSeries(nbrMonths, cryptoCurrenciesByWeek);


                serverClientService.saveTimeSeries(currency, cryptoCurrencySupplierDays, CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);
                serverClientService.saveTimeSeries(currency, cryptoCurrencySupplierWeeks, CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);


                // attendre deux minute
                Thread.sleep(1000 * 60 * 2);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });


    }

    // cette fonction doit recupper les donneés  au demarage une fois par jour

    public synchronized  void getTimeSeries() throws IOException, MissingRequiredQueryParameterException {

            timeSeriesDaily = serverClientService.getTimeSeries(CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);

            timeSeriesWeekly = serverClientService.getTimeSeries(CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);


    }



    private Supplier<Stream<CryptoCurrency>> getValidTimeSeries(int nbrMonths, List<CryptoCurrency> cryptoCurrenciesByDay) {
        return () -> cryptoCurrenciesByDay.stream().
                filter(valideCryptoCurrencyHistoByMonths(nbrMonths));
    }



    private List<CryptoCurrency> getCryptoCurrencies(String currency, CryptoCurrenciesFunction cryptoCurrenciesFunction) throws IOException, MissingRequiredQueryParameterException {
        CryptoCurrenciesResult cryptoCurrenciesResult = alphaVantageClient.getCryptoCurrencies(cryptoCurrenciesFunction, Market.USD, currency, OutputSize.COMPACT);
        List<CryptoCurrency> cryptoCurrencies = mapCryptoCurrenciesResult(cryptoCurrenciesResult);

        return cryptoCurrencies;
    }

    private Predicate<CryptoCurrency> valideCryptoCurrencyHistoByMonths(int nbrMonths) {
        Date startDate = Date.from(ZonedDateTime.now().minusMonths(nbrMonths).toInstant());

        return cryptoCurrenciesEntry -> cryptoCurrenciesEntry.getDate().after(startDate);
    }



    private List<CryptoCurrency> mapCryptoCurrenciesResult(CryptoCurrenciesResult cryptoCurrenciesResult) {
        List<CryptoCurrency> cryptoCurrencies = new ArrayList<>();
        cryptoCurrenciesResult.getCryptoCurrencies().forEach((date, cryptoCur) -> {

            CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoCur.getOpen(), cryptoCur.getHigh(), cryptoCur.getLow(), cryptoCur.getClose(), cryptoCur.getVolume(), cryptoCur.getMarketCup(), date);
            cryptoCurrencies.add(cryptoCurrency);

        });
        return cryptoCurrencies;
    }



}