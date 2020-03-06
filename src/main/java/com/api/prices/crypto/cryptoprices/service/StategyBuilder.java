package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.entity.Decision;
import com.api.prices.crypto.cryptoprices.entity.Strategy;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;

import java.util.ArrayList;
import java.util.List;

public class StategyBuilder {


    public static List<Strategy> generateStrategies(double price) {
        List<Strategy> strategies = new ArrayList<>();

        // RSI
        strategies.add(new Strategy(RSIIndicator.class, 2, result -> result <= 5, null, Decision.BUY));
        strategies.add(new Strategy(RSIIndicator.class, 2, result -> result >= 95, null, Decision.SELL));

        strategies.add(new Strategy(RSIIndicator.class, 20, result -> result <= 30, null, Decision.BUY));
        strategies.add(new Strategy(RSIIndicator.class, 20, result -> result >= 70, null, Decision.SELL));


        // SMA
        strategies.add(new Strategy(RSIIndicator.class, 2, result -> result < price, null, Decision.BUY));
        strategies.add(new Strategy(SMAIndicator.class, 2, result -> result > price, null, Decision.SELL));


        //Stochastic
        //strategies.add(new Strategy(S, 2, result -> result > price, null, Decision.SELL));





        return strategies;
    }
}
