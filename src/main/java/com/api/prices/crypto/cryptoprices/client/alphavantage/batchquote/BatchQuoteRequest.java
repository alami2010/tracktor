package com.api.prices.crypto.cryptoprices.client.alphavantage.batchquote;

import com.api.prices.crypto.cryptoprices.client.alphavantage.request.APIRequest;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import lombok.Builder;
import lombok.Data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
public class BatchQuoteRequest implements APIRequest {

  @Override
  public String toQueryParameters()
      throws MissingRequiredQueryParameterException,
      InvalidSymbolLengthException {
    if (symbols == null)
      throw new MissingRequiredQueryParameterException("Symbols");
    if (symbols.length == 0 || symbols.length > 100)
      throw new InvalidSymbolLengthException(symbols.length);
    return "function=BATCH_QUOTES_US&symbols=" +
       Stream.of(symbols)
         .map(Object::toString)
         .collect(Collectors.joining(","));
  }

  private String[] symbols;
}
