package com.yuansfer.pay.payment;

import android.app.Activity;

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
import com.yuansfer.pay.alipay.AlipayItem;
import com.yuansfer.pay.alipay.AlipayStrategy;
import com.yuansfer.pay.braintree.BrainTreeDropInActivity;
import com.yuansfer.pay.braintree.BrainTreePayActivity;
import com.yuansfer.pay.wxpay.WxPayItem;
import com.yuansfer.pay.wxpay.WxPayStrategy;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.sdk.BuildConfig;


/**
 * @Author Fly
 * @CreateDate 2019/5/23 16:43
 */
public class YSAppPay {

    private static YSAppPay sInstance;

    private YSAppPay() {
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
     * 注册支付结果通知
     */
    public static void registerPayResultCallback(PayResultMgr.IPayResultCallback callback) {
        PayResultMgr.getInstance().addPayResultCallback(callback);
    }

    /**
     * 移除支付结果通知
     */
    public static void unregisterPayResultCallback(PayResultMgr.IPayResultCallback callback) {
        PayResultMgr.getInstance().removeResultCallback(callback);
    }

    /**
     * 绑定Braintree
     */
    public <T extends BrainTreePayActivity> void bindBrainTree(T activity, String authorization) {
        try {
            activity.setBrainTreeFragment(BraintreeFragment.newInstance(activity, authorization));
        } catch (InvalidArgumentException e) {
            PayResultMgr.getInstance().dispatchPayFail(PayType.BRAIN_TREE
                    , ErrStatus.getInstance("B10", e.getMessage()));
        }
    }

    /**
     * 解绑Braintree
     */
    public <T extends BrainTreePayActivity> void unbindBrainTree(T activity) {
        BraintreeFragment braintreeFragment = activity.getBrainTreeFragment();
        if (braintreeFragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .remove(braintreeFragment).commitAllowingStateLoss();
        }
    }

    /**
     * 发起支付宝支付
     */
    public void requestAliPayment(Activity activity, AlipayItem alipayItem) {
        new AlipayStrategy().startPay(activity, alipayItem);
    }

    /**
     * 发起微信支付
     */
    public void requestWechatPayment(Activity activity, WxPayItem wxPayItem) {
        WxPayStrategy.initWxAppId(activity.getApplicationContext(), wxPayItem.getPayReq().appId);
        WxPayStrategy wxPayStrategy = WxPayStrategy.getInstance();
        wxPayStrategy.startPay(activity, wxPayItem);
    }

    /**
     * 发送Google Pay
     */
    public <T extends BrainTreePayActivity> void requestGooglePayment(T activity, GooglePaymentRequest googlePaymentRequest) {
        GooglePayment.requestPayment(activity.getBrainTreeFragment(), googlePaymentRequest);
    }

    /**
     * 发送PayPal支付，已安装App会自动启动，否则将打开H5
     */
    public <T extends BrainTreePayActivity> void requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestOneTimePayment(activity.getBrainTreeFragment(), payPalRequest);
    }

    /**
     * 发送PayPal支付，已安装App会自动启动，否则将打开H5
     */
    public <T extends BrainTreePayActivity> void requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestBillingAgreement(activity.getBrainTreeFragment(), payPalRequest);
    }

    /**
     * 发起Venmo支付
     */
    public <T extends BrainTreePayActivity> void requestVenmoPayment(T activity, boolean vault) {
        Venmo.authorizeAccount(activity.getBrainTreeFragment(), vault);
    }

    /**
     * 发起卡片支付,信用卡/借记卡
     */
    public <T extends BrainTreePayActivity> void requestCardPayment(T activity, CardBuilder cardBuilder) {
        Card.tokenize(activity.getBrainTreeFragment(), cardBuilder);
    }

    /**
     * 发起Drop-in UI多钱包支付
     */
    public <T extends BrainTreeDropInActivity> void requestDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest) {
        dropInRequest.clientToken(authorization);
        activity.startActivityForResult(dropInRequest.getIntent(activity), BrainTreeDropInActivity.REQUEST_CODE);
    }

}
