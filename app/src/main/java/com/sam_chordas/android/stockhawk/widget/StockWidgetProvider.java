package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.GraphActivity;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by Majeed on 13-07-2016.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_WIDGET_UPDATE = "com.sam.chordas.android.stockhawk.UPDATEWIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for(int appWidgetId : appWidgetIds){

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            Intent myStockActivityIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context,0,myStockActivityIntent,0);
            views.setOnClickPendingIntent(R.id.header,pi);

            views.setEmptyView(R.id.lv,R.id.widget_empty);

            views.setRemoteAdapter(R.id.lv,new Intent(context, StockRemoteViewService.class));

            Intent clickIntentTemplate = new Intent(context, GraphActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.lv,clickPendingIntentTemplate);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

       if(intent.getAction().equals(ACTION_WIDGET_UPDATE)){

           AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
           int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                   getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv);

        }



    }
}
