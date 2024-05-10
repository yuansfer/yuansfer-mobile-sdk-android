package com.pockyt.pay.req

import android.app.Activity
import com.braintreepayments.api.PayPalCheckoutRequest
import com.braintreepayments.api.PayPalVaultRequest
import com.pockyt.pay.base.BaseReq

sealed class PPWrapRequest {
    data class Checkout(val checkoutRequest: PayPalCheckoutRequest) : PPWrapRequest()
    data class Vault(val vaultRequest: PayPalVaultRequest) : PPWrapRequest()
}

data class PayPalReq(
    val activity: Activity,
    val clientToken: String,
    val request: PPWrapRequest,
    val autoDeviceData: Boolean = false
) : BaseReq()


