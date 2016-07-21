package com.sam_chordas.android.stockhawk.quote_history;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Majeed on 03-07-2016.
 */
public class QuoteHistoryContract implements BaseColumns {


    public static final String CONTENT_AUTHORITY = "com.sam_chordas.android.stockhawk.quote_history.QuoteHistoryProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_QUOTE_HISTORY = QuoteHistoryContract.TABLE_NAME;

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUOTE_HISTORY).build();
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
            CONTENT_AUTHORITY + "/" + PATH_QUOTE_HISTORY;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
          CONTENT_AUTHORITY + "/" + PATH_QUOTE_HISTORY;


    private static final String QUERY_PARAM_FROM_DATE = "from";
    private static final String QUERY_PARAM_TO_DATE = "to";


    public static final String TABLE_NAME = "quotes_history";

    public static final String COLUMN_SYMBOL = "symbol";
    public static final String COLUMN_CLOSE = "close";
    public static final String COLUMN_DATE = "date";


    public static Uri buildUriWithId(long id){
        return ContentUris.withAppendedId(CONTENT_URI,id);
    }

    public static Uri buildUriWithSymbol(String symbol){
        return CONTENT_URI.buildUpon().appendPath(symbol).build();
    }

    public static String getSymbolFromUri(Uri uri){
        return uri.getPathSegments().get(1);
    }

    public static Uri buildUriWithSymbolAndDates(String symbol, String fromDate, String toDate){
        return buildUriWithSymbol(symbol).buildUpon().appendQueryParameter(QUERY_PARAM_FROM_DATE,fromDate)
                .appendQueryParameter(QUERY_PARAM_TO_DATE, toDate).build();
    }

    public static String getFromDateFromUri(Uri uri){
        return uri.getQueryParameter(QUERY_PARAM_FROM_DATE);
    }

    public static String getToDateFromUri(Uri uri){
        return uri.getQueryParameter(QUERY_PARAM_TO_DATE);
    }

}

