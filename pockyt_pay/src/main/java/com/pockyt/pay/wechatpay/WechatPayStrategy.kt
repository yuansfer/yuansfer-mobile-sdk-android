package com.pockyt.pay.wechatpay

import android.content.Context
import android.content.Intent
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.WechatPayReq
import com.pockyt.pay.resp.WechatPayResp
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WechatPayStrategy: IPaymentStrategy<WechatPayReq, WechatPayResp> {

    companion object {
        private lateinit var mWXApi: IWXAPI
        var payResp: ((WechatPayResp) -> Unit)? = null

        private fun isInitialized(): Boolean {
            return ::mWXApi.isInitialized
        }

        @JvmStatic
        fun registerApi(context: Context, appId: String): Boolean {
            mWXApi = WXAPIFactory.createWXAPI(context.applicationContext, appId)
            return mWXApi.registerApp(appId)
        }

        @JvmStatic
        fun isInstalled(): Boolean {
            return if (isInitialized()) {
                mWXApi.isWXAppInstalled
            } else {
                false
            }
        }

        @JvmStatic
        fun isSupportPayApi(): Boolean {
            return if (isInitialized()) {
                mWXApi.wxAppSupportAPI >= Build.PAY_SUPPORTED_SDK_INT
            } else {
                false
            }
        }

        @JvmStatic
        fun handleIntent(intent: Intent, eventHandler: IWXAPIEventHandler?): Boolean {
            return if (isInitialized()) {
                mWXApi.handleIntent(intent, eventHandler)
            } else {
                false
            }
        }
    }

    override fun requestPay(req: WechatPayReq, resp: (WechatPayResp) -> Unit) {
        if (isInitialized()) {
            val payReq = PayReq()
            payReq.appId = req.appId
            payReq.partnerId = req.partnerId
            payReq.prepayId = req.prepayId
            payReq.packageValue = req.packageValue
            payReq.nonceStr = req.nonceStr
            payReq.timeStamp = req.timeStamp
            payReq.sign = req.sign
            mWXApi.sendReq(payReq)
            payResp = resp
        } else {
            throw IllegalStateException("WXApi is not initialized. Please call registerApi() first.")
        }
    }
}
