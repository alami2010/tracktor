package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.*;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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


                getTimeSeriesFromAndSaveInServerByInterval(currency, nbrMonths, CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);

                getTimeSeriesFromAndSaveInServerByInterval(currency, nbrMonths, CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);


                // attendre deux minute
                Thread.sleep(1000 * 60 * 2);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });


    }

    private void getTimeSeriesFromAndSaveInServerByInterval(String currency, int nbrMonths, CryptoCurrenciesFunction digitalCurrency) throws IOException, MissingRequiredQueryParameterException {
        List<CryptoCurrency> cryptoCurrenciesBy = getCryptoCurrencies(currency, digitalCurrency);
        Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplier = getValidTimeSeries(nbrMonths, cryptoCurrenciesBy);
        serverClientService.saveTimeSeries(currency, cryptoCurrencySupplier, digitalCurrency);
    }

    // cette fonction doit recupper les donneés  au demarage une fois par jour

    public synchronized  void getTimeSeries() throws IOException, MissingRequiredQueryParameterException {

            timeSeriesDaily = serverClientService.getTimeSeries(CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);

            timeSeriesWeekly = serverClientService.getTimeSeries(CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);


    }



    private Supplier<Stream<CryptoCurrency>> getValidTimeSeries(int nbrMonths, List<CryptoCurrency> cryptoCurrenciesByDay) {
        return () -> cryptoCurrenciesByDay.stream().
                filter(validCryptCurrencyHistByMonths(nbrMonths));
    }



    private List<CryptoCurrency> getCryptoCurrencies(String currency, CryptoCurrenciesFunction cryptoCurrenciesFunction) throws IOException, MissingRequiredQueryParameterException {
        CryptoCurrenciesResult cryptoCurrenciesResult = alphaVantageClient.getCryptoCurrencies(cryptoCurrenciesFunction, Market.USD, currency, OutputSize.COMPACT);

        return mapCryptCurrenciesResult(cryptoCurrenciesResult);
    }

    private Predicate<CryptoCurrency> validCryptCurrencyHistByMonths(int nbrMonths) {
        Date startDate = Date.from(ZonedDateTime.now().minusMonths(nbrMonths).toInstant());

        return cryptoCurrenciesEntry -> cryptoCurrenciesEntry.getDate().after(startDate);
    }



    private List<CryptoCurrency> mapCryptCurrenciesResult(CryptoCurrenciesResult cryptoCurrenciesResult) {
        List<CryptoCurrency> cryptoCurrencies = new ArrayList<>();
        cryptoCurrenciesResult.getCryptoCurrencies().forEach((date, cryptoCur) -> {

            CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoCur.getOpen(), cryptoCur.getHigh(), cryptoCur.getLow(), cryptoCur.getClose(), cryptoCur.getVolume(), cryptoCur.getMarketCup(), date);
            cryptoCurrencies.add(cryptoCurrency);

        });
        return cryptoCurrencies;
    }



}