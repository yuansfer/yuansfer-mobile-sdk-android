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
        @JvmStatic
        val alipayPay: PockytPay<AlipayReq, AlipayResp> by lazy { PockytPay(AlipayStrategy()) }
        @JvmStatic
        val wechatPay: PockytPay<WechatPayReq, WechatPayResp> by lazy { PockytPay(WechatPayStrategy()) }
        @JvmStatic
        val dropInPay: PockytPay<DropInReq, DropInResp> by lazy { PockytPay(DropInStrategy()) }
        @JvmStatic
        val cardPay: PockytPay<CardReq, CardResp> by lazy { PockytPay(CardStrategy()) }
        @JvmStatic
        val paypalPay: PockytPay<PayPalReq, PayPalResp> by lazy { PockytPay(PayPalStrategy()) }
        @JvmStatic
        val venmoPay: PockytPay<VenmoReq, VenmoResp> by lazy { PockytPay(VenmoStrategy()) }
        @JvmStatic
        val googlePay: PockytPay<GooglePayReq, GooglePayResp> by lazy { PockytPay(GooglePayStrategy()) }
        @JvmStatic
        val threeDPay: PockytPay<ThreeDReq, CardResp> by lazy { PockytPay(ThreeDStrategy()) }
    }
}