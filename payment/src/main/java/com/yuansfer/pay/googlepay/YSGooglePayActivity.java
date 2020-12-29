package com.yuansfer.pay.googlepay;


import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.BraintreePaymentResultListener;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.BraintreePaymentResult;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.braintree.IBrainTreeCallback;
import com.yuansfer.pay.braintree.YSBrainTreeListenerHandler;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.util.LogUtils;

/**
 * @author fly
 * @date 2020/12/21
 * @desc 子类Activity需继承此类，覆盖处理Google Pay的多个支付流程
 */
public abstract class YSGooglePayActivity extends AppCompatActivity implements BraintreeCancelListener, BraintreeErrorListener
        , PaymentMethodNonceCreatedListener, ConfigurationListener, BraintreePaymentResultListener, IBrainTreeCallback {

    private static final String TAG = "YSGooglePayActivity";
    private BraintreeFragment mBrainTreeFragment;
    private String mDeviceData;

    @Override
    public void onCancel(int i) {
        LogUtils.d(TAG, "Google Pay canceled");
        PayResultMgr.getInstance().dispatchPayCancel(PayType.GOOGLE_PAY);
    }

    @Override
    public void onError(Exception error) {
        LogUtils.d(TAG, "Google Pay error:" + error.getMessage());
        YSBrainTreeListenerHandler.handleError(error);
    }

    @Override
    public void onConfigurationFetched(Configuration configuration) {
        LogUtils.d(TAG, "onConfigurationFetched");
        collectDeviceData();
        YSBrainTreeListenerHandler.handleConfigurationFetched(mBrainTreeFragment, configuration, this);
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        LogUtils.d(TAG, "onPaymentMethodNonceCreated");
        YSBrainTreeListenerHandler.handlerPaymentMethodNonceCreated(paymentMethodNonce, mDeviceData, this);
    }

    @Override
    public void onBraintreePaymentResult(BraintreePaymentResult braintreePaymentResult) {
        LogUtils.d(TAG, "onBrainTreePaymentResult");
        YSBrainTreeListenerHandler.handleBrainTreePaymentResult(braintreePaymentResult);
    }

    private void collectDeviceData() {
        DataCollector.collectDeviceData(mBrainTreeFragment, new BraintreeResponseListener<String>() {
            @Override
            public void onResponse(String deviceData) {
                LogUtils.d(TAG, "deviceData=" + deviceData);
                mDeviceData = deviceData;
            }
        });
    }

    public final void setBrainTreeFragment(BraintreeFragment braintreeFragment) {
        this.mBrainTreeFragment = braintreeFragment;
    }

    public final BraintreeFragment getBrainTreeFragment() {
        return mBrainTreeFragment;
    }

    @Override
    public void onPaymentMethodResult(CardNonce cardNonce, String deviceData) {

    }

    @Override
    public void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData) {

    }

    @Override
    public void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData) {

    }

    @Override
    public void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData) {

    }

    @Override
    public void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData) {

    }

}
