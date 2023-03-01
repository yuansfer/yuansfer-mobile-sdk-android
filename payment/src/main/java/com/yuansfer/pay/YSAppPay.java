package com.yuansfer.pay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alipay.sdk.app.EnvUtils;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.GooglePayment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.Venmo;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PayPalRequest;
import com.yuansfer.pay.aliwx.AliWxPayMgr;
import com.yuansfer.pay.aliwx.IAliWxPay;
import com.yuansfer.pay.aliwx.WxPayItem;
import com.yuansfer.pay.api.IClientAPI;
import com.yuansfer.pay.braintree.BTCustomPayActivity;
import com.yuansfer.pay.braintree.BTDropInActivity;
import com.yuansfer.pay.braintree.IBraintreePay;
import com.yuansfer.pay.cashapp.ICashAppPay;
import com.yuansfer.pay.util.ErrStatus;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.sdk.BuildConfig;


/**
 * @Author Fly
 * @CreateDate 2019/5/23 16:43
 */
public class YSAppPay {

    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static YSAppPay sInstance;

    private YSAppPay() {
    }

    @Deprecated
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

    public static IClientAPI getClientAPI() {
        return ClientAPIImpl.get(sHandler);
    }

    public static String getSDKVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static void setLogEnable(boolean enable) {
        LogUtils.logEnable = enable;
    }

    public static IAliWxPay getAliWxPay() {
        return AliWxPayImpl.get();
    }

    public static IBraintreePay getBraintreePay() {
        return BraintreePayImpl.get();
    }

    public static ICashAppPay getCashAppPay(String clientId) {
        return CashAppPayImpl.get(sHandler, clientId, true);
    }

    public static ICashAppPay getSandboxCashAppPay(String clientId) {
        return CashAppPayImpl.get(sHandler, clientId, false);
    }

    @Deprecated
    public static void setAliEnv(boolean production) {
        EnvUtils.setEnv(production ? EnvUtils.EnvEnum.ONLINE : EnvUtils.EnvEnum.SANDBOX);
    }

    @Deprecated
    public static void registerAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback) {
        AliWxPayMgr.getInstance().addAliWxPayCallback(callback);
    }

    @Deprecated
    public static void unregisterAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback) {
        AliWxPayMgr.getInstance().removeAliWxPayCallback(callback);
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void bindBrainTree(T activity, String authorization) {
        try {
            activity.setBrainTreeFragment(BraintreeFragment.newInstance(activity, authorization));
        } catch (InvalidArgumentException e) {
            activity.onPrepayError(ErrStatus.getInstance(ErrStatus.BT_INIT_ERROR, e.getMessage()));
        }
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void unbindBrainTree(T activity) {
        BraintreeFragment braintreeFragment = activity.getBrainTreeFragment();
        if (braintreeFragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .remove(braintreeFragment).commitAllowingStateLoss();
        }
    }

    @Deprecated
    public void requestAliPayment(Activity activity, String orderInfo) {
        AliWxPayMgr.getInstance().requestAlipay(activity, orderInfo);
    }

    @Deprecated
    public void registerWXAPP(Context context, String appId) {
        AliWxPayMgr.getInstance().registerApp(context, appId);
    }

    @Deprecated
    public void requestWechatPayment(WxPayItem wxPayItem) {
        AliWxPayMgr.getInstance().requestWXPay(wxPayItem);
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void requestGooglePayment(T activity, GooglePaymentRequest googlePaymentRequest) {
        GooglePayment.requestPayment(activity.getBrainTreeFragment(), googlePaymentRequest);
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestOneTimePayment(activity.getBrainTreeFragment(), payPalRequest);
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestBillingAgreement(activity.getBrainTreeFragment(), payPalRequest);
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void requestVenmoPayment(T activity, boolean vault) {
        Venmo.authorizeAccount(activity.getBrainTreeFragment(), vault);
    }

    @Deprecated
    public <T extends BTCustomPayActivity> void requestCardPayment(T activity, CardBuilder cardBuilder) {
        Card.tokenize(activity.getBrainTreeFragment(), cardBuilder);
    }

    @Deprecated
    public <T extends BTDropInActivity> void requestDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest) {
        dropInRequest.clientToken(authorization);
        activity.startActivityForResult(dropInRequest.getIntent(activity), BTDropInActivity.REQUEST_CODE);
    }
}
