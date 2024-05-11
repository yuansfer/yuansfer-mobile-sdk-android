package com.pockyt.pay

import com.pockyt.pay.alipay.AlipayStrategy
import com.pockyt.pay.base.BaseReq
import com.pockyt.pay.base.BaseResp
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.braintree.*
import com.pockyt.pay.req.*
import com.pockyt.pay.resp.*
import com.pockyt.pay.wechatpay.WechatPayStrategy

class Pockyt<T : BaseReq, E : BaseResp>(private var paymentStrategy: IPaymentStrategy<T, E>) {
    fun requestPay(req: T, resp: (E) -> Unit) {
        paymentStrategy.requestPay(req, resp)
    }

    companion object {
        @JvmStatic
        val alipay: Pockyt<AlipayReq, AlipayResp> by lazy { Pockyt(AlipayStrategy()) }
        @JvmStatic
        val wechatPay: Pockyt<WechatPayReq, WechatPayResp> by lazy { Pockyt(WechatPayStrategy()) }
        @JvmStatic
        val dropInPay: Pockyt<DropInReq, DropInResp> by lazy { Pockyt(DropInStrategy()) }
        @JvmStatic
        val cardPay: Pockyt<CardReq, CardResp> by lazy { Pockyt(CardStrategy()) }
        @JvmStatic
        val paypal: Pockyt<PayPalReq, PayPalResp> by lazy { Pockyt(PayPalStrategy()) }
        @JvmStatic
        val venmoPay: Pockyt<VenmoReq, VenmoResp> by lazy { Pockyt(VenmoStrategy()) }
        @JvmStatic
        val googlePay: Pockyt<GooglePayReq, GooglePayResp> by lazy { Pockyt(GooglePayStrategy()) }
        @JvmStatic
        val threeDPay: Pockyt<ThreeDReq, CardResp> by lazy { Pockyt(ThreeDStrategy()) }
    }
}