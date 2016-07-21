package com.sam_chordas.android.stockhawk.quote_history;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majeed on 09-07-2016.
 */
public class QuoteHistoryLoader extends AsyncTaskLoader<List<Quote>> {

    private static final String[] QUOTE_HISTORY_PROJECTION = {
            QuoteHistoryContract.COLUMN_SYMBOL,
            QuoteHistoryContract.COLUMN_CLOSE,
            QuoteHistoryContract.COLUMN_DATE
    };

    private static final int COLUMN_SYMBOL = 0;
    private static final int COLUMN_CLOSE = 1;
    private static final int COLUMN_DATE = 2;

    private String symbol;

    public QuoteHistoryLoader(Context context, String symbol) {
        super(context);
        this.symbol = symbol;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Quote> loadInBackground() {

        Uri uri = QuoteHistoryContract.buildUriWithSymbol(symbol);
        Cursor c = getContext().getContentResolver().query(uri, QuoteHistoryLoader.QUOTE_HISTORY_PROJECTION, null, null, null);

        List<Quote> q = new ArrayList<>();
        while (c.moveToFirst()) {
            q.add(new Quote(c.getString(COLUMN_SYMBOL), c.getString(COLUMN_CLOSE), c.getString(COLUMN_DATE)));
        }
        return q;
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    public void deliverResult(List<Quote> data) {
        super.deliverResult(data);
    }
}
