package com.yuansfer.sdk;

import android.app.Activity;
import android.content.Context;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.alipay.sdk.app.EnvUtils;
import com.yuansfer.sdk.pay.IPayStrategy;
import com.yuansfer.sdk.pay.PayItem;
import com.yuansfer.sdk.pay.PayResultMgr;
import com.yuansfer.sdk.pay.PayType;
import com.yuansfer.sdk.pay.alipay.AlipayItem;
import com.yuansfer.sdk.pay.alipay.AlipayStrategy;
import com.yuansfer.sdk.pay.wxpay.WxPayItem;
import com.yuansfer.sdk.pay.wxpay.WxPayStrategy;
import com.yuansfer.sdk.util.ResStringGet;


/**
 * @Author Fly-Android
 * @CreateDate 2019/5/23 16:43
 * @Desciption Yuansfer app pay
 */
public class YSAppPay {

    public static final String SDK_TAG = "ysapppay";
    public static boolean sDebug;
    private static YSAppPay sInstance;
    private Context mContext;

    private YSAppPay() {
    }

    /**
     * init context
     *
     * @param context
     */
    public static void initialize(Context context) {
        YSAppPay client = getInstance();
        if (context instanceof Activity) {
            client.mContext = context.getApplicationContext();
        } else {
            client.mContext = context;
        }
        ResStringGet.setContext(client.mContext);
    }

    /**
     * get instance
     *
     * @return
     */
    public static YSAppPay getInstance() {
        if (sInstance == null) {
            synchronized (YSAppPay.class) {
                if (sInstance == null) {
                    sInstance = new YSAppPay();
                }
            }
        }
        return sInstance;
    }

    /**
     * set test env
     */
    public static void setTestMode() {
        sDebug = true;
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
    }

    /**
     * unregister call listener
     *
     * @param callback
     */
    public static void registerPayResultCallback(PayResultMgr.IPayResultCallback callback) {
        PayResultMgr.getInstance().addPayResultCallback(callback);
    }

    /**
     * unregister callback listener
     *
     * @param callback
     */
    public static void unregisterPayResultCallback(PayResultMgr.IPayResultCallback callback) {
        PayResultMgr.getInstance().removeResultCallback(callback);
    }

    /**
     * should support wechat pay?
     *
     * @param appId
     */
    public void supportWxPay(String appId) {
        if (mContext == null) {
            throw new AndroidRuntimeException("fun initialize has not been called");
        }
        WxPayStrategy.initWxAppId(mContext, appId);
    }

    /**
     * start pay
     *
     * @param activity
     * @param payItem
     */
    public void startPay(Activity activity, PayItem payItem) {
        if (mContext == null) {
            throw new AndroidRuntimeException("fun initialize has not been called");
        }
        if (payItem.getPayType() == PayType.ALIPAY) {
            if (!(payItem instanceof AlipayItem)) {
                if (YSAppPay.sDebug) {
                    Log.d(SDK_TAG, ResStringGet.getString(R.string.pay_item_wrong));
                }
                throw new AndroidRuntimeException(ResStringGet.getString(R.string.pay_item_wrong));
            }
            IPayStrategy<AlipayItem> payStrategy = new AlipayStrategy();
            payStrategy.startPay(activity, (AlipayItem) payItem);
        } else if (payItem.getPayType() == PayType.WXPAY) {
            if (!(payItem instanceof WxPayItem)) {
                if (YSAppPay.sDebug) {
                    Log.d(SDK_TAG, ResStringGet.getString(R.string.pay_item_wrong));
                }
                throw new AndroidRuntimeException(ResStringGet.getString(R.string.pay_item_wrong));
            }
            IPayStrategy<WxPayItem> payStrategy = WxPayStrategy.getInstance();
            payStrategy.startPay(activity, (WxPayItem) payItem);
        } else {
            throw new AndroidRuntimeException(ResStringGet.getString(R.string.pay_type_not_support));
        }
    }

}
