package com.pockyt.pay.braintree

import android.content.Intent
import com.braintreepayments.api.*
import com.pockyt.pay.util.IntentExtras

class ThreeDActivity : CustomPayActivity(), ThreeDSecureListener {

    override fun handleIntent(intent: Intent) {
        val threeDSecureRequest = intent.getParcelableExtra(IntentExtras.EXTRA_CLIENT_REQUEST) as? ThreeDSecureRequest
        processThreeDSecure(threeDSecureRequest!!)
    }

    private fun processThreeDSecure(threeDSecureRequest: ThreeDSecureRequest) {
        ThreeDSecureClient(this, braintreeClient).apply {
            setListener(this@ThreeDActivity)
            performVerification(
                this@ThreeDActivity,
                threeDSecureRequest
            ) { threeDSecureResult, error ->
                if (threeDSecureResult != null) {
                    this.continuePerformVerification(
                        this@ThreeDActivity,
                        threeDSecureRequest,
                        threeDSecureResult
                    )
                } else {
                    handleError(error)
                }
            }
        }
    }

    override fun onThreeDSecureSuccess(threeDSecureResult: ThreeDSecureResult) {
        handleSuccess(threeDSecureResult.tokenizedCard!!)
    }

    override fun onThreeDSecureFailure(error: Exception) {
        handleError(error)
    }
}
