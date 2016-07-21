package com.sam_chordas.android.stockhawk.quote_history;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by Majeed on 07-07-2016.
 */
public class FetchStockHistory {

    Context mContext;
    String symbol;
    String LOG_TAG = getClass().getSimpleName();

    public FetchStockHistory (Context context){
        this.mContext = context;
    }

    private void writeToDatabase(String s) throws JSONException {

        if(s==null){return;}

        JSONObject jo = null;
        JSONArray ja = null;

        try {
            jo = new JSONObject(s);
            jo = jo.getJSONObject("query");
            jo = jo.getJSONObject("results");
            ja = jo.getJSONArray("quote");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues[] values = new ContentValues[ja.length()];
        for(int i=0; i<ja.length(); i++){
            ContentValues value = new ContentValues();
            JSONObject tmpJo = ja.getJSONObject(i);
            value.put(QuoteHistoryContract.COLUMN_SYMBOL, tmpJo.getString("Symbol"));
            value.put(QuoteHistoryContract.COLUMN_CLOSE, String.valueOf(tmpJo.getDouble("Close")));
            value.put(QuoteHistoryContract.COLUMN_DATE, tmpJo.getString("Date"));
            values[i] = value;
        }

        mContext.getContentResolver().bulkInsert(
                QuoteHistoryContract.buildUriWithSymbol(symbol),
                values
        );
    }

    public String fetch(String sym) throws JSONException {
        this.symbol = sym;
        String jsonString = null;
        String sUrl = FetchStockHistory.buildStockHistoryDataUrl(symbol);

        URL url = null;

        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonString = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }

            writeToDatabase(jsonString);
            return jsonString;
        }
    }

    public static String buildStockHistoryDataUrl(String symbol) {
        String fromDate = getFromDate();
        String toDate = getToDate();
        try {
            String BASE_URL = "http://query.yahooapis.com/v1/public/yql?q=";
            String TABLE_QUERY = "select * from yahoo.finance.historicaldata where " +
                    "symbol = \"" + symbol + "\" and startDate = \"" + fromDate + "\" " +
                    "and endDate = \"" + toDate + "\"";

            String FINAL_URL = BASE_URL + URLEncoder.encode(TABLE_QUERY, "UTF-8")
                    + "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=";
            return FINAL_URL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String getFromDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        return UtilsHistory.dateFormat.format(cal.getTime());
    }

    private static String getToDate() {
        Calendar cal = Calendar.getInstance();
        return UtilsHistory.dateFormat.format(cal.getTime());
    }
}
