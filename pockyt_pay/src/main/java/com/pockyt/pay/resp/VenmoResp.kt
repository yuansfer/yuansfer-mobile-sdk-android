package com.pockyt.pay.resp

import com.braintreepayments.api.VenmoAccountNonce
import com.pockyt.pay.PockytCodes
import com.pockyt.pay.base.BaseResp

data class VenmoResp(
    override val respCode: String,
    override val respMsg: String? = null,
    val venmoNonce: VenmoAccountNonce? = null,
    val deviceData: String? = null
) : BaseResp(respCode, respMsg) {
    val isSuccessful: Boolean
        get() = respCode == PockytCodes.SUCCESS
    val isCancelled: Boolean
        get() = respCode == PockytCodes.CANCEL
}
