package com.pockyt.pay.braintree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.DropInReq
import com.pockyt.pay.resp.DropInResp
import com.pockyt.pay.util.StartForResultManager

class DropInStrategy: IPaymentStrategy<DropInReq, DropInResp>, StartForResultManager.Callback {

    private var payResp: ((DropInResp) -> Unit)? = null

    override fun requestPay(req: DropInReq, resp: (DropInResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(DropInResp(
                PockytCodes.ERROR
                ,"DropIn is already in progress"))
            return
        }
        payResp = resp
        val args = Bundle()
        args.putString("token", req.clientToken)
        args.putParcelable("dropInRequest", req.request)
        StartForResultManager.get().from(req.activity)
            .bundle(args)
            .to(DropInPayActivity::class.java)
            .startForResult(this)
    }

    override fun onResultError() {
        payResp?.invoke(DropInResp(PockytCodes.ERROR, "Result error"))
        payResp = null
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                payResp?.invoke(DropInResp(PockytCodes.SUCCESS
                    , dropInResult = data?.getParcelableExtra("dropInResult")))
            }
            Activity.RESULT_CANCELED -> {
                payResp?.invoke(DropInResp(PockytCodes.CANCEL, "User canceled"))
            }
            else -> {
                payResp?.invoke(DropInResp(PockytCodes.ERROR, data?.getStringExtra("error")))
            }
        }
        payResp = null
    }

}