package com.yuansfer.pay.braintree;

import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.GooglePaymentException;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.ErrStatus;

public class BTListenerHandler {

    /**
     * 解析处理BraintreeErrorListener
     */
    public static void handleError(Exception error, IBTPrepayCallback callback) {
        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;
            BraintreeError cardErrors = errorWithResponse.errorFor("creditCard");
            if (cardErrors != null) {
                // There is an issue with the credit card.
                BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    callback.onPrepayError(ErrStatus.getInstance(ErrStatus.CARD_EXPIRE_ERROR, expirationMonthError.getMessage()));
                    return;
                }
            }
            callback.onPrepayError(ErrStatus.getInstance(ErrStatus.BT_RESPONSE_ERROR, errorWithResponse.getErrorResponse()));
        } else if (error instanceof GooglePaymentException) {
            GooglePaymentException errorGooglePayment = (GooglePaymentException) error;
            callback.onPrepayError(ErrStatus.getInstance(ErrStatus.GGPAY_COMMON_ERROR
                            , errorGooglePayment.getMessage()));
        } else {
            callback.onPrepayError(ErrStatus.getInstance(ErrStatus.BT_UNKNOWN_ERROR, error.getMessage()));
        }
    }

    /**
     * 解析处理PaymentMethodNonceCreatedListener，
     */
    public static void handlerPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce, String deviceData, IBTNonceCallback callback) {
        if (paymentMethodNonce instanceof CardNonce) {
            callback.onPaymentNonceFetched((CardNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof PayPalAccountNonce) {
            callback.onPaymentNonceFetched((PayPalAccountNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof GooglePaymentCardNonce) {
            callback.onPaymentNonceFetched((GooglePaymentCardNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof VisaCheckoutNonce) {
            callback.onPaymentNonceFetched((VisaCheckoutNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof VenmoAccountNonce) {
            callback.onPaymentNonceFetched((VenmoAccountNonce) paymentMethodNonce, deviceData);
        } else if (paymentMethodNonce instanceof LocalPaymentResult) {
            callback.onPaymentNonceFetched((LocalPaymentResult) paymentMethodNonce, deviceData);
        }
    }

}
