package com.api.prices.crypto.cryptoprices.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.ta4j.core.num.Num;

@Data
@Builder
@AllArgsConstructor
public class StrategyRuleResult {

    private Num result;
    private String ruleName;
    private Signal signal;

}
