package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.util.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.PayPalReq
import com.pockyt.pay.resp.PayPalResp
import com.pockyt.pay.util.IntentExtras
import com.pockyt.pay.util.StartForResultManager

class PayPalStrategy : IPaymentStrategy<PayPalReq, PayPalResp>, StartForResultManager.Callback {
    private var payResp: ((PayPalResp) -> Unit)? = null

    override fun requestPay(req: PayPalReq, resp: (PayPalResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(PayPalResp(PockytCodes.ERROR, "PayPal payment is already in progress."))
            return
        }
        payResp = resp
        val args = Bundle().apply {
            putString(IntentExtras.EXTRA_TOKEN, req.clientToken)
            putBoolean(IntentExtras.EXTRA_AUTO_DEVICE_DATA, req.autoDeviceData)
            putString(IntentExtras.EXTRA_SCHEMA, CustomPayActivity.PAYPAL_SCHEMA)
            putParcelable(IntentExtras.EXTRA_CLIENT_REQUEST, req.request)
        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(PayPalActivity::class.java)
            .startForResult(this)
    }

    override fun onResultError() {
        payResp?.invoke(PayPalResp(PockytCodes.ERROR, "Result error"))
        payResp = null
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                payResp?.invoke(
                    PayPalResp(
                        PockytCodes.SUCCESS,
                        paypalNonce = data?.getParcelableExtra(IntentExtras.EXTRA_NONCE_RESULT),
                        deviceData = data?.getStringExtra(IntentExtras.EXTRA_DEVICE_DATA)
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(PayPalResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(PayPalResp(PockytCodes.ERROR, data?.getStringExtra(IntentExtras.EXTRA_ERROR)))
            }
        }
        payResp = null
    }
}

