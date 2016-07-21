package com.sam_chordas.android.stockhawk.quote_history;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Majeed on 06-07-2016.
 */
public class QuoteHistoryProvider extends ContentProvider {

    private QuoteHistoryDbHelper mDbHelper;
    public static final UriMatcher sUriMatcher = buildUriMatcher();
    //private static final SQLiteQueryBuilder sQueryBuilderWithSymbol;

    /*static {
        sQueryBuilderWithSymbol = new SQLiteQueryBuilder();

        sQueryBuilderWithSymbol.setTables(
                QuoteHistoryContract.TABLE_NAME + " INNER JOIN " +
                        QuoteDatabase.QUOTES + " ON " +
                        QuoteHistoryContract.TABLE_NAME + "." + QuoteHistoryContract.COLUMN_SYMBOL +
                        " = " +QuoteDatabase.QUOTES + "." + QuoteColumns.SYMBOL
        );

    }*/


    static final int QUOTE_HISTORY_WITH_SYMBOL = 100;
    static final int QUOTE_HISTORY_WITH_SYMBOL_AND_DATE = 101;


    static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = QuoteHistoryContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority,QuoteHistoryContract.PATH_QUOTE_HISTORY + "/*", QUOTE_HISTORY_WITH_SYMBOL);
        uriMatcher.addURI(authority,QuoteHistoryContract.PATH_QUOTE_HISTORY + "/*/#", QUOTE_HISTORY_WITH_SYMBOL_AND_DATE);


        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new QuoteHistoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor = null;
        String defaultSort = QuoteHistoryContract.COLUMN_DATE + " ASC";

        switch (sUriMatcher.match(uri)){

            case QUOTE_HISTORY_WITH_SYMBOL:
                String symbol = QuoteHistoryContract.getSymbolFromUri(uri);
                String sel = QuoteHistoryContract.COLUMN_SYMBOL + " = ? ";
                String selArgs[] = {symbol};

                retCursor = mDbHelper.getReadableDatabase().query(
                        QuoteHistoryContract.TABLE_NAME,
                        projection,sel,selArgs,null,null,defaultSort
                        );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){

            case QUOTE_HISTORY_WITH_SYMBOL:
                return QuoteHistoryContract.CONTENT_DIR_TYPE;
            case QUOTE_HISTORY_WITH_SYMBOL_AND_DATE:
                return QuoteHistoryContract.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = 0l;
        Uri returnUri;
        final int match = sUriMatcher.match(uri);

        switch (match){

            case QUOTE_HISTORY_WITH_SYMBOL:
                id = db.insert(QuoteHistoryContract.TABLE_NAME,null,values);
                if(id>0)
                    returnUri = QuoteHistoryContract.buildUriWithId(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
//        super.bulkInsert(uri, values);

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;


        switch (match){
            case QUOTE_HISTORY_WITH_SYMBOL:

                db.beginTransaction();

                try {

                    for(ContentValues value : values){
                        long id = db.insert(QuoteHistoryContract.TABLE_NAME,null,value);

                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnCount;
    }
}
