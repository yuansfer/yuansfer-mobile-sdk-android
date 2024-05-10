package com.pockyt.pay.base

interface IPaymentStrategy<in T : BaseReq, out E : BaseResp> {
    fun requestPay(req: T, resp: (E) -> Unit)
}