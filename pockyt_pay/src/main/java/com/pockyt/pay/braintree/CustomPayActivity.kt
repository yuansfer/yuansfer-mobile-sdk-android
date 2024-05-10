package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.*

class CustomPayActivity : FragmentActivity(), PayPalListener, VenmoListener, GooglePayListener, ThreeDSecureListener {

    private var autoDeviceData = false

    private val braintreeClient: BraintreeClient by lazy {
        BraintreeClient(this, intent.getStringExtra("token") ?: "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(intent)
        intent = newIntent
    }

    private fun handleIntent(intent: Intent) {
        autoDeviceData = intent.getBooleanExtra("autoDeviceData", false)
        when (val request = intent.getParcelableExtra("clientRequest") as? Parcelable) {
            is PayPalRequest -> {
                processPayPal(request)
            }
            is VenmoRequest -> {
                processVenmo(request)
            }
            is Card -> {
                val threeDSecureRequest = intent.getParcelableExtra("threeDSecureRequest") as? ThreeDSecureRequest
                processCard(request, threeDSecureRequest)
            }
            is GooglePayRequest -> {
                processGooglePay(request)
            }
        }
    }

    private fun processPayPal(request: PayPalRequest) {
        val payPalClient = PayPalClient(this, braintreeClient)
        payPalClient.setListener(this)
        payPalClient.tokenizePayPalAccount(this, request)
    }

    private fun processVenmo(request: VenmoRequest) {
        val venmoClient = VenmoClient(this, braintreeClient)
        venmoClient.setListener(this)
        venmoClient.tokenizeVenmoAccount(this, request)
    }

    private fun processCard(card: Card, threeDSecureRequest: ThreeDSecureRequest?) {
        val needThreeDSecure = threeDSecureRequest != null
        var threeDSecureClient: ThreeDSecureClient? = null
        val cardClient = CardClient(braintreeClient)
        if (needThreeDSecure) {
            threeDSecureClient= ThreeDSecureClient(this, braintreeClient).apply {
                setListener(this@CustomPayActivity)
            }
        }
        cardClient.tokenize(card) { cardNonce, error ->
            if (cardNonce != null) {
                if (needThreeDSecure) {
                    threeDSecureRequest?.nonce = cardNonce.string
                    threeDSecureClient?.performVerification(this@CustomPayActivity, threeDSecureRequest!!) { threeDSecureResult, error ->
                        if (threeDSecureResult != null) {
                            threeDSecureClient.continuePerformVerification(this@CustomPayActivity, threeDSecureRequest, threeDSecureResult)
                        } else {
                            handleError(error)
                        }
                    }
                } else {
                    handleSuccess(cardNonce)
                }
                return@tokenize
            }
            handleError(error)
        }
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

    override fun onThreeDSecureSuccess(threeDSecureResult: ThreeDSecureResult) {
        handleSuccess(threeDSecureResult.tokenizedCard!!)
    }

    override fun onThreeDSecureFailure(error: Exception) {
        handleError(error)
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

    private fun handleSuccess(result: Parcelable) {
        Intent().apply {
            putExtra("nonceResult", result)
            if (autoDeviceData) {
                DataCollector(braintreeClient).collectDeviceData(this@CustomPayActivity) { deviceData, _ ->
                    putExtra("deviceData", deviceData)
                    setResult(Activity.RESULT_OK, this)
                    finish()
                }
            } else {
                setResult(Activity.RESULT_OK, this)
                finish()
            }
        }
    }

    private fun handleCancellation() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun handleError(error: Exception?) {
        val resultIntent = Intent().apply {
            putExtra("error", error?.message)
        }
        setResult(2, resultIntent)
        finish()
    }
}
