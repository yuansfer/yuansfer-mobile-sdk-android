package com.pockyt.pay.braintree

import android.content.Intent
import com.braintreepayments.api.*
import com.pockyt.pay.util.IntentExtras

class VenmoActivity : CustomPayActivity(), VenmoListener {

    override fun handleIntent(intent: Intent) {
        val venmo = intent.getParcelableExtra(IntentExtras.EXTRA_CLIENT_REQUEST) as? VenmoRequest
        processVenmo(venmo!!)
    }

    private fun processVenmo(request: VenmoRequest) {
        val venmoClient = VenmoClient(this, braintreeClient)
        venmoClient.setListener(this)
        venmoClient.tokenizeVenmoAccount(this, request)
    }

    override fun onVenmoSuccess(venmoAccountNonce: VenmoAccountNonce) {
        handleSuccess(venmoAccountNonce)
    }

    override fun onVenmoFailure(error: Exception) {
        if (error is UserCanceledException) {
            handleCancellation()
        } else {
            handleError(error)
        }
    }

}
