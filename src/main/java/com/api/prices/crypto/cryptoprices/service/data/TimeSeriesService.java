package com.api.prices.crypto.cryptoprices.service.data;

import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesResult;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrency;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.Market;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.entity.enums.AlphaVantageCurrency;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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


    // cette fonction doit tourné une seul fois par jour
    public void loadTimeSeries(String currencyFrom) {

        Runnable task = () -> {


            int nbrMonths = 1;

            Stream.of(getCurrenciesToLoad(currencyFrom)).forEach(alphaVantageCurrency -> {


                try {

                    loadOneCurrency(nbrMonths, alphaVantageCurrency.name());


                    Thread.sleep(1000 * 60 * 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                }


            });
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    public void loadOneTimeSeries(String currency) throws IOException, MissingRequiredQueryParameterException {

        int nbrMonths = 1;
        loadOneCurrency(nbrMonths, currency);
    }

    private void loadOneCurrency(int nbrMonths, String alphaVantageCurrency) throws IOException, MissingRequiredQueryParameterException {
        String currency = alphaVantageCurrency;
        System.out.println(currency);


        getTimeSeriesFromAndSaveInServerByInterval(currency, nbrMonths, CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);

        getTimeSeriesFromAndSaveInServerByInterval(currency, nbrMonths, CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);
    }

    private AlphaVantageCurrency[] getCurrenciesToLoad(String currencyFrom) {

        int indexCurrencyFrom = Arrays.asList(AlphaVantageCurrency.values()).indexOf(currencyFrom);
        return Arrays.copyOfRange(AlphaVantageCurrency.values(), indexCurrencyFrom, AlphaVantageCurrency.values().length);
    }

    private void getTimeSeriesFromAndSaveInServerByInterval(String currency, int nbrMonths, CryptoCurrenciesFunction digitalCurrency) throws IOException, MissingRequiredQueryParameterException {
        List<CryptoCurrency> cryptoCurrenciesBy = getCryptoCurrencies(currency, digitalCurrency);
        Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplier = getValidTimeSeries(nbrMonths, cryptoCurrenciesBy);
        serverClientService.saveTimeSeries(currency, cryptoCurrencySupplier, digitalCurrency);
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