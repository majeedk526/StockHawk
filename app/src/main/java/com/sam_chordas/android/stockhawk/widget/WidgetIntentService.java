package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by Majeed on 17-07-2016.
 */
public class WidgetIntentService extends IntentService {



    public WidgetIntentService(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

        for(int appWidgetId : appWidgetIds){
            Intent wintent = new Intent();
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);

            PendingIntent pi = PendingIntent.getBroadcast(context,0,wintent,0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent remotSerivceIntent = new Intent(context,StockRemoteViewService.class);
            views.setRemoteAdapter(R.id.lv,remotSerivceIntent);

            Intent clickIntentTemplate = new Intent(context, MyStocksActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(appWidgetId,clickPendingIntentTemplate);


            views.setOnClickPendingIntent(R.id.iv,pi);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }


    }
}
