package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by mkdin on 03-07-2016.
 */
public class ShowToast implements Runnable {
    private String mText;
    private Context mContext;
    public ShowToast(String text, Context context) {
        mText = text;
        mContext = context;
    }
    @Override
    public void run() {
        Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
    }
}
