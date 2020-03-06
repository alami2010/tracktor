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
public class Strategy {


    private Class indicator;
    private int length ;
    private Predicate<Integer> predicate;
    private BiPredicate<Num,Number> biPredicate;
    private Decision  decision;

}
