package com.pockyt.pay.resp

import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.BaseResp

data class WechatPayResp(
    override val respCode: String,
    override val respMsg: String?,
) : BaseResp(respCode, respMsg) {
    val isSuccessful: Boolean
        get() = respCode == PockytCodes.SUCCESS
    val isCancelled: Boolean
        get() = respCode == PockytCodes.CANCEL
}
