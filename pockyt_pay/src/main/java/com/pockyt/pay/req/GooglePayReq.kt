package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.GooglePayRequest
import com.pockyt.pay.base.BaseReq

data class GooglePayReq(
    val activity: Activity,
    val clientToken: String,
    val request: GooglePayRequest,
    val autoDeviceData: Boolean = false
): BaseReq()
