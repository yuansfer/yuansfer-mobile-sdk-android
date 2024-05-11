package com.pockyt.demo.page

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.*
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.PockytPay
import com.pockyt.pay.req.PPWrapRequest
import com.pockyt.pay.req.PayPalReq

class PayPalActivity: AppCompatActivity() {

    private lateinit var vLog: ViewLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paypal_page)
        vLog = ViewLog(findViewById(R.id.tv_result))
    }

    fun onMyClick(view: View) {
        when (view.id) {
            R.id.btn_checkout -> {
                sendCheckout()
            }
            R.id.btn_vault -> {
                sendVault()
            }
        }
    }

    /**
     * Call the server's secure-pay api to get the authorization,
     * and then use the authorization to pay.
     * Here use hardcode for the authorization, please read DropInActivity.kt for details.
     */
    private fun sendCheckout() {
        val loadDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            show()
        }
        val checkoutRequest = PayPalCheckoutRequest("0.01")
        checkoutRequest.currencyCode = "USD"
        val pockytRequest = PayPalReq(this, HttpUtils.CLIENT_TOKEN, PPWrapRequest.Checkout(checkoutRequest), false)
        PockytPay.paypalPay.requestPay(pockytRequest) {
            loadDialog.dismiss()
            vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.paypalNonce?.string}, deviceData:${it.deviceData}")
            if (it.isSuccessful) {
                submitNonceToServer("Your transactionNo", it.paypalNonce!!.string, it.deviceData)
            }
        }
    }

    /**
     * Call the server's secure-pay api to get the authorization,
     * and then use the authorization to pay.
     * Here use hardcode for the authorization, please read DropInActivity.kt for details.
     */
    private fun sendVault() {
        val vaultRequest = PayPalVaultRequest()
        vaultRequest.billingAgreementDescription = "Your agreement description"
        val pockytRequest = PayPalReq(this, HttpUtils.CLIENT_TOKEN, PPWrapRequest.Vault(vaultRequest), true)
        PockytPay.paypalPay.requestPay(pockytRequest) {
            vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.paypalNonce?.string}, deviceData:${it.deviceData}")
            if (it.isSuccessful) {
                submitNonceToServer("Your transactionNo", it.paypalNonce!!.string, it.deviceData)
            }
        }
    }

    /**
     * Send nonce to your server, please read DropInActivity.kt for details.
     * @param transactionNo transaction number
     * @param paymentMethodNonce payment method nonce
     * @param deviceData device data
     */
    private fun submitNonceToServer(transactionNo: String, paymentMethodNonce: String, deviceData: String?) {
        // Send the nonce and deviceData to your server
        // ...
    }
}