package com.yuansfer.pay.braintree;

import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;

public interface IBrainTreeCallback {

    /**
     * 支付发起已可用，一般用于显示支付发起按钮
     */
    void onReadyToPay();

    /**
     * 获取支付方式随机数和设备信息
     */
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData);

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData);

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData);

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData);

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData);

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData);
}
