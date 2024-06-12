package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.ThreeDSecureClient
import com.pockyt.pay.util.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.ThreeDReq
import com.pockyt.pay.resp.CardResp
import com.pockyt.pay.util.IntentExtras
import com.pockyt.pay.util.StartForResultManager

class ThreeDStrategy: IPaymentStrategy<ThreeDReq, CardResp>, StartForResultManager.Callback {
    private var payResp: ((CardResp) -> Unit)? = null

    override fun requestPay(req: ThreeDReq, resp: (CardResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(CardResp(PockytCodes.DUPLICATE, "ThreeD secure is already in progress."))
            return
        }
        payResp = resp
        val args = Bundle().apply {
            putString(IntentExtras.EXTRA_TOKEN, req.clientToken)
            putParcelable(IntentExtras.EXTRA_CLIENT_REQUEST, req.threeDSecureRequest)
            putBoolean(IntentExtras.EXTRA_AUTO_DEVICE_DATA, req.autoDeviceData)
            putString(IntentExtras.EXTRA_SCHEMA, CustomPayActivity.THREE_D_SCHEMA)

        }
        StartForResultManager.get()
            .from(req.activity)
            .bundle(args)
            .to(ThreeDActivity::class.java)
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
                        cardNonce = data?.getParcelableExtra(IntentExtras.EXTRA_NONCE_RESULT),
                        deviceData = data?.getStringExtra(IntentExtras.EXTRA_DEVICE_DATA)
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(CardResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(CardResp(PockytCodes.ERROR, data?.getStringExtra(IntentExtras.EXTRA_ERROR)))
            }
        }
        payResp = null
    }

}