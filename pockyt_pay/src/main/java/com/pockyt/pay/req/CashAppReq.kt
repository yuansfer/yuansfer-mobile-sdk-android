package com.pockyt.pay.req

import com.pockyt.pay.base.BaseReq
import com.pockyt.pay.cashapp.CashAppRequestData

const val REDIRECT_URL = "cashapppay://checkout"
data class CashAppReq(
    val clientId: String,
    val requestData: CashAppRequestData,
    val redirectUrl: String = REDIRECT_URL,
    val sandboxEnv: Boolean = false
): BaseReq()
