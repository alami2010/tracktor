package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.CoinMarketPlaceClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.*;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.client.pojo.Currency;
import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformation;
import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformationStats;
import com.api.prices.crypto.cryptoprices.entity.BinanceCurrency;
import com.api.prices.crypto.cryptoprices.entity.CurrencyToTrack;
import com.api.prices.crypto.cryptoprices.entity.TableHtml;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicatorTechnicalService {
    private static Logger logger = LogManager.getLogger(IndicatorTechnicalService.class);

    AlphaVantageCurrency[] alphaVantageCurrencies = AlphaVantageCurrency.values();
    int indexCurrentCurrency = 0;

    List<String> enumA = Arrays.stream(alphaVantageCurrencies).map(Enum::name).collect(Collectors.toList());


    @Autowired
    private IAlphaVantageClient alphaVantageClient;

    public void getNewCurrentCurrencyAndAnalyse() throws IOException, MissingRequiredQueryParameterException {


        String currency = getCurrentCurrency();


        try {
            CryptoCurrenciesResult btc = alphaVantageClient.getCryptoCurrencies(CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY, Market.USD, currency, OutputSize.COMPACT);
            System.out.println(btc);
            double somme = 0;
            TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName("AXP_Stock").build();
            ;


            Date startDate = Date.from(ZonedDateTime.now().minusMonths(1).toInstant());

            btc.getCryptoCurrencies().entrySet().stream().
                    filter(cryptoCurrenciesEntry -> cryptoCurrenciesEntry.getKey().after(startDate)).
                    forEach(cryptoCurrenciesEntry -> {

                        Date dateHistorian = cryptoCurrenciesEntry.getKey();
                        CryptoCurrencies cryptoCurrencyHisotrian = cryptoCurrenciesEntry.getValue();

                        series.addBar(ZonedDateTime.ofInstant(dateHistorian.toInstant(),
                                ZoneId.systemDefault()), cryptoCurrencyHisotrian.getOpen(), cryptoCurrencyHisotrian.getHigh(), cryptoCurrencyHisotrian.getLow(), cryptoCurrencyHisotrian.getClose(), cryptoCurrencyHisotrian.getVolume());

                        System.out.println(cryptoCurrencyHisotrian);

                    });


            ClosePriceIndicator closePrice = new ClosePriceIndicator(series);


            // Getting the simple moving average (SMA) of the close price over the last 5 ticks
            SMAIndicator shortSma = new SMAIndicator(closePrice, 2);
// Here is the 5-ticks-SMA value at the 42nd index
            System.out.println("5-ticks-SMA value at the 42nd index: " + shortSma.getValue(2));

// Getting a longer SMA (e.g. over the 30 last ticks)
            SMAIndicator longSma = new SMAIndicator(closePrice, 2);
            System.out.println("30-ticks-SMA value at the 42nd index: " + longSma.getValue(28));
            System.out.println(somme / 31);
        } catch (Exception e) {
            System.out.println("ERROR " + currency +" "+ e.getMessage());

            enumA.remove(currency);
            System.out.println(enumA);
        }


        // String makeAPICall = coinMarketPlaceClient.makeAPICall(CoinMarketPlaceClient.UrlManager.TEST, null, null);
        System.out.println("hhelooo");



    }

    private String getCurrentCurrency() {

        majIndexCurrentCurrency();
        return AlphaVantageCurrency.BTC.name();

        //    return alphaVantageCurrencies[indexCurrentCurrency].name();
    }

    private void majIndexCurrentCurrency() {

        indexCurrentCurrency = (indexCurrentCurrency + 1) % alphaVantageCurrencies.length;

    }


}