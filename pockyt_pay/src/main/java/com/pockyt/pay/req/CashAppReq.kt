package com.pockyt.pay.req

import com.pockyt.pay.base.BaseReq
import com.pockyt.pay.cashapp.CashAppRequest

const val REDIRECT_URL = "cashapppay://checkout"
data class CashAppReq(
    val clientId: String,
    val request: CashAppRequest,
    val redirectUrl: String = REDIRECT_URL,
    val sandboxEnv: Boolean = false
): BaseReq()
