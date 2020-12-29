package com.yuansfer.pay.payment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alipay.sdk.app.EnvUtils;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.GooglePayment;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.google.android.gms.wallet.ShippingAddressRequirements;
import com.google.android.gms.wallet.TransactionInfo;
import com.yuansfer.pay.alipay.AlipayItem;
import com.yuansfer.pay.alipay.AlipayStrategy;
import com.yuansfer.pay.dropin.YSDropInPayActivity;
import com.yuansfer.pay.googlepay.YSGooglePayActivity;
import com.yuansfer.pay.googlepay.YSGooglePayItem;
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
     * 发起支付宝支付
     */
    public void startAlipay(Activity activity, AlipayItem alipayItem) {
        new AlipayStrategy().startPay(activity, alipayItem);
    }

    /**
     * 发起微信支付
     */
    public void startWechatPay(Activity activity, WxPayItem wxPayItem) {
        WxPayStrategy.initWxAppId(activity.getApplicationContext(), wxPayItem.getPayReq().appId);
        WxPayStrategy wxPayStrategy = WxPayStrategy.getInstance();
        wxPayStrategy.startPay(activity, wxPayItem);
    }

    /**
     * 绑定并启动google pay校验
     */
    public <T extends YSGooglePayActivity> void bindGooglePay(T activity, String authorization) {
        try {
            activity.setBrainTreeFragment(BraintreeFragment.newInstance(activity, authorization));
        } catch (InvalidArgumentException e) {
            PayResultMgr.getInstance().dispatchPayFail(PayType.GOOGLE_PAY
                    , ErrStatus.getInstance("G10", e.getMessage()));
        }
    }

    /**
     * 解绑google pay
     */
    public <T extends YSGooglePayActivity> void unbindGooglePay(T activity) {
        BraintreeFragment braintreeFragment = activity.getBrainTreeFragment();
        if (braintreeFragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .remove(braintreeFragment).commitAllowingStateLoss();
        }
    }

    /**
     * 发起Google Pay
     */
    public <T extends YSGooglePayActivity> void startGooglePay(T activity, YSGooglePayItem googlePayItem) {
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setCurrencyCode(googlePayItem.getCurrency())
                        .setTotalPrice(String.valueOf(googlePayItem.getTotalPrice()))
                        .setTotalPriceStatus(googlePayItem.getTotalPriceStatus())
                        .build())
                .allowPrepaidCards(googlePayItem.isAllowPrepaidCards())
                .billingAddressFormat(googlePayItem.getBillingAddressFormat())
                .billingAddressRequired(googlePayItem.isBillingAddressRequired())
                .emailRequired(googlePayItem.isEmailRequired())
                .phoneNumberRequired(googlePayItem.isPhoneNumberRequired())
                .shippingAddressRequired(googlePayItem.isShippingAddressRequired())
                .shippingAddressRequirements(ShippingAddressRequirements.newBuilder()
                        .addAllowedCountryCodes(googlePayItem.getAddAllowedCountryCodes())
                        .build())
                .googleMerchantId(googlePayItem.getGoogleMerchantId());
        GooglePayment.requestPayment(activity.getBrainTreeFragment(), googlePaymentRequest);
    }

    /**
     * 发起Drop-in UI多钱包支付
     */
    public <T extends YSDropInPayActivity> void startDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest) {
        dropInRequest.clientToken(authorization);
        activity.startActivityForResult(dropInRequest.getIntent(activity), YSDropInPayActivity.REQUEST_CODE);
    }

    /**
     * 发起Drop-in UI多钱包支付
     */
    public <T extends YSDropInPayActivity> void startDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest, YSGooglePayItem googlePayItem) {
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setCurrencyCode(googlePayItem.getCurrency())
                        .setTotalPrice(String.valueOf(googlePayItem.getTotalPrice()))
                        .setTotalPriceStatus(googlePayItem.getTotalPriceStatus())
                        .build())
                .allowPrepaidCards(googlePayItem.isAllowPrepaidCards())
                .billingAddressFormat(googlePayItem.getBillingAddressFormat())
                .billingAddressRequired(googlePayItem.isBillingAddressRequired())
                .emailRequired(googlePayItem.isEmailRequired())
                .phoneNumberRequired(googlePayItem.isPhoneNumberRequired())
                .shippingAddressRequired(googlePayItem.isShippingAddressRequired())
                .shippingAddressRequirements(ShippingAddressRequirements.newBuilder()
                        .addAllowedCountryCodes(googlePayItem.getAddAllowedCountryCodes())
                        .build())
                .googleMerchantId(googlePayItem.getGoogleMerchantId());
        dropInRequest.clientToken(authorization);
        dropInRequest.googlePaymentRequest(googlePaymentRequest);
        activity.startActivityForResult(dropInRequest.getIntent(activity), YSDropInPayActivity.REQUEST_CODE);
    }

}
