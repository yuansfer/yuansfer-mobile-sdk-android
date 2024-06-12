package com.pockyt.pay.braintree

import android.content.Context
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.CardClient
import com.braintreepayments.api.CardNonce
import com.braintreepayments.api.DataCollector
import com.pockyt.pay.util.PockytCodes
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.CardReq
import com.pockyt.pay.resp.CardResp

class CardStrategy: IPaymentStrategy<CardReq, CardResp> {
    private var payResp: ((CardResp) -> Unit)? = null
    private var braintreeClient: BraintreeClient? = null

    override fun requestPay(req: CardReq, resp: (CardResp) -> Unit) {
        if (payResp != null) {
            resp.invoke(CardResp(PockytCodes.DUPLICATE, "Card payment is already in progress."))
            return
        }
        payResp = resp
        braintreeClient = BraintreeClient(req.activity, req.clientToken)
        val cardClient = CardClient(braintreeClient!!)
        cardClient.tokenize(req.card) { cardNonce, error ->
            if (cardNonce != null) {
                handleSuccess(req.activity.baseContext, cardNonce, req.autoDeviceData)
            } else {
                handleError(error)
            }
        }
    }

    private fun handleSuccess(context:Context, cardNonce: CardNonce?, autoDeviceData: Boolean = false) {
        if (autoDeviceData) {
            DataCollector(braintreeClient!!).collectDeviceData(context) { deviceData, _ ->
                payResp?.invoke(CardResp(
                    PockytCodes.SUCCESS,
                    cardNonce = cardNonce,
                    deviceData = deviceData
                ))
            }
        } else {
            payResp?.invoke(CardResp(
                PockytCodes.SUCCESS,
                cardNonce = cardNonce
            ))
        }
        payResp = null
    }

    private fun handleError(error: Exception?) {
        payResp?.invoke(CardResp(PockytCodes.ERROR, error?.message))
        payResp = null
    }

}