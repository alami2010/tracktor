package com.api.prices.crypto.cryptoprices.client.alphavantage.currencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representation of the data that is returned by the CryptoCurrencies endpoints
 * in the API.
 * The values that are returned for the adjusted CryptoCurrencies endpoints are
 * {@code Optional} typed.
 */
@Data
public class CryptoCurrency {

    public static  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    public CryptoCurrency(double open, double high, double low, double close, double volume, double marketCup, Date date) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.marketCup = marketCup;
        this.date = date;
    }

    private double open;

    private double high;

    private double low;

    private double close;

    private double volume;

    private double marketCup;

    private Date  date;


    @Override
    public String toString() {
        return open + "," + high + "," + low + "," + close + "," + volume + "," + marketCup+","+simpleDateFormat.format(date);
    }
}
