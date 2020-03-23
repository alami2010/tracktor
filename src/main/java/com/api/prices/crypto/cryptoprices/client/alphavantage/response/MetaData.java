package com.api.prices.crypto.cryptoprices.client.alphavantage.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Part of the Response from the API is a meta data section.
 * This java class represents the meta data in the response.
 */
@Data
public class MetaData {
  @JsonProperty("Information")
  private String information;
  @JsonProperty("Digital Currency Code")
  private String symbol;
  @JsonProperty("Digital Currency Name")
  private String symbolName;
  @JsonProperty("Market Code")
  private String marketCode;
  @JsonProperty("Market Name")
  private String marketName;
  @JsonProperty("Last Refreshed")
  private String lastRefreshed;
  @JsonProperty("Time Zone")
  private String timeZone;


  public static final String META_DATA_RESPONSE_KEY = "Meta Data";
}
