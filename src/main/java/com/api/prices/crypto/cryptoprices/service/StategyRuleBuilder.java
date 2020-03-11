package com.api.prices.crypto.cryptoprices.service;

import com.api.prices.crypto.cryptoprices.entity.Signal;
import com.api.prices.crypto.cryptoprices.entity.StrategyRule;
import com.api.prices.crypto.cryptoprices.entity.StrategyRule.RuleName;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorDIndicator;

import java.util.ArrayList;
import java.util.List;

public class StategyRuleBuilder {


    public static List<StrategyRule> generateRuleStrategies(double price) {
        List<StrategyRule> rules = new ArrayList<>();

        // RSI
        rules.add(new StrategyRule(RuleName.RSI_2_DOWN_5, RSIIndicator.class, 2, result -> result <= 5, Signal.BUY));
        rules.add(new StrategyRule(RuleName.RSI_2_UP_95, RSIIndicator.class, 2, result -> result >= 95, Signal.SELL));
        rules.add(new StrategyRule(RuleName.RSI_20_DOWN_30, RSIIndicator.class, 20, result -> result <= 30, Signal.BUY));
        rules.add(new StrategyRule(RuleName.RSI_20_UP_70, RSIIndicator.class, 20, result -> result >= 70, Signal.SELL));


        // SMA
        rules.add(new StrategyRule(RuleName.SMA_5_DOWN_PRICE, SMAIndicator.class, 5, result -> result < price, Signal.BUY));
        rules.add(new StrategyRule(RuleName.SMA_5_UP_PRICE, SMAIndicator.class, 5, result -> result > price, Signal.SELL));
        rules.add(new StrategyRule(RuleName.SMA_20_DOWN_PRICE, SMAIndicator.class, 20, result -> result < price, Signal.BUY));
        rules.add(new StrategyRule(RuleName.SMA_20_UP_PRICE, SMAIndicator.class, 20, result -> result > price, Signal.SELL));


        //  Stochastic https://youtu.be/SrCENnDVHRk?t=524
        rules.add(new StrategyRule(StrategyRule.RuleName.STOCHASTIC_14_DOWN_20, StochasticOscillatorDIndicator.class, 14, result -> result < 20, Signal.SELL));
        rules.add(new StrategyRule(StrategyRule.RuleName.STOCHASTIC_14_UP_80, StochasticOscillatorDIndicator.class, 14, result -> result > 80, Signal.SELL));


        return rules;
    }
}
