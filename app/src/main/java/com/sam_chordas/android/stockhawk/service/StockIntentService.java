package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.Consts;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public static final String ACTION_STOCK_NOT_FOUND = "com.sam_chordas.android.stockhawk.STOCKNOTFOUND";
  public static final String ERROR_MSG = "error_msg";

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra(Consts.STR_TAG).equals(Consts.STR_ADD)){
      args.putString(Consts.STR_SYMBOL, intent.getStringExtra(Consts.STR_SYMBOL));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    int r = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(Consts.STR_TAG), args));


    if(r == StockTaskService.STOCK_NOT_FOUND){

      Intent erIntent = new Intent();
      erIntent.setAction(ACTION_STOCK_NOT_FOUND);
      String s1 = getString(R.string.msg_stock_not_found_1);
      String s2 = getString(R.string.msg_stock_not_found_2);


      erIntent.putExtra(ERROR_MSG,s1+ intent.getStringExtra(Consts.STR_SYMBOL)
              + s2);
      LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(erIntent);
    }

  }
}
