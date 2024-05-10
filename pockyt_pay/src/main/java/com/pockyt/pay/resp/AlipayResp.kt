package com.pockyt.pay.resp

import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.BaseResp

data class AlipayResp(
    override val respCode: String,
    override val respMsg: String?,
    val memo: String?
) : BaseResp(respCode, respMsg) {
    companion object {
        @JvmStatic
        fun fromJson(jsonMap: Map<String, String>): AlipayResp {
            return AlipayResp(
                jsonMap["resultStatus"] ?: "",
                jsonMap["result"],
                jsonMap["memo"]
            )
        }
    }

    val isSuccessful: Boolean
        get() = respCode == PockytCodes.ALIPAY_SUCCESS

    val isCancelled: Boolean
        get() = respCode == PockytCodes.ALIPAY_USER_CANCEL
}
