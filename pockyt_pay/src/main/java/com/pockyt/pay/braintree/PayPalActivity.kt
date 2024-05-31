package com.pockyt.pay.braintree

import android.content.Intent
import com.braintreepayments.api.*
import com.pockyt.pay.util.IntentExtras

class PayPalActivity : CustomPayActivity(), PayPalListener {

    override fun handleIntent(intent: Intent) {
        val paypalRequest = intent.getParcelableExtra(IntentExtras.EXTRA_CLIENT_REQUEST) as? PayPalRequest
        processPayPal(paypalRequest!!)
    }

    private fun processPayPal(request: PayPalRequest) {
        val payPalClient = PayPalClient(this, braintreeClient)
        payPalClient.setListener(this)
        payPalClient.tokenizePayPalAccount(this, request)
    }

    override fun onPayPalSuccess(payPalAccountNonce: PayPalAccountNonce) {
        handleSuccess(payPalAccountNonce)
    }

    override fun onPayPalFailure(error: Exception) {
        if (error is UserCanceledException) {
            handleCancellation()
        } else {
            handleError(error)
        }
    }

}
