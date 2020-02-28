package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.entity.Strategy;
import org.ta4j.core.indicators.RSIIndicator;

import java.util.ArrayList;
import java.util.List;

public class StategyBuilder {



    public static List<Strategy> getStrategies() {
        List<Strategy> strategies = new ArrayList<>();
        strategies.add(new Strategy(RSIIndicator.class, 2  , result -> result <= 5 || result >= 95,null));
        strategies.add(new Strategy(RSIIndicator.class, 20 , result -> result <= 30 || result >= 70,null));
        return strategies;
    }
}
