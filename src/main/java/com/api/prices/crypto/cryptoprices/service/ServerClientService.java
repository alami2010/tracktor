package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.CoinRestClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrency;
import com.api.prices.crypto.cryptoprices.entity.ServerTimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ServerClientService {


    @Autowired
    private CoinRestClient pricesRestClient;


    public void saveTimeSeries(String currency, Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplier, CryptoCurrenciesFunction digitalCurrency) {

        String cryptoCurrenciesJson = cryptoCurrencySupplier.get().map(cryptoCurrency -> cryptoCurrency.toString()).collect(Collectors.joining(";"));
        pricesRestClient.saveTimeSeries(currency, cryptoCurrenciesJson, digitalCurrency);

        System.out.println(digitalCurrency + " " + cryptoCurrenciesJson);

    }


    public Map<String,List<CryptoCurrency>> getTimeSeries(CryptoCurrenciesFunction digitalCurrency) {
        Map<String,List<CryptoCurrency>> mapServerTimeSeries = new HashMap<>();


        List<ServerTimeSeries> serverTimeSeriesList = pricesRestClient.geTimeSeries(digitalCurrency.name());

        serverTimeSeriesList.stream().forEach(serverTimeSeries -> mapServerTimeSeriesToCryptoCurrency(serverTimeSeries,mapServerTimeSeries));


        return mapServerTimeSeries;
    }

    private void mapServerTimeSeriesToCryptoCurrency(ServerTimeSeries serverTimeSeries , Map<String,List<CryptoCurrency>> listMap) {
        String[] timeSeries = serverTimeSeries.getTimeSeries().split(";");
        List<CryptoCurrency> cryptoCurrencies = Stream.of(timeSeries).map(timeSerie -> {
            String[] timeSeriesArray = timeSerie.split(",");
            try {
                return new CryptoCurrency(Double.valueOf(timeSeriesArray[0]), Double.valueOf(timeSeriesArray[1]),
                        Double.valueOf(timeSeriesArray[2]), Double.valueOf(timeSeriesArray[3]), Double.valueOf(timeSeriesArray[4]),
                        Double.valueOf(timeSeriesArray[5]), CryptoCurrency.simpleDateFormat.parse(timeSeriesArray[6]));
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        if(cryptoCurrencies != null )
            listMap.put(serverTimeSeries.getSymbol(),cryptoCurrencies);

    }
}