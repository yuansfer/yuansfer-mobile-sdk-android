package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.util.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.VenmoReq
import com.pockyt.pay.resp.VenmoResp
import com.pockyt.pay.util.IntentExtras
import com.pockyt.pay.util.StartForResultManager

class VenmoStrategy : IPaymentStrategy<VenmoReq, VenmoResp>, StartForResultManager.Callback {
    private var payResp: ((VenmoResp) -> Unit)? = null

    override fun requestPay(req: VenmoReq, resp: (VenmoResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(VenmoResp(PockytCodes.DUPLICATE, "Venmo payment is already in progress."))
            return
        }
        payResp = resp
        val args = Bundle().apply {
            putString(IntentExtras.EXTRA_TOKEN, req.clientToken)
            putParcelable(IntentExtras.EXTRA_CLIENT_REQUEST, req.request)
            putBoolean(IntentExtras.EXTRA_AUTO_DEVICE_DATA, req.autoDeviceData)
            putString(IntentExtras.EXTRA_SCHEMA, CustomPayActivity.VENMO_SCHEMA)
        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(VenmoActivity::class.java)
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
                        venmoNonce = data?.getParcelableExtra(IntentExtras.EXTRA_NONCE_RESULT),
                        deviceData = data?.getStringExtra(IntentExtras.EXTRA_DEVICE_DATA)
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(VenmoResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(VenmoResp(PockytCodes.ERROR, data?.getStringExtra(IntentExtras.EXTRA_ERROR)))
            }
        }
        payResp = null
    }
}

