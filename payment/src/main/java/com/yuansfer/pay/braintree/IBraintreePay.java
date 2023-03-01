package com.yuansfer.pay.braintree;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PayPalRequest;

public interface IBraintreePay {
    <T extends BTCustomPayActivity> void bindBrainTree(T activity, String authorization);
    <T extends BTCustomPayActivity> void unbindBrainTree(T activity);
    <T extends BTCustomPayActivity> void requestGooglePayment(T activity, GooglePaymentRequest googlePaymentRequest);
    <T extends BTCustomPayActivity> void requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest);
    <T extends BTCustomPayActivity> void requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest);
    <T extends BTCustomPayActivity> void requestVenmoPayment(T activity, boolean vault);
    <T extends BTCustomPayActivity> void requestCardPayment(T activity, CardBuilder cardBuilder);
    <T extends BTDropInActivity> void requestDropInPayment(T activity, String authorization, DropInRequest dropInRequest);
}
