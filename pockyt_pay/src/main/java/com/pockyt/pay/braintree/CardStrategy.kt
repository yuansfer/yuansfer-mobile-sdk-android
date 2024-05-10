package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.CardReq
import com.pockyt.pay.resp.CardResp
import com.pockyt.pay.util.StartForResultManager

class CardStrategy: IPaymentStrategy<CardReq, CardResp>, StartForResultManager.Callback {
    private var payResp: ((CardResp) -> Unit)? = null

    override fun requestPay(req: CardReq, resp: (CardResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(CardResp(PockytCodes.ERROR, "Card payment is already in progress."))
            return
        }
        payResp = resp
        val args = Bundle().apply {
            putString("token", req.clientToken)
            putParcelable("clientRequest", req.card)
            putParcelable("threeDSecureRequest", req.threeDSecureRequest)
            putBoolean("autoDeviceData", req.autoDeviceData)
        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(CustomPayActivity::class.java)
            .startForResult(this)
    }

    override fun onResultError() {
        payResp?.invoke(CardResp(PockytCodes.ERROR, "Result error"))
        payResp = null
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                payResp?.invoke(
                    CardResp(
                        PockytCodes.SUCCESS,
                        cardNonce = data?.getParcelableExtra("nonceResult"),
                        deviceData = data?.getStringExtra("deviceData")
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(CardResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(CardResp(PockytCodes.ERROR, data?.getStringExtra("error")))
            }
        }
        payResp = null
    }
}