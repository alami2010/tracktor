package com.api.prices.crypto.cryptoprices.restcontroller;

import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.service.AnalyseService;
import com.api.prices.crypto.cryptoprices.service.TimeSeriesService;
import com.api.prices.crypto.cryptoprices.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class TracktorController {

    @Autowired
    private PriceService priceService;
    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private TimeSeriesService timeSeriesService;




    @GetMapping(value = "alert")
    public ResponseEntity<?> getPrices() {
        analyseService.alterCurrentCurrency();
        return null;
    }

    @PostMapping(value = "monitoring")
    public ResponseEntity<?> enableAlertPrices() {
        analyseService.initMonitoringOfStats();
        return  null;
    }

    @PostMapping(value = "redis")
    public ResponseEntity<?> testredis() {
        analyseService.redis();
        return  null;
    }


    @PostMapping(value = "load_timeseries")
    public ResponseEntity<?>  loadTimeSeries() throws IOException, MissingRequiredQueryParameterException {
        timeSeriesService.loadTimeSeries();
        return  null;
    }



    @PostMapping(value = "get_timeseries")
    public ResponseEntity<?> getTimeSeries() throws IOException, MissingRequiredQueryParameterException {
        timeSeriesService.getTimeSeries();
        return  null;
    }
}
