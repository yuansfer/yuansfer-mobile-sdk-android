package com.yuansfer.pay.aliwx;

import android.app.Activity;
import android.content.Context;


public interface IAliWxPay {
   void setAliEnv(boolean production);
   void registerAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback);
   void unregisterAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback);
   void requestAliPayment(Activity activity, String orderInfo);
   void registerWXAPP(Context context, String appId);
   void requestWechatPayment(WxPayItem wxPayItem);
}
