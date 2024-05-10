package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.VenmoReq
import com.pockyt.pay.resp.VenmoResp
import com.pockyt.pay.util.StartForResultManager

class VenmoStrategy : IPaymentStrategy<VenmoReq, VenmoResp>, StartForResultManager.Callback {
    private var payResp: ((VenmoResp) -> Unit)? = null

    override fun requestPay(req: VenmoReq, resp: (VenmoResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(VenmoResp(PockytCodes.ERROR, "Venmo payment is already in progress."))
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
        payResp?.invoke(VenmoResp(PockytCodes.ERROR, "Result error"))
        payResp = null
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                payResp?.invoke(
                    VenmoResp(
                        PockytCodes.SUCCESS,
                        venmoNonce = data?.getParcelableExtra("nonceResult"),
                        deviceData = data?.getStringExtra("deviceData")
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(VenmoResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(VenmoResp(PockytCodes.ERROR, data?.getStringExtra("error")))
            }
        }
        payResp = null
    }
}

