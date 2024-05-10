package com.pockyt.pay.resp

import com.braintreepayments.api.DropInResult
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.BaseResp

data class DropInResp(
    override val respCode: String,
    override val respMsg: String? = null,
    val dropInResult: DropInResult? = null
) : BaseResp(respCode, respMsg) {
    val isSuccessful: Boolean
        get() = respCode == PockytCodes.SUCCESS

    val isCancelled: Boolean
        get() = respCode == PockytCodes.CANCEL
}
