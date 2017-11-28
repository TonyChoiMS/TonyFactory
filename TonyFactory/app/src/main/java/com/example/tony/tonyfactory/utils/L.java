package com.example.tony.tonyfactory.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017-09-18.
 */

public class L {

    public static final String TAG = "algorithm";
    public static boolean DEBUG = true;

    public static void i(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }
}
