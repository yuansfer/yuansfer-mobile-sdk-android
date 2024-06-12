package com.pockyt.pay

import com.pockyt.pay.alipay.AlipayStrategy
import com.pockyt.pay.base.BaseReq
import com.pockyt.pay.base.BaseResp
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.braintree.*
import com.pockyt.pay.cashapp.CashAppStrategy
import com.pockyt.pay.req.*
import com.pockyt.pay.resp.*
import com.pockyt.pay.wechatpay.WechatPayStrategy

class Pockyt<T : BaseReq, E : BaseResp>(private var paymentStrategy: IPaymentStrategy<T, E>) {
    fun requestPay(req: T, resp: (E) -> Unit) {
        paymentStrategy.requestPay(req, resp)
    }

    companion object {
        @JvmStatic
        fun createAlipay(): Pockyt<AlipayReq, AlipayResp> = createPockyt(AlipayStrategy())

        @JvmStatic
        fun createWechatPay(): Pockyt<WechatPayReq, WechatPayResp> = createPockyt(WechatPayStrategy())

        @JvmStatic
        fun createDropIn(): Pockyt<DropInReq, DropInResp> = createPockyt(DropInStrategy())

        @JvmStatic
        fun createCardPay(): Pockyt<CardReq, CardResp> = createPockyt(CardStrategy())

        @JvmStatic
        fun createPaypal(): Pockyt<PayPalReq, PayPalResp> = createPockyt(PayPalStrategy())

        @JvmStatic
        fun createVenmo(): Pockyt<VenmoReq, VenmoResp> = createPockyt(VenmoStrategy())

        @JvmStatic
        fun createGooglePay(): Pockyt<GooglePayReq, GooglePayResp> = createPockyt(GooglePayStrategy())

        @JvmStatic
        fun createThreeDSecure(): Pockyt<ThreeDReq, CardResp> = createPockyt(ThreeDStrategy())

        @JvmStatic
        fun createCashApp(): Pockyt<CashAppReq, CashAppResp> = createPockyt(CashAppStrategy())

        private inline fun <reified T : BaseReq, reified E : BaseResp> createPockyt(strategy: IPaymentStrategy<T, E>): Pockyt<T, E> {
            return Pockyt(strategy)
        }
    }
}