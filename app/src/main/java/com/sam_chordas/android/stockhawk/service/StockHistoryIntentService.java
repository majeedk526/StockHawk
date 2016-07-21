package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sam_chordas.android.stockhawk.quote_history.FetchStockHistory;

import org.json.JSONException;


public class StockHistoryIntentService extends IntentService {


    public StockHistoryIntentService() {
        super("StockHistoryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(!isConnected()) {return;}

        String symbol = intent.getExtras().getString("symbol");
        FetchStockHistory fsh = new FetchStockHistory(getApplicationContext());
        try {
            fsh.fetch(symbol);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}
