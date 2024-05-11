package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.Card
import com.pockyt.pay.base.BaseReq

data class CardReq(
    val activity: Activity,
    val clientToken: String,
    val card: Card,
    val autoDeviceData: Boolean = false
): BaseReq()

