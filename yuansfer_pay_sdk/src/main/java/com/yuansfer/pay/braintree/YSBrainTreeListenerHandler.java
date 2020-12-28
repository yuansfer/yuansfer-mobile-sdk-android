package com.yuansfer.pay.braintree;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.GooglePayment;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.GooglePaymentException;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.BraintreePaymentResult;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;

public class YSBrainTreeListenerHandler {

    public static void handleError(Exception error) {
        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;
            BraintreeError cardErrors = errorWithResponse.errorFor("creditCard");
            if (cardErrors != null) {
                // There is an issue with the credit card.
                BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    PayResultMgr.getInstance().dispatchPayFail(PayType.GOOGLE_PAY
                            , ErrStatus.getInstance("G11", expirationMonthError.getMessage()));
                    return;
                }
            }
        } else if (error instanceof GooglePaymentException) {
            GooglePaymentException errorGooglePayment = (GooglePaymentException) error;
            PayResultMgr.getInstance().dispatchPayFail(PayType.GOOGLE_PAY
                    , ErrStatus.getInstance(String.valueOf("G" + errorGooglePayment.getStatus().getStatusCode())
                            , errorGooglePayment.getStatus().zzg()));
            return;
        }
        PayResultMgr.getInstance().dispatchPayFail(PayType.GOOGLE_PAY
                , ErrStatus.getInstance("G10", error.getMessage()));
    }

    public static void handleConfigurationFetched(BraintreeFragment braintreeFragment, Configuration configuration, final IBrainTreeCallback callback) {
        if (configuration.getGooglePayment().isEnabled(braintreeFragment.getContext())) {
            GooglePayment.isReadyToPay(braintreeFragment, new BraintreeResponseListener<Boolean>() {
                @Override
                public void onResponse(Boolean isReadyToPay) {
                    if (isReadyToPay) {
                        callback.onReadyToPay();
                    } else {
                        handleError(new Exception("Google Payments are not available. The following issues could be the cause:\n\n" +
                                "No user is logged in to the device.\n\n" +
                                "Google Play Services is missing or out of date."));
                    }
                }
            });
        } else {
            handleError(new Exception("Google Payments are not available. The following issues could be the cause:\n\n" +
                    "Google Payments are not enabled for the current merchant.\n\n" +
                    "Google Play Services is missing or out of date."));
        }
    }

    public static void handlerPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce, String deviceData, IBrainTreeCallback callback) {
        if (paymentMethodNonce instanceof CardNonce) {
            callback.onPaymentMethodResult((CardNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof PayPalAccountNonce) {
            callback.onPaymentMethodResult((PayPalAccountNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof GooglePaymentCardNonce) {
            callback.onPaymentMethodResult((GooglePaymentCardNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof VisaCheckoutNonce) {
            callback.onPaymentMethodResult((VisaCheckoutNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof VenmoAccountNonce) {
            callback.onPaymentMethodResult((VenmoAccountNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof LocalPaymentResult) {
            callback.onPaymentMethodResult((LocalPaymentResult) paymentMethodNonce, deviceData);
        }
    }

    public static void handleBrainTreePaymentResult(BraintreePaymentResult braintreePaymentResult) {
        PayResultMgr.getInstance().dispatchPaySuccess(PayType.GOOGLE_PAY);
    }

}
