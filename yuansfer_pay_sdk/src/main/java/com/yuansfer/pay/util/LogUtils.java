package com.yuansfer.pay.util;

import android.util.Log;

public class LogUtils {

    private static final String SDK_TAG = "YSAppPay";
    public static boolean logEnable = false;

    public static void d(String msg) {
        if (logEnable) {
            Log.d(SDK_TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (logEnable) {
            Log.d(TAG, msg);
        }
    }

}
