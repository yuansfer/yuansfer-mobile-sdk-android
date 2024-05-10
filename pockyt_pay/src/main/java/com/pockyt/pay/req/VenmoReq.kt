package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.VenmoRequest
import com.pockyt.pay.base.BaseReq

data class VenmoReq(
    val activity: Activity,
    val clientToken: String,
    val request: VenmoRequest,
    val autoDeviceData: Boolean = false
): BaseReq()
