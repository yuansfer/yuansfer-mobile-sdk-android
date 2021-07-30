package com.yuansfer.pay.braintree;

import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;

public interface IBTNonceCallback {

    void onPaymentNonceFetched(CardNonce cardNonce, String deviceData);

    void onPaymentNonceFetched(PayPalAccountNonce payPalAccountNonce, String deviceData);

    void onPaymentNonceFetched(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData);

    void onPaymentNonceFetched(VisaCheckoutNonce visaCheckoutNonce, String deviceData);

    void onPaymentNonceFetched(VenmoAccountNonce venmoAccountNonce, String deviceData);

    void onPaymentNonceFetched(LocalPaymentResult localPaymentResult, String deviceData);
}
