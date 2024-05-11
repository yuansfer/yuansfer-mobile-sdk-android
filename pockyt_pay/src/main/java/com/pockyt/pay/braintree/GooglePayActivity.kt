package com.pockyt.pay.braintree

import android.content.Intent
import com.braintreepayments.api.*
import com.pockyt.pay.util.IntentExtras

class GooglePayActivity : CustomPayActivity(), GooglePayListener {

    override fun handleIntent(intent: Intent) {
        val googlePay = intent.getParcelableExtra(IntentExtras.EXTRA_CLIENT_REQUEST) as? GooglePayRequest
        processGooglePay(googlePay!!)
    }

    private fun processGooglePay(request: GooglePayRequest) {
        val googlePayClient = GooglePayClient(this, braintreeClient)
        googlePayClient.isReadyToPay(this) { isReadyToPay, error ->
            if (isReadyToPay) {
                googlePayClient.setListener(this)
                googlePayClient.requestPayment(this, request)
            } else {
                handleError(error ?: Exception("Google Pay is not ready to pay"))
            }
        }
    }

    override fun onGooglePaySuccess(paymentMethodNonce: PaymentMethodNonce) {
        handleSuccess(paymentMethodNonce)
    }

    override fun onGooglePayFailure(error: java.lang.Exception) {
        if (error is UserCanceledException) {
            handleCancellation()
        } else {
            handleError(error)
        }
    }

}
