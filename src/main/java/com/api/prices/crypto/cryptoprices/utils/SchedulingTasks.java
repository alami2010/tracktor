package com.api.prices.crypto.cryptoprices.utils;

import com.api.prices.crypto.cryptoprices.service.data.TimeSeriesService;
import com.api.prices.crypto.cryptoprices.service.trading.AnalyseService;
import com.api.prices.crypto.cryptoprices.service.trading.PriceService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


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


    @Scheduled(fixedRate = MINUTE * 3)
    public void reportPrice() {

        priceService.initMonitoringOfPrice();

    }

    //@Scheduled(fixedRate = HOUR * 4)
    public void reportStats() {

        // todo more analysis
        logger.info("reportStats");
        analyseService.initMonitoringOfStats();
        logger.info("reportStats End");


    }


    //@Scheduled(fixedRate = MINUTE * 1)

    @Scheduled(cron = "0 01 00 * * ?")
    public void loadTimeSeries() {
        logger.info("loadTimeSeries ");

        timeSeriesService.loadTimeSeries("BTC");
        logger.info("loadTimeSeries end ");


    }





}
