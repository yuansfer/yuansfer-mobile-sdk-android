package com.yuansfer.pay.braintree;

import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.GooglePaymentException;
import com.braintreepayments.api.models.BraintreePaymentResult;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;

public class BrainTreeListenerHandler {

    /**
     * 解析处理BraintreeErrorListener
     * @param error
     */
    public static void handleError(Exception error) {
        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;
            BraintreeError cardErrors = errorWithResponse.errorFor("creditCard");
            if (cardErrors != null) {
                // There is an issue with the credit card.
                BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    PayResultMgr.getInstance().dispatchPayFail(PayType.BRAIN_TREE
                            , ErrStatus.getInstance("B01", expirationMonthError.getMessage()));
                    return;
                }
            }
            PayResultMgr.getInstance().dispatchPayFail(PayType.BRAIN_TREE
                    , ErrStatus.getInstance("B02", errorWithResponse.getErrorResponse()));
        } else if (error instanceof GooglePaymentException) {
            GooglePaymentException errorGooglePayment = (GooglePaymentException) error;
            PayResultMgr.getInstance().dispatchPayFail(PayType.GOOGLE_PAY
                    , ErrStatus.getInstance(String.valueOf("G" + errorGooglePayment.getStatus().getStatusCode())
                            , errorGooglePayment.getStatus().zzg()));
        } else {
            PayResultMgr.getInstance().dispatchPayFail(PayType.BRAIN_TREE
                    , ErrStatus.getInstance("B03", error.getMessage()));
        }
    }

    /**
     * 解析处理PaymentMethodNonceCreatedListener，
     * @param paymentMethodNonce
     * @param deviceData
     * @param callback
     */
    public static void handlerPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce, String deviceData, IBrainTreeCallback callback) {
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

    public static void handleBrainTreePaymentResult(BraintreePaymentResult braintreePaymentResult) {
        PayResultMgr.getInstance().dispatchPaySuccess(PayType.BRAIN_TREE);
    }

}
