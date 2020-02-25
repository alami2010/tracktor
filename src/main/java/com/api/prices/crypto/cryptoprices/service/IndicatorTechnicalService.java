package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.*;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class IndicatorTechnicalService {
    private static Logger logger = LogManager.getLogger(IndicatorTechnicalService.class);

    AlphaVantageCurrency[] alphaVantageCurrencies = AlphaVantageCurrency.values();
    int indexCurrentCurrency = 0;


    @Autowired
    private IAlphaVantageClient alphaVantageClient;

    public void getNewCurrentCurrencyAndAnalyse() throws IOException, MissingRequiredQueryParameterException {


        String currency = getCurrentCurrency();
        int nbrMonths = 1 ;

        try {


            List<CryptoCurrency> cryptoCurrencies = getCryptoCurrencies(currency);

            Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplier = () -> cryptoCurrencies.stream().
                    filter(valideCryptoCurrencyHistoByMonths(nbrMonths));



            TimeSeries series = new BaseTimeSeries.SeriesBuilder().withName("AXP_Stock").build();
            populateBars(series, cryptoCurrencySupplier);
            ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

            int countBars = (int) cryptoCurrencySupplier.get().count();


            calculateRSI( closePrice, 5, countBars);
            calculateRSI( closePrice, 10, countBars);
            calculateRSI( closePrice, 20, countBars);

            calculateSMA( closePrice, 5, countBars);
            calculateSMA( closePrice, 10, countBars);
            calculateSMA( closePrice, 20, countBars);


            calculateEMA( closePrice, 5, countBars);
            calculateEMA( closePrice, 10, countBars);
            calculateEMA( closePrice, 20, countBars);



            calculate( closePrice,RSIIndicator.class, 20, countBars);


        } catch (Exception e) {
            System.out.println("ERROR " + currency + " " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void calculateRSI(ClosePriceIndicator closePrice, int dayMovingAverrage,int nbrBars) {
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice,dayMovingAverrage);
        Num result = rsiIndicator.getValue(nbrBars - 1);
        System.out.println(" RSIIndicator  " + dayMovingAverrage + " jours " + result);
    }

    private Num calculateSMA(ClosePriceIndicator closePrice, int dayMovingAverrage,int nbrBars) {
        SMAIndicator shortSma = new SMAIndicator(closePrice, dayMovingAverrage);
        Num result = shortSma.getValue(nbrBars - 1);
        System.out.println(" SMA  " + dayMovingAverrage + " jours " + result);
        return result;
    }



    private Num calculate(ClosePriceIndicator closePrice, Class  x , int dayMovingAverrage, int nbrBars) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] allConstructors = x.getDeclaredConstructors();


        Indicator shortSma =(Indicator) x.getConstructor().newInstance(closePrice,dayMovingAverrage);
        Num result =(Num) shortSma.getValue(nbrBars - 1);
        System.out.println(" SMA  " + dayMovingAverrage + " jours " + result);
        return result;
    }



    private Num calculateEMA(ClosePriceIndicator closePrice, int dayMovingAverrage,int nbrBars) {
        EMAIndicator shortSma = new EMAIndicator(closePrice, dayMovingAverrage);
        Num result = shortSma.getValue(nbrBars - 1);
        System.out.println(" EMA  " + dayMovingAverrage + " jours " + result);
        return result;
    }

    private List<CryptoCurrency> getCryptoCurrencies(String currency) throws IOException, MissingRequiredQueryParameterException {
        CryptoCurrenciesResult cryptoCurrenciesResult = alphaVantageClient.getCryptoCurrencies(CryptoCurrenciesFunction.DIGITAL_CURRENCY_DAILY, Market.USD, currency, OutputSize.COMPACT);
        List<CryptoCurrency> cryptoCurrencies = mapCryptoCurrenciesResult(cryptoCurrenciesResult);

        return cryptoCurrencies;
    }

    private Predicate<CryptoCurrency> valideCryptoCurrencyHistoByMonths(int nbrMonths) {
        Date startDate = Date.from(ZonedDateTime.now().minusMonths(nbrMonths).toInstant());

        return cryptoCurrenciesEntry -> cryptoCurrenciesEntry.getDate().after(startDate);
    }

    private void populateBars(TimeSeries series, Supplier<Stream<CryptoCurrency>> cryptoCurrencySupplier) {
        cryptoCurrencySupplier.get().forEach(cryptoCurrencyHisotrian -> {


            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(cryptoCurrencyHisotrian.getDate().toInstant(), ZoneId.systemDefault());
            series.addBar(zonedDateTime, cryptoCurrencyHisotrian.getOpen(), cryptoCurrencyHisotrian.getHigh(), cryptoCurrencyHisotrian.getLow(), cryptoCurrencyHisotrian.getClose(), cryptoCurrencyHisotrian.getVolume());

            System.out.println(cryptoCurrencyHisotrian);

        });
    }

    private List<CryptoCurrency> mapCryptoCurrenciesResult(CryptoCurrenciesResult cryptoCurrenciesResult) {
        List<CryptoCurrency> cryptoCurrencies = new ArrayList<>();
        cryptoCurrenciesResult.getCryptoCurrencies().forEach((date, cryptoCur) -> {

            CryptoCurrency cryptoCurrency = new CryptoCurrency(cryptoCur.getOpen(), cryptoCur.getHigh(), cryptoCur.getLow(), cryptoCur.getClose(), cryptoCur.getVolume(), cryptoCur.getMarketCup(), date);
            cryptoCurrencies.add(cryptoCurrency);

        });
        return cryptoCurrencies;
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