package com.pockyt.demo.page

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.*
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.Pockyt
import com.pockyt.pay.req.VenmoReq

class VenmoActivity: AppCompatActivity() {

    private lateinit var vLog: ViewLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venmo_page)
        vLog = ViewLog(findViewById(R.id.tv_result))
    }

    fun onMyClick(view: View) {
        when (view.id) {
            R.id.btn_venmo -> {
                sendPay()
            }
        }
    }

    /**
     * Call the server's secure-pay api to get the authorization,
     * and then use the authorization to pay.
     * Here use hardcode for the authorization, please read DropInActivity.kt for details.
     */
    private fun sendPay() {
        val request = VenmoReq(this, HttpUtils.CLIENT_TOKEN, VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE), true)
        Pockyt.createVenmo().requestPay(request) {
            vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.venmoNonce?.string}, deviceData:${it.deviceData}")
            if (it.isSuccessful) {
                submitNonceToServer("Your transactionNo", it.venmoNonce!!.string, it.deviceData)
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