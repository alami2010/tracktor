package com.api.prices.crypto.cryptoprices.client.alphavantage.currencies;

import com.api.prices.crypto.cryptoprices.client.alphavantage.utils.AlphaVantageResultDeserializerHelper;
import com.api.prices.crypto.cryptoprices.client.alphavantage.utils.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static com.api.prices.crypto.cryptoprices.client.alphavantage.response.MetaData.META_DATA_RESPONSE_KEY;

/**
 * A custom deserializer for the CryptoCurrencies API response.
 * It is a custom deserializer due to the complex structure of the response.
 * Firstly, field names are numbered, and are numbered differently between different
 * requests, so the fields need to be sanitized before deserialization.
 * Secondly, the field name for the actual data returned by the API i.e. the non
 * meta data section, is a dynamic string that changes between requests.
 */
public class CryptoCurrenciesDeserializer extends JsonDeserializer<CryptoCurrenciesResult> {

  @Override
  public CryptoCurrenciesResult deserialize(
      com.fasterxml.jackson.core.JsonParser parser, DeserializationContext context)
      throws IOException {
    CryptoCurrenciesResult cryptoCurrenciesResult = new CryptoCurrenciesResult();
    ObjectCodec oc = parser.getCodec();
    JsonNode node = oc.readTree(parser);
    try {
      cryptoCurrenciesResult.setMetaData(AlphaVantageResultDeserializerHelper.getMetaData(node));
      cryptoCurrenciesResult.setCryptoCurrencies(getDateObjectMap(node));
    } catch (Throwable throwable) {
      System.out.println("Error when deserializing:");
      System.out.println(node.toString());
      throwable.printStackTrace();
    }
    return cryptoCurrenciesResult;
  }

  private Date parseDate(String dateStr)
      throws ParseException {
    Date date = DATE_PARSER.parse(dateStr);
    if (dateStr.length() > 10)
      date = DATE_TIME_PARSER.parse(dateStr);
    return date;
  }

  private Map<Date, CryptoCurrencies> getDateObjectMap(JsonNode node) {
    Map<Date, CryptoCurrencies> cryptoCurrenciesMap = new TreeMap<>();
    node.fields().forEachRemaining(nodeEntry -> {
      // ignore meta data, we want the time series data
      if (!nodeEntry.getKey().equals(META_DATA_RESPONSE_KEY)) {
        nodeEntry.getValue().fields().forEachRemaining(timeSeriesEntry -> {
          try {
            cryptoCurrenciesMap.put(
                parseDate(timeSeriesEntry.getKey()),
                JsonParser.toObject(
                    JsonParser.toJson(AlphaVantageResultDeserializerHelper.sanitizeNodeKeys(timeSeriesEntry.getValue())),
                    CryptoCurrencies.class
                )
            );
          }
          catch (IOException | ParseException e) {
            e.printStackTrace();
          }
        });
      }
    });
    return cryptoCurrenciesMap;
  }

  private static final SimpleDateFormat DATE_TIME_PARSER = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
  private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
}
