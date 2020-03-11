package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.client.CoinRestClient;
import com.api.prices.crypto.cryptoprices.client.pojo.Currency;
import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformation;
import com.api.prices.crypto.cryptoprices.entity.CurrencyToTrack;
import com.api.prices.crypto.cryptoprices.entity.Signal;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceService {

    public static final int MULTIPLY_BIG_MARGE = 3;
    public static final double MUTIPLY_SMALL_MARGE = 1.3;
    private static Logger logger = LogManager.getLogger(PriceService.class);
    List<CurrencyToTrack> currencyToTracks = null;


    @Autowired
    private TrackService currencyTrackService;
    @Autowired
    private CoinRestClient pricesRestClient;
    @Autowired
    private AlertService alertService;


    public void initMonitoringOfPrice() {
        logger.info(" ===> Monitoring price <=== ");


        currencyTrackService.trackPriceChangeByRobot(currencyToTracks);
        String currencies = getCurrencyToTracks().stream().map(currencyToTrack -> currencyToTrack.getName()).sorted().collect(Collectors.joining(","));
        CurrencyInformation currencyInfo = pricesRestClient.getOneCurrenciesInfo(currencies);
        if (currencyInfo != null && currencyInfo.getData() != null) {

            double sumDouble1H = currencyInfo.getData().entrySet().stream().mapToDouble(currency -> currency.getValue().getQuote().getUSD().getPercent_change_1h()).sum();
            double sumDouble24H = currencyInfo.getData().entrySet().stream().mapToDouble(currency -> currency.getValue().getQuote().getUSD().getPercent_change_24h()).sum();
            double sumDouble7D = currencyInfo.getData().entrySet().stream().mapToDouble(currency -> currency.getValue().getQuote().getUSD().getPercent_change_7d()).sum();

            System.out.println("Somme 1h= " + sumDouble1H + " 24h= " + sumDouble24H + " 7D= " + sumDouble7D);
            currencyInfo.getData().entrySet().stream().forEach(currency -> checkPrice(currency));
        }
        currencyTrackService.trackPriceChangeByRobot(currencyToTracks);
    }

    // todo la date pour rechercher pas une seul fois , creationde class Supplier data from ovh
    private List<CurrencyToTrack> getCurrencyToTracks() {
        //if (currencyToTracks == null)
        currencyToTracks = pricesRestClient.getCurrencyToTrack();

        return currencyToTracks;
    }

    private void checkPrice(Map.Entry<String, Currency> currency) {
        CurrencyToTrack currencyToTrack = getCurrencyToTrackByKey(currencyToTracks, currency.getKey());
        double priceCurrency = currency.getValue().getQuote().getUSD().getPrice();

        checkPrice(currencyToTrack, priceCurrency, priceCurrency >= currencyToTrack.getMax(), Signal.SELL);
        checkPrice(currencyToTrack, priceCurrency, priceCurrency <= currencyToTrack.getMin(), Signal.BUY);

        displayConsole(currency, currencyToTrack, priceCurrency);

    }

    private void displayConsole(Map.Entry<String, Currency> currency, CurrencyToTrack currencyToTrack, double priceCurrency) {

        String leftAlignFormat = "| %-5s |%-16s | %-10s | %-10s | %-10s | %-10s | %-10s |%n";
        System.out.format(leftAlignFormat, StringUtils.capitalize(currencyToTrack.getName()), priceCurrency, currencyToTrack.getMin(), currencyToTrack.getMax(), currency.getValue().getQuote().getUSD().getPercent_change_1h(), currency.getValue().getQuote().getUSD().getPercent_change_24h(), currency.getValue().getQuote().getUSD().getPercent_change_7d());
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
            case SELL:
                marge = priceCurrency - currencyToTrack.getMax();
                currencyToTrack.setMax(priceCurrency + marge * MULTIPLY_BIG_MARGE);
                currencyToTrack.setMin(currencyToTrack.getMin() + marge * MUTIPLY_SMALL_MARGE);
                break;
            case BUY:
                marge = currencyToTrack.getMin() - priceCurrency;
                currencyToTrack.setMin(priceCurrency - marge * MULTIPLY_BIG_MARGE);
                currencyToTrack.setMax(currencyToTrack.getMax() - marge * MUTIPLY_SMALL_MARGE);
                break;
        }
        pricesRestClient.updateCurrency(currencyToTrack);
    }


}
