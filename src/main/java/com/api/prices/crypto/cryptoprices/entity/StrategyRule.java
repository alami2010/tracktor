package com.api.prices.crypto.cryptoprices.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Data
@Builder
@AllArgsConstructor
public class StrategyRule {

    public enum RuleName {
        RSI_2_DOWN_5,
        RSI_2_UP_95,
        RSI_20_DOWN_30,
        RSI_20_UP_70,
        SMA_5_DOWN_PRICE,
        SMA_5_UP_PRICE,
        SMA_20_DOWN_PRICE,
        SMA_20_UP_PRICE,
        Stochastic_14_DOWN_20,
        Stochastic_14_UP_80

    }



    private RuleName ruleName;
    private Class indicator;
    private int length ;
    private Predicate<Integer> predicate;
    private Decision  decision;

}
