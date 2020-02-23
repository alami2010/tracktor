package com.api.prices.crypto.cryptoprices.client.alphavantage.currencies;

import com.api.prices.crypto.cryptoprices.client.alphavantage.request.APIRequest;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import lombok.Builder;

/**
 * A wrapper class for the available query parameters for the CryptoCurrencies
 * endpoints of the API.
 */
@Builder
public class CryptoCurrenciesRequest implements APIRequest {

  /**
   * Convert all the selected query parameters to a query parameter string
   * to be used the in the API request.
   * @return A Query parameter string.
   */
  public String toQueryParameters()
      throws MissingRequiredQueryParameterException {
    if (cryptoCurrenciesFunction == null)
      throw new MissingRequiredQueryParameterException("CryptoCurrenciesFunction");
    if (symbol == null)
      throw new MissingRequiredQueryParameterException("Symbol");


    StringBuilder builder = new StringBuilder();
    builder
        .append("function=")
        .append(cryptoCurrenciesFunction.getQueryParameterKey());
    builder
        .append("&symbol=")
        .append(symbol);
    if (outputSize != null) {
      builder
          .append("&outputsize=")
          .append(outputSize);
    }
    if (market != null) {
      builder
          .append("&market=")
          .append(market.toString());
    }
    return builder.toString();
  }

  private Market market;
  private CryptoCurrenciesFunction cryptoCurrenciesFunction;
  private String symbol;
  private OutputSize outputSize;
}
