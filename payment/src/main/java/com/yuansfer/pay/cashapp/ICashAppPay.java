package com.yuansfer.pay.cashapp;

import android.app.Activity;

import com.yuansfer.pay.util.YSException;

public interface ICashAppPay {
    void registerPayEventCallback(ICashPayCallback callback);
    void unRegisterPayEventCallback();
    void requestCashAppPayInBackground(OneTimeItem item) throws YSException;
    void requestCashAppPayInBackground(OnFileItem item);
    void authorizeCashAppPay(Activity activity) throws YSException;
}
