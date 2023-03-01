package com.yuansfer.pay;

import android.app.Activity;
import android.content.Context;

import com.alipay.sdk.app.EnvUtils;
import com.yuansfer.pay.aliwx.AliWxPayMgr;
import com.yuansfer.pay.aliwx.IAliWxPay;
import com.yuansfer.pay.aliwx.WxPayItem;

class AliWxPayImpl implements IAliWxPay {

    private static AliWxPayImpl instance;

    public static AliWxPayImpl get() {
        if (instance == null) {
            instance = new AliWxPayImpl();
        }
        return  instance;
    }

    @Override
    public void setAliEnv(boolean production) {
        EnvUtils.setEnv(production ? EnvUtils.EnvEnum.ONLINE : EnvUtils.EnvEnum.SANDBOX);
    }

    @Override
    public void registerAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback) {
        AliWxPayMgr.getInstance().addAliWxPayCallback(callback);
    }

    @Override
    public void unregisterAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback) {
        AliWxPayMgr.getInstance().removeAliWxPayCallback(callback);
    }

    @Override
    public void requestAliPayment(Activity activity, String orderInfo) {
        AliWxPayMgr.getInstance().requestAlipay(activity, orderInfo);
    }

    @Override
    public void registerWXAPP(Context context, String appId) {
        AliWxPayMgr.getInstance().registerApp(context, appId);
    }

    @Override
    public void requestWechatPayment(WxPayItem wxPayItem) {
        AliWxPayMgr.getInstance().requestWXPay(wxPayItem);
    }
}
