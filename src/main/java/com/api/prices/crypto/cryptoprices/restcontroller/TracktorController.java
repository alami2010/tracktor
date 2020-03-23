package com.api.prices.crypto.cryptoprices.restcontroller;

import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.service.data.ServerClientService;
import com.api.prices.crypto.cryptoprices.service.data.TimeSeriesService;
import com.api.prices.crypto.cryptoprices.service.trading.AnalyseService;
import com.api.prices.crypto.cryptoprices.service.trading.PriceService;
import com.api.prices.crypto.cryptoprices.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class TracktorController {

    @Autowired
    private PriceService priceService;
    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private TimeSeriesService timeSeriesService;

    @Autowired
    private ServerClientService serverClientService;


    @GetMapping(value = "alert")
    public ResponseEntity<?> getPrices() {
        analyseService.alterCurrentCurrency();
        return null;
    }

    @PostMapping(value = "monitoring")
    public ResponseEntity<?> enableAlertPrices() {
        analyseService.initMonitoringOfStats();
        return null;
    }

    @PostMapping(value = "redis")
    public ResponseEntity<?> testredis() {
        analyseService.redis();
        return null;
    }


    @PostMapping(value = "load_timeseries")
    public ResponseEntity<?> loadTimeSeries() throws IOException, MissingRequiredQueryParameterException {
        timeSeriesService.loadTimeSeries("BTC");
        return null;
    }


    @PostMapping(value = "get_timeseries")
    public ResponseEntity<?> getTimeSeries() throws IOException, MissingRequiredQueryParameterException {
        serverClientService.getTimeSeriesDaily();
        serverClientService.getTimeSeriesWeekly();

        return null;
    }

    @GetMapping(value = "somme")
    public @ResponseBody
    List<String> getSommes() {
        List<String> sommes = Utils.generateSomme(0);
        return sommes;
    }


}
