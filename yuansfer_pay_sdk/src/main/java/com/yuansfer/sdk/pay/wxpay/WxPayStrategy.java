package com.yuansfer.sdk.pay.wxpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yuansfer.sdk.R;
import com.yuansfer.sdk.pay.PayType;
import com.yuansfer.sdk.pay.IPayStrategy;
import com.yuansfer.sdk.pay.PayResultMgr;
import com.yuansfer.sdk.YSAppPay;
import com.yuansfer.sdk.util.ResStringGet;

/**
 * @Author Fly-Android
 * @CreateDate 2019/5/23 12:01
 * @Desciption 微信支付
 */
public class WxPayStrategy implements IPayStrategy<WxPayItem> {

    private IWXAPI sWxAPI;
    private static WxPayStrategy sInstance;

    private WxPayStrategy(Context context, String appId) {
        if (context instanceof Activity) {
            context = context.getApplicationContext();
        }
        sWxAPI = WXAPIFactory.createWXAPI(context, appId);
        sWxAPI.registerApp(appId);
    }

    /**
     * 获取实例
     *
     * @return WxPayStrategy实例
     */
    public static WxPayStrategy getInstance() {
        requireNonNull(sInstance, ResStringGet.getString(R.string.wx_pay_not_init));
        return sInstance;
    }

    /**
     * 创建实例
     *
     * @param context 上下文,applicationContext
     * @param appId   应用id
     */
    public static void initWxAppId(Context context, String appId) {
        if (sInstance == null) {
            synchronized (WxPayStrategy.class) {
                if (sInstance == null) {
                    sInstance = new WxPayStrategy(context, appId);
                }
            }
        }
    }

    /**
     * 处理回调Intent
     *
     * @param intent       意图
     * @param eventHandler 事件handler
     */
    public void handleIntent(Intent intent, IWXAPIEventHandler eventHandler) {
        requireNonNull(sWxAPI, ResStringGet.getString(R.string.wx_not_register));
        sWxAPI.handleIntent(intent, eventHandler);
    }

    /**
     * 是否支付微信支付
     *
     * @return true支持
     */
    private boolean isSupportWxPay() {
        return sWxAPI.isWXAppInstalled() && sWxAPI.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }

    /**
     * 判空
     *
     * @param obj     对象
     * @param message 空提示
     * @param <T>     原对象
     * @return
     */
    private static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new AndroidRuntimeException(message);
        }
        return obj;
    }

    /**
     * 调起微信应用发起支付
     *
     * @param activity Context
     * @param request  WxPayItem实例
     */
    @Override
    public void startPay(Activity activity, WxPayItem request) {
        requireNonNull(sWxAPI, ResStringGet.getString(R.string.wx_not_register));
        if (!isSupportWxPay()) {
            if (YSAppPay.sDebug) {
                Log.d(YSAppPay.SDK_TAG, ResStringGet.getString(R.string.wx_not_support));
            }
            PayResultMgr.getInstance().dispatchPayFail(PayType.WXPAY, ResStringGet.getString(R.string.wx_not_support));
            return;
        }
        boolean ret = sWxAPI.sendReq(request.getPayReq());
        if (YSAppPay.sDebug) {
            Log.d(YSAppPay.SDK_TAG, "wxpay started:" + ret);
        }
    }

}
