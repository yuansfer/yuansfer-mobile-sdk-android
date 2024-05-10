package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.GooglePayReq
import com.pockyt.pay.resp.GooglePayResp
import com.pockyt.pay.util.StartForResultManager

class GooglePayStrategy: IPaymentStrategy<GooglePayReq, GooglePayResp>, StartForResultManager.Callback {

    private var payResp: ((GooglePayResp) -> Unit)? = null

    override fun requestPay(req: GooglePayReq, resp: (GooglePayResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(GooglePayResp(PockytCodes.ERROR, "Google Pay is already in progress."))
            return
        }
        payResp = resp
        val args = Bundle().apply {
            putString("token", req.clientToken)
            putParcelable("clientRequest", req.request)
            putBoolean("autoDeviceData", req.autoDeviceData)
        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(CustomPayActivity::class.java)
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
                        paymentNonce = data?.getParcelableExtra("nonceResult"),
                        deviceData = data?.getStringExtra("deviceData")
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(GooglePayResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(GooglePayResp(PockytCodes.ERROR, data?.getStringExtra("error")))
            }
        }
        payResp = null
    }
}