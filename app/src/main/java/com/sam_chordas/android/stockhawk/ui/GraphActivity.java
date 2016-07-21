package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.quote_history.QuoteHistoryContract;
import com.sam_chordas.android.stockhawk.quote_history.UtilsHistory;
import com.sam_chordas.android.stockhawk.service.StockHistoryIntentService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Majeed on 30-06-2016.
 */
public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    LineChartView mChart;
    LineSet dataSet;
    String[] mLabels;
    float[] mValues;
    String symbol;

    private static final String[] QUOTE_HISTORY_PROJECTION = {
            QuoteHistoryContract.COLUMN_SYMBOL,
            QuoteHistoryContract.COLUMN_CLOSE,
            QuoteHistoryContract.COLUMN_DATE
    };

    private static final int COLUMN_SYMBOL = 0;
    private static final int COLUMN_CLOSE = 1;
    private static final int COLUMN_DATE = 2;



    public static final int QUOTE_HISTORY_LOADER = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_graph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart = (LineChartView) findViewById(R.id.linechart);


        Bundle args = getIntent().getExtras();
        symbol = args.getString("symbol");
        getSupportActionBar().setTitle(symbol);

        getLoaderManager().initLoader(QUOTE_HISTORY_LOADER,args,this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = QuoteHistoryContract.buildUriWithSymbol(symbol);
        return new CursorLoader(this,uri,QUOTE_HISTORY_PROJECTION,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.getCount()==0){
            Bundle args = getIntent().getExtras();
            Intent intent = new Intent(this, StockHistoryIntentService.class);
            intent.putExtra(Consts.STR_SYMBOL, args.getString(Consts.STR_SYMBOL));
            startService(intent);
            return;
        }


        mLabels = new String[data.getCount()];
        mValues = new float[data.getCount()];

        int i=0;
        while(data.moveToNext()){
            try {

                mLabels[i] = getReadableMonth(String.format(Locale.US,"%s",data.getString(COLUMN_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mValues[i] = Float.valueOf(String.format(Locale.getDefault(),"%s",data.getString(COLUMN_CLOSE)));
            i++;
        }

        dataSet = new LineSet(mLabels,mValues);
        dataSet.setColor(Color.parseColor("#778dbb"))
                .setFill(Color.parseColor("#8e375c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(2)
                .beginAt(0);

        int min = Math.round(getMinVal(mValues))-5;
        int max = Math.round(getMaxVal(mValues))+5;

        int tmp = max-min, step=1;
        while(tmp > 15){
            tmp-=15;
            step++;
        }

        while((max-min)%step!=0) {max++;}

        mChart.setAxisBorderValues(min,max,step);
        mChart.setAxisLabelsSpacing(2.0f);
        mChart.addData(dataSet);
        mChart.notifyDataUpdate();
        mChart.show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        mChart.invalidate();
    }


    static String lastMonth="";
    private static String getReadableMonth(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(UtilsHistory.dateFormat.parse(date));
        String tmpMonth = cal.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault());
        if(tmpMonth.equals(GraphActivity.lastMonth)){
            return "";
        } else {
            lastMonth = tmpMonth;
            return tmpMonth;
        }
    }

    private static float getMinVal(float[] values){
        float min=values[0];

        for(float v : values){
            if(v<min) {min = v;}
        }
        return min;
    }

    private static float getMaxVal(float[] values){
        float max=0;

        for(float v : values){
            if(v>max) {max = v;}
        }
        return max;
    }
}
