package com.pockyt.pay.resp

import com.pockyt.pay.base.BaseResp
import com.pockyt.pay.util.PockytCodes

data class CashAppResp(
    override val respCode: String,
    override val respMsg: String? = null,
) : BaseResp(respCode, respMsg) {
    val isSuccessful: Boolean
        get() = respCode == PockytCodes.SUCCESS
    val isCancelled: Boolean
        get() = respCode == PockytCodes.CANCEL
}
