package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.PayPalRequest
import com.pockyt.pay.base.BaseReq

data class PayPalReq(
    val activity: Activity,
    val clientToken: String,
    val request: PayPalRequest,
    val autoDeviceData: Boolean = false
) : BaseReq()


