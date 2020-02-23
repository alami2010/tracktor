package com.api.prices.crypto.cryptoprices.client;


import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformation;
import com.api.prices.crypto.cryptoprices.client.pojo.CurrencyInformationStats;
import com.api.prices.crypto.cryptoprices.entity.CurrencyToTrack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.api.prices.crypto.cryptoprices.client.CoinMarketPlaceClient.UrlManager.*;

@Service
public class CoinMarketPlaceClient {


    private static final int creditCount = 0;

    private static final Logger logger = LogManager.getLogger(CoinMarketPlaceClient.class);
    private static final String URL_COIN_TO_TRACK = "http://www.ydahar.com/currencies/currencies.php";
    private static final String URL_COIN_TO_TRACK_UPDATE = "http://www.ydahar.com/currencies/server.php";
    private static final String URI_GET_SPECEFIC_COIN = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
    private static final String URI_GET_ALL_COIN = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?convert=USD&start=1&limit=5000";
    public static final String TOKEN_COMPTE_1 = "a3c5ac9b-1b2d-470b-8a67-a5112f71a981";
    public static final String TOKEN_COMPTE_2 = "b5ac83f7-ff05-4b81-85b2-682f49557114";

    public enum UrlManager {
        TEST("https://www.alphavantage.co/query?function=SMA&symbol=MSFT&interval=weekly&time_period=10&series_type=open&apikey=demo", null),
        COIN_TO_TRACK_UPDATE(URL_COIN_TO_TRACK_UPDATE, null),
        COIN_TO_TRACK(URL_COIN_TO_TRACK, null),
        SPECIFIC_COIN(URI_GET_SPECEFIC_COIN, TOKEN_COMPTE_1),
        ALL_COIN(URI_GET_ALL_COIN, TOKEN_COMPTE_2);
        String header;
        String url;

        UrlManager(String url, String header) {
            this.header = header;
            this.url = url;
        }
    }


    public static String makeAPICall(UrlManager urlManager, List<NameValuePair> parameters, List<NameValuePair> parametersBody)
            throws URISyntaxException, IOException {
        String response_content;

        URIBuilder query = new URIBuilder(urlManager.url);

        if (parameters != null) {
            query.addParameters(parameters);
        }

        CloseableHttpClient client = HttpClients.createDefault();

        HttpRequestBase request;


        if (parametersBody == null) {

            request = new HttpGet(query.build());

        } else {

            request = new HttpPost(query.build());
            ((HttpPost) request).setEntity(new UrlEncodedFormEntity(parametersBody));

        }

        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        if (urlManager.header != null)
            request.addHeader("X-CMC_PRO_API_KEY", urlManager.header);

        CloseableHttpResponse response = client.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return response_content;
    }


    public CurrencyInformation getOneCurrenciesInfo(String currencies) {

        logger.info("Symbol " + currencies);

        List<NameValuePair> paratmers = new ArrayList<>();
        paratmers.add(new BasicNameValuePair("symbol", currencies));

        try {
            String result = makeAPICall(SPECIFIC_COIN, paratmers, null);


            Gson g = new Gson();


            CurrencyInformation currencyInformation = g.fromJson(result, CurrencyInformation.class);


            return currencyInformation;
        } catch (IOException e) {
            logger.error("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            logger.error("Error: Invalid URL " + e.toString());
        }

        return null;

    }


    public CurrencyInformationStats getStatCurrencies() {
        logger.info("getStatCurrencies  ");

        List<NameValuePair> paratmers = new ArrayList<>();

        try {
            String result = makeAPICall(UrlManager.ALL_COIN, paratmers, null);

            Gson g = new Gson();
            CurrencyInformationStats currencyInformation = g.fromJson(result, CurrencyInformationStats.class);


            return currencyInformation;
        } catch (IOException e) {
            logger.error("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            logger.error("Error: Invalid URL " + e.toString());
        }
        return null;
    }


    public void updateCurrency(CurrencyToTrack currencyToTrack) {
        logger.info("getStatCurrencies  ");

        List<NameValuePair> paratmersBody = new ArrayList<>();
        paratmersBody.add(new BasicNameValuePair("update_name", "true"));
        paratmersBody.add(new BasicNameValuePair("name", currencyToTrack.getName()));
        paratmersBody.add(new BasicNameValuePair("min", String.valueOf(currencyToTrack.getMin())));
        paratmersBody.add(new BasicNameValuePair("max", String.valueOf(currencyToTrack.getMax())));
        List<NameValuePair> paratmers = new ArrayList<>();

        try {

            makeAPICall(COIN_TO_TRACK_UPDATE, paratmers, paratmersBody);


        } catch (IOException e) {
            logger.error("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            logger.error("Error: Invalid URL " + e.toString());
        }
    }

    public List<CurrencyToTrack> getCurrencyToTrack() {

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(COIN_TO_TRACK.url);

            request.setHeader(HttpHeaders.ACCEPT, "application/json");

            CloseableHttpResponse response = client.execute(request);

            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String response_content = EntityUtils.toString(entity);

            Gson g = new Gson();

            Type listType = new TypeToken<ArrayList<CurrencyToTrack>>() {
            }.getType();

            List<CurrencyToTrack> currencyInformation = g.fromJson(response_content, listType);

            return currencyInformation;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }




}