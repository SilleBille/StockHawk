package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = StockDetailActivity.class.getSimpleName();
    private final int STOCK_DETAIL_LOADER = 14;
    LineChartView mLineChart;
    private final String[] mProjection = new String[]{
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.CREATED
    };
    private final int COL_SYMBOL_IND = 0;
    private final int COL_BID_IND = 1;
    private final int COL_CREATED_IND = 2;

    String[] mSymbolSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mLineChart = (LineChartView) findViewById(R.id.linechart);
        mSymbolSelected = new String[]{
                getIntent().getStringExtra(getString(R.string.symbol_selected))
        };
        Log.v(LOG_TAG, "Symbol Selected: " + mSymbolSelected[0]);

        getLoaderManager().initLoader(STOCK_DETAIL_LOADER, null, this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mSymbolSelected[0]);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                QuoteProvider.Quotes.CONTENT_URI,
                mProjection,
                QuoteColumns.SYMBOL + " = ?",
                mSymbolSelected,
                QuoteColumns.CREATED + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            fillLineChart(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void fillLineChart(Cursor cursor) {
        LineSet dataset = new LineSet();
        Paint gridPaint = new Paint();
        gridPaint.setColor(getResources().getColor(R.color.material_blue_100));

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        dataset.setColor(getResources().getColor(R.color.material_blue_500))
                .setThickness(getResources().getDimension(R.dimen.chart_radius));


        // Initialize the start and end index
        cursor.moveToFirst();
        int start = (int) Math.floor(Float.parseFloat(cursor.getString(COL_BID_IND)));
        int end = start;

        int i = 1;
        do {

            Float bidValue = Float.parseFloat(cursor.getString(COL_BID_IND));
            if (bidValue.intValue() < start)
                start = bidValue.intValue();
            if (bidValue.intValue() > end)
                end = bidValue.intValue();
            Point point = new Point("bid " + i++, bidValue);
            point.setRadius(getResources().getDimension(R.dimen.dot_radius));
            point.setColor(getResources().getColor(R.color.dot_color));
            dataset.addPoint(point);
        } while (cursor.moveToNext());


        mLineChart.addData(dataset);
        start -= 50;
        end += 50;
        Log.v(LOG_TAG, "Start: " + start + " End: " + end);

        if (start % 10 != 0) start -= start % 10;

        if (end % 10 != 0) end += 10 - (end % 10);

        mLineChart.setBorderSpacing(1)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(getResources().getColor(R.color.font_color))
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setAxisBorderValues(start, end, 10)
                .setGrid(ChartView.GridType.FULL, gridPaint);
        mLineChart.show();

    }

}
