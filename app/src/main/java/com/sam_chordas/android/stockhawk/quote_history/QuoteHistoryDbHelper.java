package com.sam_chordas.android.stockhawk.quote_history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sam_chordas.android.stockhawk.data.QuoteDatabase;

/**
 * Created by Majeed on 03-07-2016.
 */
public class QuoteHistoryDbHelper extends SQLiteOpenHelper {

    public QuoteHistoryDbHelper(Context context) {
        super(context, "quotes_history.db", null, QuoteDatabase.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_QUOTES_HISTORY_TABLE = "CREATE TABLE " + QuoteHistoryContract.TABLE_NAME +
                " ( " + QuoteHistoryContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuoteHistoryContract.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                QuoteHistoryContract.COLUMN_CLOSE + " REAL NOT NULL, " +
                QuoteHistoryContract.COLUMN_DATE + " TEXT NOT NULL ); ";
/**
                "FOREIGN KEY ( " + QuoteHistoryContract.COLUMN_SYMBOL + ") REFERENCES" +
                QuoteDatabase.QUOTES + "( " + QuoteColumns.SYMBOL + "));";
**/
        db.execSQL(CREATE_QUOTES_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + QuoteHistoryContract.TABLE_NAME);
    }
}
