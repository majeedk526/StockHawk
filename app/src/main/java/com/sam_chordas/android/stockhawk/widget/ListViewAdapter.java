package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by Majeed on 17-07-2016.
 */
public class ListViewAdapter extends CursorAdapter {

    public ListViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item_quote,null,false);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView change = (TextView) view.findViewById(R.id.change);
        TextView symbol = (TextView) view.findViewById(R.id.stock_symbol);
        TextView bidPrice = (TextView) view.findViewById(R.id.bid_price);

        int sdk = Build.VERSION.SDK_INT;
        if (cursor.getInt(cursor.getColumnIndex("is_up")) == 1){
            if (sdk < Build.VERSION_CODES.JELLY_BEAN){
                change.setBackgroundDrawable(
                        context.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }else {
                change.setBackground(
                        context.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }
        } else{
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                change.setBackgroundDrawable(
                        context.getResources().getDrawable(R.drawable.percent_change_pill_red));
            } else{
                change.setBackground(
                        context.getResources().getDrawable(R.drawable.percent_change_pill_red));
            }
        }
        if (Utils.showPercent){
            change.setText(cursor.getString(cursor.getColumnIndex("percent_change")));
        } else{
            change.setText(cursor.getString(cursor.getColumnIndex("change")));
        }

        symbol.setText(cursor.getString(cursor.getColumnIndex("symbol")));
        bidPrice.setText(cursor.getString(cursor.getColumnIndex("bid_price")));

    }
}
