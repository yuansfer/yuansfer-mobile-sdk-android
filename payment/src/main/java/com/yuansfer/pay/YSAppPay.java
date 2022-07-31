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
import com.yuansfer.pay.aliwx.WxPayItem;
import com.yuansfer.pay.api.IClientAPI;
import com.yuansfer.pay.braintree.BTDropInActivity;
import com.yuansfer.pay.braintree.BTCustomPayActivity;
import com.yuansfer.pay.util.ErrStatus;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.sdk.BuildConfig;


/**
 * @Author Fly
 * @CreateDate 2019/5/23 16:43
 */
public class YSAppPay {

    private static Handler sHandler;
    private static YSAppPay sInstance;

    private YSAppPay() {
        sHandler = new Handler(Looper.getMainLooper());
    }

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
     * 获取在线API接口
     */
    public IClientAPI getClientAPI() {
        return ClientAPIImpl.get(sHandler);
    }

    /**
     * 获取SDK版本
     */
    public static String getSDKVersion() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 是否打印日志log
     */
    public static void setLogEnable(boolean enable) {
        LogUtils.logEnable = enable;
    }

    /**
     * 设置支付宝生产/沙箱环境
     */
    public static void setAliEnv(boolean production) {
        EnvUtils.setEnv(production ? EnvUtils.EnvEnum.ONLINE : EnvUtils.EnvEnum.SANDBOX);
    }

    /**
     * 注册微信支付宝结果通知
     */
    public static void registerAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback) {
        AliWxPayMgr.getInstance().addAliWxPayCallback(callback);
    }

    /**
     * 移除微信支付宝结果通知
     */
    public static void unregisterAliWxPayCallback(AliWxPayMgr.IAliWxPayCallback callback) {
        AliWxPayMgr.getInstance().removeAliWxPayCallback(callback);
    }

    /**
     * 绑定Braintree
     */
    public <T extends BTCustomPayActivity> void bindBrainTree(T activity, String authorization) {
        try {
            activity.setBrainTreeFragment(BraintreeFragment.newInstance(activity, authorization));
        } catch (InvalidArgumentException e) {
            activity.onPrepayError(ErrStatus.getInstance(ErrStatus.BT_INIT_ERROR, e.getMessage()));
        }
    }

    /**
     * 解绑Braintree
     */
    public <T extends BTCustomPayActivity> void unbindBrainTree(T activity) {
        BraintreeFragment braintreeFragment = activity.getBrainTreeFragment();
        if (braintreeFragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .remove(braintreeFragment).commitAllowingStateLoss();
        }
    }

    /**
     * 发起支付宝支付
     */
    public void requestAliPayment(Activity activity, String orderInfo) {
        AliWxPayMgr.getInstance().requestAlipay(activity, orderInfo);
    }

    /**
     * 注册APP到微信
     * @param context
     * @param appId
     */
    public void registerWXAPP(Context context, String appId) {
        AliWxPayMgr.getInstance().registerApp(context, appId);
    }

    /**
     * 发起微信支付
     */
    public void requestWechatPayment(WxPayItem wxPayItem) {
        AliWxPayMgr.getInstance().requestWXPay(wxPayItem);
    }

    /**
     * 发送Google Pay
     */
    public <T extends BTCustomPayActivity> void requestGooglePayment(T activity, GooglePaymentRequest googlePaymentRequest) {
        GooglePayment.requestPayment(activity.getBrainTreeFragment(), googlePaymentRequest);
    }

    /**
     * 发送PayPal支付，不保存付款方式
     */
    public <T extends BTCustomPayActivity> void requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestOneTimePayment(activity.getBrainTreeFragment(), payPalRequest);
    }

    /**
     * 发送PayPal支付，保存付款方式
     */
    public <T extends BTCustomPayActivity> void requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestBillingAgreement(activity.getBrainTreeFragment(), payPalRequest);
    }

    /**
     * 发起Venmo支付
     */
    public <T extends BTCustomPayActivity> void requestVenmoPayment(T activity, boolean vault) {
        Venmo.authorizeAccount(activity.getBrainTreeFragment(), vault);
    }

    /**
     * 发起卡片支付,信用卡/借记卡
     */
    public <T extends BTCustomPayActivity> void requestCardPayment(T activity, CardBuilder cardBuilder) {
        Card.tokenize(activity.getBrainTreeFragment(), cardBuilder);
    }

    /**
     * 发起Drop-in UI多钱包支付
     */
    public <T extends BTDropInActivity> void requestDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest) {
        dropInRequest.clientToken(authorization);
        activity.startActivityForResult(dropInRequest.getIntent(activity), BTDropInActivity.REQUEST_CODE);
    }

}
