package com.yuansfer.pay.braintree;



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
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.util.LogUtils;

/**
 * @author fly
 * @date 2020/12/21
 * @desc 集成Braintree自定义支付时可继承此类，处理相应渠道的支付流程，如Google Pay, PayPal, Venmo等
 */
public abstract class BrainTreePayActivity extends AppCompatActivity implements BraintreeCancelListener, BraintreeErrorListener
        , PaymentMethodNonceCreatedListener, ConfigurationListener, BraintreePaymentResultListener, IBrainTreeCallback {

    private static final String TAG = "YSBrainTreePayActivity";
    private BraintreeFragment mBrainTreeFragment;
    private String mDeviceData;

    @Override
    public final void onCancel(int i) {
        LogUtils.d(TAG, "BrainTree Pay canceled");
        PayResultMgr.getInstance().dispatchPayCancel(PayType.GOOGLE_PAY);
    }

    @Override
    public final void onError(Exception error) {
        LogUtils.d(TAG, "BrainTree Pay error:" + error.getMessage());
        BrainTreeListenerHandler.handleError(error);
    }

    @Override
    public final void onConfigurationFetched(Configuration configuration) {
        LogUtils.d(TAG, "onConfigurationFetched");
        collectDeviceData();
        onPaymentConfigurationFetched(configuration);
    }

    @Override
    public final void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        LogUtils.d(TAG, "onPaymentMethodNonceCreated");
        BrainTreeListenerHandler.handlerPaymentMethodNonceCreated(paymentMethodNonce, mDeviceData, this);
    }

    @Override
    public final void onBraintreePaymentResult(BraintreePaymentResult braintreePaymentResult) {
        LogUtils.d(TAG, "onBrainTreePaymentResult");
        BrainTreeListenerHandler.handleBrainTreePaymentResult(braintreePaymentResult);
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
    public void onPaymentNonceFetched(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {

    }

    @Override
    public void onPaymentNonceFetched(CardNonce cardNonce, String deviceData) {

    }

    @Override
    public void onPaymentNonceFetched(VisaCheckoutNonce visaCheckoutNonce, String deviceData) {

    }

    @Override
    public void onPaymentNonceFetched(VenmoAccountNonce venmoAccountNonce, String deviceData) {

    }

    @Override
    public void onPaymentNonceFetched(LocalPaymentResult localPaymentResult, String deviceData) {

    }

    @Override
    public void onPaymentNonceFetched(PayPalAccountNonce payPalAccountNonce, String deviceData) {

    }

}
