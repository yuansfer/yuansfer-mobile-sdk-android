package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.braintreepayments.api.*
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.PPWrapRequest
import com.pockyt.pay.req.PayPalReq
import com.pockyt.pay.resp.PayPalResp
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
            putString("token", req.clientToken)
            putBoolean("autoDeviceData", req.autoDeviceData)
            val request = req.request
            if (request is PPWrapRequest.Checkout) {
                // with amount
                putParcelable("clientRequest", request.checkoutRequest)
            } else if (request is PPWrapRequest.Vault) {
                // without amount
                putParcelable("clientRequest", request.vaultRequest)
            }
        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(CustomPayActivity::class.java)
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
                        paypalNonce = data?.getParcelableExtra("nonceResult"),
                        deviceData = data?.getStringExtra("deviceData")
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(PayPalResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(PayPalResp(PockytCodes.ERROR, data?.getStringExtra("error")))
            }
        }
        payResp = null
    }
}

