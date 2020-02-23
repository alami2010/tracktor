package com.api.prices.crypto.cryptoprices.client.alphavantage.currencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation of the data that is returned by the CryptoCurrencies endpoints
 * in the API.
 * The values that are returned for the adjusted CryptoCurrencies endpoints are
 * {@code Optional} typed.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoCurrencies {


    @JsonProperty("1b. open (USD)")
    private double open;

    @JsonProperty("2b. high (USD)")
    private double high;

    @JsonProperty("3b. low (USD)")
    private double low;

    @JsonProperty("4b. close (USD)")
    private double close;

    @JsonProperty("volume")
    private double volume;

    @JsonProperty("market cap (USD)")
    private Double marketCup = null;
}
