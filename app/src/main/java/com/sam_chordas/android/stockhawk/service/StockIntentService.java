package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.ShowToast;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {
    private final String LOG_TAG = StockIntentService.class.getSimpleName();
    Handler mHandler;
    public StockIntentService() {
        super(StockIntentService.class.getName());
        mHandler = new Handler();
    }

    public StockIntentService(String name) {
        super(name);
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        String inputSymbol = intent.getStringExtra("symbol");

        if (intent.getStringExtra("tag").equals("add") && !inputSymbol.isEmpty()) {
            args.putString("symbol", inputSymbol.trim());
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        int errorCode = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));

        Log.v(LOG_TAG, "Error Code: " + errorCode);
        if(errorCode == GcmNetworkManager.RESULT_FAILURE && null != inputSymbol) {
            String errorMessage = getString(R.string.symbol_not_found);
            mHandler.post(new ShowToast(String.format(errorMessage, inputSymbol), this));
        } else if(errorCode == GcmNetworkManager.RESULT_SUCCESS && null != inputSymbol) {
            String successMessage = getString(R.string.symbol_found);
            mHandler.post(new ShowToast(String.format(successMessage, inputSymbol), this));
        }
    }
}
