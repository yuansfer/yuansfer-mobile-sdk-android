package com.pockyt.demo.page

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.*
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.WalletConstants
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.Pockyt
import com.pockyt.pay.req.*

class GooglePayActivity: AppCompatActivity() {

    private lateinit var vLog: ViewLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googlepay_page)
        vLog = ViewLog(findViewById(R.id.tv_result))
    }

    fun onMyClick(view: View) {
        when (view.id) {
            R.id.btn_google -> {
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
        val googlePayRequest = GooglePayRequest()
        googlePayRequest.transactionInfo = TransactionInfo.newBuilder()
            .setTotalPrice("0.01")
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setCurrencyCode("USD")
            .build()
        googlePayRequest.googleMerchantId = "merchant-id-from-google";
        googlePayRequest.isBillingAddressRequired = true
        val request = GooglePayReq(this, HttpUtils.CLIENT_TOKEN, googlePayRequest)
        Pockyt.createGooglePay().requestPay(request) {
            vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.paymentNonce?.string}")
            if (it.isSuccessful) {
                submitNonceToServer("Your transactionNo", it.paymentNonce!!.string)
            }
        }
    }

    /**
     * Send nonce to your server, please read DropInActivity.kt for details.
     * @param transactionNo transaction number
     * @param paymentMethodNonce payment method nonce
     */
    private fun submitNonceToServer(transactionNo: String, paymentMethodNonce: String) {
        // Manually get the device data
//        val braintreeClient = BraintreeClient(this, HttpUtils.CLIENT_TOKEN)
//        DataCollector(braintreeClient).collectDeviceData(this) { deviceData, error ->
//            if (deviceData != null) {
//                vLog.log("DeviceData:${deviceData}")
//                // Send the nonce and deviceData to your server
//                // ...
//            } else {
//                vLog.log("DeviceData error:${error?.message}")
//            }
//        }
    }
}