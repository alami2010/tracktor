package com.api.prices.crypto.cryptoprices.client.alphavantage.currencies;

/**
 * Available parameters for the query parameter function in the CryptoCurrencies
 * endpoints.
 */
public enum CryptoCurrenciesFunction {
  DIGITAL_CURRENCY_DAILY("DIGITAL_CURRENCY_DAILY"),
  DIGITAL_CURRENCY_WEEKLY("DIGITAL_CURRENCY_WEEKLY"),
  DIGITAL_CURRENCY_MONTHLY("DIGITAL_CURRENCY_MONTHLY");


  CryptoCurrenciesFunction(String queryParameterKey) {
    this.queryParameterKey = queryParameterKey;
  }

  public String getQueryParameterKey() {
    return queryParameterKey;
  }

  private String queryParameterKey;
}
