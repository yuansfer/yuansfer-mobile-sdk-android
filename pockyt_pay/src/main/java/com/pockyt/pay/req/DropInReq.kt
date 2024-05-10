package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.DropInRequest
import com.pockyt.pay.base.BaseReq

data class DropInReq(
    val activity: Activity,
    val clientToken: String,
    val request: DropInRequest
): BaseReq()
