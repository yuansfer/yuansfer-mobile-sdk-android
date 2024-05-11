package com.pockyt.pay

import com.pockyt.pay.alipay.AlipayStrategy
import com.pockyt.pay.base.BaseReq
import com.pockyt.pay.base.BaseResp
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.braintree.*
import com.pockyt.pay.req.*
import com.pockyt.pay.resp.*
import com.pockyt.pay.wechatpay.WechatPayStrategy

class PockytPay<T : BaseReq, E : BaseResp>(private var paymentStrategy: IPaymentStrategy<T, E>) {
    fun requestPay(req: T, resp: (E) -> Unit) {
        paymentStrategy.requestPay(req, resp)
    }

    companion object {
        val alipayPay: PockytPay<AlipayReq, AlipayResp> by lazy { PockytPay(AlipayStrategy()) }

        val wechatPay: PockytPay<WechatPayReq, WechatPayResp> by lazy { PockytPay(WechatPayStrategy()) }

        val dropInPay: PockytPay<DropInReq, DropInResp> by lazy { PockytPay(DropInStrategy()) }

        val cardPay: PockytPay<CardReq, CardResp> by lazy { PockytPay(CardStrategy()) }

        val paypalPay: PockytPay<PayPalReq, PayPalResp> by lazy { PockytPay(PayPalStrategy()) }

        val venmoPay: PockytPay<VenmoReq, VenmoResp> by lazy { PockytPay(VenmoStrategy()) }

        val googlePay: PockytPay<GooglePayReq, GooglePayResp> by lazy { PockytPay(GooglePayStrategy()) }

        val threeDPay: PockytPay<ThreeDReq, CardResp> by lazy { PockytPay(ThreeDStrategy()) }
    }
}