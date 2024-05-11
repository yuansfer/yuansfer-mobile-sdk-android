package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.*
import com.pockyt.pay.util.IntentExtras

abstract class CustomPayActivity : FragmentActivity() {

    private var autoDeviceData = false

    companion object {
        const val PAYPAL_SCHEMA = "com.pockyt.paypal"
        const val VENMO_SCHEMA = "com.pockyt.venmo"
        const val THREE_D_SCHEMA = "com.pockyt.threedsecure"
        const val GOOGLE_PAY_SCHEMA = "com.pockyt.googlepay"
    }

    protected val braintreeClient: BraintreeClient by lazy {
        BraintreeClient(this, intent.getStringExtra(IntentExtras.EXTRA_TOKEN) ?: ""
        , intent.getStringExtra(IntentExtras.EXTRA_SCHEMA) ?: "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDeviceData = intent.getBooleanExtra(IntentExtras.EXTRA_AUTO_DEVICE_DATA, false)
        handleIntent(intent)
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(intent)
        intent = newIntent
    }

    protected open fun handleSuccess(result: Parcelable) {
        Intent().apply {
            putExtra(IntentExtras.EXTRA_NONCE_RESULT, result)
            if (autoDeviceData) {
                DataCollector(braintreeClient).collectDeviceData(this@CustomPayActivity) { deviceData, _ ->
                    putExtra(IntentExtras.EXTRA_DEVICE_DATA, deviceData)
                    setResult(Activity.RESULT_OK, this)
                    finish()
                }
            } else {
                setResult(Activity.RESULT_OK, this)
                finish()
            }
        }
    }

    protected open fun handleCancellation() {
        setResult(RESULT_CANCELED)
        finish()
    }

    protected open fun handleError(error: Exception?) {
        val resultIntent = Intent().apply {
            putExtra(IntentExtras.EXTRA_ERROR, error?.message)
        }
        setResult(2, resultIntent)
        finish()
    }

    protected abstract fun handleIntent(intent: Intent)
}
