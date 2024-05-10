package com.pockyt.pay.req

import com.pockyt.pay.base.BaseReq

data class WechatPayReq(
    val appId: String,
    val partnerId: String,
    val prepayId: String,
    val packageValue: String,
    val nonceStr: String,
    val timeStamp: String,
    val sign: String): BaseReq()