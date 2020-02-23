package com.api.prices.crypto.cryptoprices.utils;

import com.api.prices.crypto.cryptoprices.client.CoinMarketPlaceClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.batchquote.InvalidSymbolLengthException;
import com.api.prices.crypto.cryptoprices.client.alphavantage.configuration.IAlphaVantageClient;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.AlphaVantageCurrency;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesResult;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.Market;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencyexchange.CurrencyExchange;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.entity.BinanceCurrency;
import com.api.prices.crypto.cryptoprices.service.AnalyseService;
import com.api.prices.crypto.cryptoprices.service.IndicatorTechnicalService;
import com.api.prices.crypto.cryptoprices.service.PriceService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class SchedulingTasks {

    private static Logger logger = LogManager.getLogger(SchedulingTasks.class);
    private final long SEGUNDO = 1000;
    private final long MINUTE = SEGUNDO * 60;
    private final long HOUR = MINUTE * 60;
    @Autowired
    private PriceService priceService;

    @Autowired
    private AnalyseService analyseService;


    @Autowired
    private CoinMarketPlaceClient coinMarketPlaceClient;

    @Autowired
    private IndicatorTechnicalService indicatorTechnicalService;




   // @Scheduled(fixedRate = MINUTE * 5)
    public void reportPrice() {

        logger.info("reportPrice");
        priceService.initMonitoringOfPrice();
    }

    //@Scheduled(fixedRate = HOUR * 6)
    public void reportStats() {

         logger.info("reportStats");
         analyseService.initMonitoringOfStats();

    }



    @Scheduled(fixedRate = MINUTE * 20)
    public void historiqueCurency() throws IOException, MissingRequiredQueryParameterException, InvalidSymbolLengthException, URISyntaxException {
        logger.info("getNewCurrentCurrencyAndAnalyse ");

        indicatorTechnicalService.getNewCurrentCurrencyAndAnalyse();



    }
    
    

}
