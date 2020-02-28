package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.CoinRestClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrency;
import com.api.prices.crypto.cryptoprices.entity.ServerTimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY;
import static com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY;

@Service
public class ServerClientService {


    @Autowired
    private CoinRestClient pricesRestClient;


    public void saveTimeSeries(String currency, Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplier, CryptoCurrenciesFunction digitalCurrency) {

        String cryptoCurrenciesJson = cryptoCurrencySupplier.get().map(cryptoCurrency -> cryptoCurrency.toString()).collect(Collectors.joining(";"));

        switch (digitalCurrency) {
            case DIGITAL_CURRENCY_DAILY:
                pricesRestClient.saveTimeSeries(currency, cryptoCurrenciesJson, DIGITAL_CURRENCY_DAILY);

                break;
            case DIGITAL_CURRENCY_WEEKLY:
                pricesRestClient.saveTimeSeries(currency, cryptoCurrenciesJson, DIGITAL_CURRENCY_WEEKLY);

                break;
        }


        System.out.println(digitalCurrency + " " + cryptoCurrenciesJson);

    }


    //todo CryptoCurrency is object otherwise serverTimeSeries is a list of  :)
    public Supplier<Stream<CryptoCurrency>> getTimeSeries(CryptoCurrenciesFunction digitalCurrency) {


        List<ServerTimeSeries> serverTimeSeries = pricesRestClient.geTimeSeries(digitalCurrency.name());

        Supplier<Stream<CryptoCurrency>> timeSeriesSupplier = () -> serverTimeSeries.stream().map(serverTimeSeries1 -> mapServerTimeSeriesToCryptoCurrency(serverTimeSeries1));


        return timeSeriesSupplier;
    }

    private CryptoCurrency mapServerTimeSeriesToCryptoCurrency(ServerTimeSeries serverTimeSeries1) {
        return null;
    }
}