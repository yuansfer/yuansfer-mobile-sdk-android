package com.pockyt.pay.util

object PockytCodes {
    // Common codes
    const val SUCCESS = "0"
    const val ERROR = "-1"
    const val CANCEL = "-2"
    const val DUPLICATE = "-3"
    // Alipay codes
    const val ALIPAY_SUCCESS = "9000"
    const val ALIPAY_USER_CANCEL = "6001"
    const val ALIPAY_PENDING = "8000"
    const val ALIPAY_PAY_FAIL = "4000"
    const val ALIPAY_DUPLICATE_REQUEST = "5000"
    const val ALIPAY_NO_CONNECTION = "6002"
    // Wechat Pay codes
    const val WECHAT_SENT_FAIL = "-3"
    const val WECHAT_AUTH_DENY = "-4"
    const val WECHAT_UN_SUPPORT = "-5"
}
