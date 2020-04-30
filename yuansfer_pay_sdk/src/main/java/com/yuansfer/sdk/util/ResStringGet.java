package com.yuansfer.sdk.util;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * @Author Fly-Android
 * @CreateDate 2019/5/30 11:34
 * @Desciption String rescource getter
 */
public class ResStringGet {

    private static Context sContext;

    public static void setContext(Context sContext) {
        ResStringGet.sContext = sContext;
    }

    public static String getString(@StringRes int resid) {
        if (sContext == null) {
            return "";
        }
        return sContext.getString(resid);
    }

    public static String getString(@StringRes int resid, Object... objects) {
        if (sContext == null) {
            return "";
        }
        return sContext.getString(resid, objects);
    }

}
