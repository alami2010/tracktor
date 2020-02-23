package com.api.prices.crypto.cryptoprices.client.alphavantage.currencies;

import com.api.prices.crypto.cryptoprices.client.alphavantage.response.MetaData;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * A representation of the API response for the CryptoCurrencies endpoints.
 */
@Data
public class CryptoCurrenciesResult {
  private MetaData metaData;
  private Map<Date, CryptoCurrencies> cryptoCurrencies;
}
