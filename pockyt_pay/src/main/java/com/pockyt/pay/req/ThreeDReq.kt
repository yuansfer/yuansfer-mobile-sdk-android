package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.ThreeDSecureRequest
import com.pockyt.pay.base.BaseReq

data class ThreeDReq(
    val activity: Activity,
    val clientToken: String,
    val threeDSecureRequest: ThreeDSecureRequest,
    val autoDeviceData: Boolean = false,
): BaseReq()

