package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by mkdin on 08-07-2016.
 */
public class DetailWidgetRemoteViewService extends RemoteViewsService {

    public final String LOG_TAG = DetailWidgetRemoteViewService.class.getSimpleName();
    private final String[] STOCKS_COLUMNS = new String[]{
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.CREATED,
            QuoteColumns.ISCURRENT,
            QuoteColumns._ID
    };

    private final int SYMB_IND = 0;
    private final int BID_IND = 1;
    private final int CREATED_IND = 2;
    private final int IS_CURRENT_IND = 3;
    private final int ID_IND = 4;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // Our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Uri stocksUri = QuoteProvider.Quotes.CONTENT_URI;
                data = getContentResolver().query(stocksUri,
                        STOCKS_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews row = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_item);
                String selectedSymbol = data.getString(SYMB_IND);
                row.setTextViewText(R.id.widget_bid_price, data.getString(BID_IND));
                row.setTextViewText(R.id.widget_stock_symbol, selectedSymbol);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(getString(R.string.symbol_selected), selectedSymbol);

                row.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return row;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_stock_symbol, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(ID_IND);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
