package com.api.prices.crypto.cryptoprices.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ServerTimeSeries {

    String symbol;
    @JsonProperty("time_series")
    String timeSeries;
    @JsonProperty("update_date")
    String updateDate;
}
