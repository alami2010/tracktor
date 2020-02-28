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


    Class indicator;
    int length ;
    Predicate<Integer> predicate;
    BiPredicate<Num,Number> biPredicate;

}
