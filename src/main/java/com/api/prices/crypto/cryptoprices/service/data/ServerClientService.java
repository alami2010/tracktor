package com.api.prices.crypto.cryptoprices.service.data;

import com.api.prices.crypto.cryptoprices.client.CoinRestClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrency;
import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformation;
import com.api.prices.crypto.cryptoprices.entity.CurrencyToTrack;
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


    List<CurrencyToTrack> currencyToTracks = null;
    private Map<String, List<CryptoCurrency>> timeSeriesDaily;
    private Map<String, List<CryptoCurrency>> timeSeriesWeekly;

    public List<CurrencyToTrack> getCurrencyToTracks() {
        //if (currencyToTracks == null)
        currencyToTracks = pricesRestClient.getCurrencyToTrack();

        return currencyToTracks;
    }


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


    // todo test for time to be one time a day

    public Map<String, List<CryptoCurrency>> getTimeSeriesDaily() {
        timeSeriesDaily = timeSeriesDaily != null ? timeSeriesDaily : getTimeSeries(CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY);
        return timeSeriesDaily;
    }

    public void setTimeSeriesDaily(Map<String, List<CryptoCurrency>> timeSeriesDaily) {
        this.timeSeriesDaily = timeSeriesDaily;
    }

    public Map<String, List<CryptoCurrency>> getTimeSeriesWeekly() {
        timeSeriesWeekly = timeSeriesWeekly != null ? timeSeriesWeekly : getTimeSeries(CryptoCurrenciesFunction.DIGITAL_CURRENCY_WEEKLY);
        return timeSeriesWeekly;
    }

    public void setTimeSeriesWeekly(Map<String, List<CryptoCurrency>> timeSeriesWeekly) {
        this.timeSeriesWeekly = timeSeriesWeekly;
    }

    public CurrencyInformation getOneCurrenciesInfo(String currencies) {
        return pricesRestClient.getOneCurrenciesInfo(currencies);
    }

    public void updateCurrency(CurrencyToTrack currencyToTrack) {
        pricesRestClient.updateCurrency(currencyToTrack);
    }
}