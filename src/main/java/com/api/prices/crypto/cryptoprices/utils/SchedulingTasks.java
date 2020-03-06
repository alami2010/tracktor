package com.api.prices.crypto.cryptoprices.utils;

import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.service.AnalyseService;
import com.api.prices.crypto.cryptoprices.service.PriceService;
import com.api.prices.crypto.cryptoprices.service.TimeSeriesService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


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
    private TimeSeriesService timeSeriesService;


    @Scheduled(fixedRate = MINUTE * 5)
    public void reportPrice() {

        logger.info("reportPrice");
        priceService.initMonitoringOfPrice();

    }

    @Scheduled(fixedRate = HOUR * 6)
    public void reportStats() {

        logger.info("reportStats");
        analyseService.initMonitoringOfStats();
        logger.info("reportStats End");


    }


    //@Scheduled(fixedRate = MINUTE * 1)

    @Scheduled(cron = "0 01 00 * * ?")
    public void loadTimeSeries() {
        logger.info("loadTimeSeries ");

        timeSeriesService.loadTimeSeries();
        logger.info("loadTimeSeries end ");


    }


    @Scheduled(cron = "0 4 17 * * ?")
    public void getTimeSeries() throws IOException, MissingRequiredQueryParameterException {
        logger.info("getTimeSeries ");

        timeSeriesService.getTimeSeries();

        logger.info("getTimeSeries  End ");


    }


}
