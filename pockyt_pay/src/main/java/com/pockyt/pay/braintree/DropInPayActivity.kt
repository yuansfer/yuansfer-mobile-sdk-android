package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.*
import com.pockyt.pay.util.IntentExtras

class DropInPayActivity: FragmentActivity(), DropInListener {

    private lateinit var dropInClient: DropInClient
    private var dropInRequest: DropInRequest? = null
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent: Intent = intent
        val token = intent.getStringExtra(IntentExtras.EXTRA_TOKEN)
        dropInClient = DropInClient(this, token)
        dropInClient.setListener(this)
        dropInRequest = intent.getParcelableExtra(IntentExtras.EXTRA_DROP_IN_REQUEST)
    }

    override fun onStart() {
        super.onStart()
        if (!started) {
            started = true
            dropInClient.launchDropIn(dropInRequest)
        }
    }

    override fun onDropInSuccess(dropInResult: DropInResult) {
        started = false
        val result = Intent()
        result.putExtra(IntentExtras.EXTRA_DROP_IN_RESULT, dropInResult)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onDropInFailure(error: Exception) {
        started = false
        if (error is UserCanceledException) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            val result = Intent()
            result.putExtra(IntentExtras.EXTRA_ERROR, error.message)
            setResult(-2, result)
        }
        finish()
    }
}