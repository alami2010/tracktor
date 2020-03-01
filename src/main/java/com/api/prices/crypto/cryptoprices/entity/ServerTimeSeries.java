package com.api.prices.crypto.cryptoprices.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ServerTimeSeries {

    String symbol;
    @SerializedName("time_series")
    String timeSeries;
    @SerializedName("update_date")
    String updateDate;
}
