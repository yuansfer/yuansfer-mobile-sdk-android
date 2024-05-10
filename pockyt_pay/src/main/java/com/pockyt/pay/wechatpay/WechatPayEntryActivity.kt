package com.pockyt.pay.wechatpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pockyt.pay.resp.WechatPayResp
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WechatPayEntryActivity: Activity(), IWXAPIEventHandler {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WechatPayStrategy.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        WechatPayStrategy.handleIntent(intent, this)
    }

    override fun onReq(p0: BaseReq?) {
    }

    override fun onResp(p0: BaseResp?) {
        if (p0?.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WechatPayStrategy.payResp?.invoke(WechatPayResp(p0.errCode.toString(), p0.errStr))
            WechatPayStrategy.payResp = null
            finish()
        }
    }

}