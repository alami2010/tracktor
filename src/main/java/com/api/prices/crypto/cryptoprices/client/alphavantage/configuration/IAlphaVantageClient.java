package com.api.prices.crypto.cryptoprices.client.alphavantage.configuration;

import com.api.prices.crypto.cryptoprices.client.alphavantage.batchquote.BatchQuoteResult;
import com.api.prices.crypto.cryptoprices.client.alphavantage.batchquote.InvalidSymbolLengthException;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.CryptoCurrenciesResult;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencies.Market;
import com.api.prices.crypto.cryptoprices.client.alphavantage.currencyexchange.CurrencyExchange;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.IntradayInterval;
import com.api.prices.crypto.cryptoprices.client.alphavantage.request.OutputSize;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.MissingRequiredQueryParameterException;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.TimeSeriesFunction;
import com.api.prices.crypto.cryptoprices.client.alphavantage.timeseries.TimeSeriesResult;

import java.io.IOException;

public interface IAlphaVantageClient {

    /**
     * Get the Intraday stock data for a single stock.
     *
     * @param intradayInterval The interval between stock quotes.
     * @param symbol           The stock to get the data for.
     * @return The Intraday API response.
     */
    TimeSeriesResult getTimeSeries(
            IntradayInterval intradayInterval,
            String symbol
    )
            throws IOException, MissingRequiredQueryParameterException;

    /**
     * Get the Intraday stock data for a single stock.
     *
     * @param intradayInterval The interval between stock quotes.
     * @param symbol           The stock to get the data for.
     * @param outputSize       The output size of either Compact or Full.
     * @return The Intraday API response.
     */
    TimeSeriesResult getTimeSeries(
            IntradayInterval intradayInterval,
            String symbol,
            OutputSize outputSize
    )
            throws IOException, MissingRequiredQueryParameterException;

    /**
     * Request the CryptoCurrencies Alpha Vantage API for a specific function and symbol.
     *
     * @param timeSeriesFunction The function for the CryptoCurrencies request.
     * @param symbol             The stock to get the data for.
     * @return The CryptoCurrencies API response.
     */
    TimeSeriesResult getTimeSeries(
            TimeSeriesFunction timeSeriesFunction,
            String symbol
    )
            throws IOException, MissingRequiredQueryParameterException;

    /**
     * Request the CryptoCurrencies Alpha Vantage API for a specific function, symbol and
     * output size.
     *
     * @param timeSeriesFunction The function for the CryptoCurrencies request.
     * @param symbol             The stock to get the data for.
     * @param outputSize         The output size of either Compact or Full.
     * @return The CryptoCurrencies API response.
     */
    TimeSeriesResult getTimeSeries(
            TimeSeriesFunction timeSeriesFunction,
            String symbol,
            OutputSize outputSize
    )
            throws IOException, MissingRequiredQueryParameterException;

    /**
     * Request the BatchQuote API for a quote on a selection of specific Symbols.
     *
     * @param symbols The Symbols to get a quote.
     * @return The quotes for the symbols requested.
     */
    BatchQuoteResult getBatchQuote(String... symbols)
            throws MissingRequiredQueryParameterException,
            InvalidSymbolLengthException, IOException;

    /**
     * Request a currency exchange rate from one currency to another.
     *
     * @param fromCurrency The from currency in the exchange rate.
     * @param toCurrency   The to currency in the exchange rate.
     * @return The quote for the currency exchange.
     */
    CurrencyExchange getCurrencyExchange(String fromCurrency, String toCurrency)
            throws MissingRequiredQueryParameterException,
            InvalidSymbolLengthException, IOException;


    public CryptoCurrenciesResult getCryptoCurrencies(CryptoCurrenciesFunction cryptoCurrenciesFunction,
                                                      Market market, String symbol, OutputSize outputSize
    ) throws IOException, MissingRequiredQueryParameterException;

}
