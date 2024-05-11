package com.pockyt.pay.resp

import com.braintreepayments.api.CardNonce
import com.pockyt.pay.util.PockytCodes
import com.pockyt.pay.base.BaseResp

data class CardResp(
    override val respCode: String,
    override val respMsg: String? = null,
    val cardNonce: CardNonce? = null,
    val deviceData: String? = null
) : BaseResp(respCode, respMsg) {
    val isSuccessful: Boolean
        get() = respCode == PockytCodes.SUCCESS
}
