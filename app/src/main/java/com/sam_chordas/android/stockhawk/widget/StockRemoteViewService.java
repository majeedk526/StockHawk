package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.Consts;

/**
 * Created by Majeed on 17-07-2016.
 */
public class StockRemoteViewService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {


        RemoteViewsFactory remoteViewsFactory = new RemoteViewsFactory() {
            private Cursor c = null;

            @Override
            public void onCreate() {
                int tmp=0;
            }

            @Override
            public void onDataSetChanged() {

                if(c!=null){c.close();}

                final long identityToken = Binder.clearCallingIdentity();
                c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,new String[]{
                                QuoteColumns._ID,
                                QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

                if(c!=null){
                    c.close();
                    c=null;
                }

            }

            @Override
            public int getCount() {
                return c!=null ? c.getCount():0;
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if(position == AdapterView.INVALID_POSITION || c==null || !c.moveToPosition(position)){
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);


                views.setTextViewText(R.id.stock_symbol,c.getString(c.getColumnIndex(Consts.STR_SYMBOL)));
                views.setTextViewText(R.id.bid_price,c.getString(c.getColumnIndex("bid_price")));

                if (c.getInt(c.getColumnIndex("is_up")) == 1){
                    views.setTextViewCompoundDrawables(R.id.change,0,R.drawable.percent_change_pill_green,0,0);
                    }
                 else {
                    views.setTextViewCompoundDrawables(R.id.change,0,R.drawable.percent_change_pill_red,0,0);

                }

                if (Utils.showPercent){
                    views.setTextViewText(R.id.change,c.getString(c.getColumnIndex("percent_change")));
                } else{
                    views.setTextViewText(R.id.change,c.getString(c.getColumnIndex("change")));
                }



                final Intent fillIntent = new Intent();
                fillIntent.putExtra(Consts.STR_SYMBOL,c.getString(c.getColumnIndex(Consts.STR_SYMBOL)));
                views.setOnClickFillInIntent(R.id.list_item_quote,fillIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(),R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };

        return remoteViewsFactory;
    }


}
