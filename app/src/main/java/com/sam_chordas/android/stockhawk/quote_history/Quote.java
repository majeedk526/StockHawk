package com.sam_chordas.android.stockhawk.quote_history;

/**
 * Created by Majeed on 09-07-2016.
 */
public class Quote {

    public String symbol, closed, date;

    public Quote(String symbol, String closed, String date){
        this.symbol = symbol;
        this.closed = closed;
        this.date = date;
    }

}
