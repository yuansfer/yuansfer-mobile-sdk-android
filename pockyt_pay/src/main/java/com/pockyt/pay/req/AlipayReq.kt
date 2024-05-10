package com.pockyt.pay.req

import android.app.Activity
import com.pockyt.pay.base.BaseReq

data class AlipayReq(
    val activity: Activity,
    val payInfo: String): BaseReq()