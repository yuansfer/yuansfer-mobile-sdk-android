package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.util.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.GooglePayReq
import com.pockyt.pay.resp.GooglePayResp
import com.pockyt.pay.util.IntentExtras
import com.pockyt.pay.util.StartForResultManager

class GooglePayStrategy: IPaymentStrategy<GooglePayReq, GooglePayResp>, StartForResultManager.Callback {

    private var payResp: ((GooglePayResp) -> Unit)? = null

    override fun requestPay(req: GooglePayReq, resp: (GooglePayResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(GooglePayResp(PockytCodes.DUPLICATE, "Google Pay is already in progress."))
            return
        }
        payResp = resp
        val args = Bundle().apply {
            putString(IntentExtras.EXTRA_TOKEN, req.clientToken)
            putParcelable(IntentExtras.EXTRA_CLIENT_REQUEST, req.request)
            putBoolean(IntentExtras.EXTRA_AUTO_DEVICE_DATA, req.autoDeviceData)
            putString(IntentExtras.EXTRA_SCHEMA, CustomPayActivity.GOOGLE_PAY_SCHEMA)
        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(GooglePayActivity::class.java)
            .startForResult(this)
    }

    override fun onResultError() {
        payResp?.invoke(GooglePayResp(PockytCodes.ERROR, "Result error"))
        payResp = null
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                payResp?.invoke(
                    GooglePayResp(
                        PockytCodes.SUCCESS,
                        paymentNonce = data?.getParcelableExtra(IntentExtras.EXTRA_NONCE_RESULT),
                        deviceData = data?.getStringExtra(IntentExtras.EXTRA_DEVICE_DATA)
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(GooglePayResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(GooglePayResp(PockytCodes.ERROR, data?.getStringExtra(IntentExtras.EXTRA_ERROR)))
            }
        }
        payResp = null
    }
}