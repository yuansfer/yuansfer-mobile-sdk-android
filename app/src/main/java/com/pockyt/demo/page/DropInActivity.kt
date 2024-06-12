package com.pockyt.demo.page

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.*
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.WalletConstants
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.Pockyt
import com.pockyt.pay.req.DropInReq
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

class DropInActivity: AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etMerchantNo: EditText
    private lateinit var etStoreNo: EditText
    private lateinit var vLog: ViewLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_page)
        vLog = ViewLog(findViewById(R.id.tv_result))
        etMerchantNo = findViewById(R.id.et_merchant_no)
        etMerchantNo.setText(HttpUtils.MERCHANT_NO)
        etStoreNo = findViewById(R.id.et_store_no)
        etStoreNo.setText(HttpUtils.STORE_NO)
        etAmount = findViewById(R.id.et_amount)
        etAmount.setText("0.01")
    }

    fun onMyClick(view: View) {
        when (view.id) {
            R.id.btn_send_pay -> {
                sendPay()
            }
        }
    }

    /**
     * Call the server's secure-pay api to get the authorization,
     * and then use the authorization to pay.
     */
    private fun sendPay() {
        val reqUrl = "${HttpUtils.BASE_URL}/online/v3/secure-pay"
        val apiToken = HttpUtils.API_TOKEN
        val reqMap = hashMapOf(
            "merchantNo" to etMerchantNo.text.toString(),
            "storeNo" to etStoreNo.text.toString(),
            "token" to apiToken,
            "amount" to etAmount.text.toString(),
            "osType" to "ANDROID",
            "vendor" to "creditcard",
            "ipnUrl" to "https://merchant.com/ipn",
            "reference" to UUID.randomUUID().toString(),
            "note" to "note",
            "description" to "description",
            "settleCurrency" to "USD",
            "currency" to "USD",
            "terminal" to "APP"
        )
        HttpUtils.doPost(reqUrl, reqMap) { response ->
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject.optString("ret_code") != "000100") {
                    vLog.log("Failed to get secure-pay: $response")
                    return@doPost
                }
                jsonObject.optJSONObject("result").optString("authorization").let { clientToken  ->
                    // Please note that the authorization field returned by the server's api should be used here. Here, only a hardcoded token is demonstrated.
                    val authorization = clientToken.ifBlank { HttpUtils.CLIENT_TOKEN }

                    val dropInRequest = DropInRequest()
                    // PayPalVaultRequest is used to enable PayPal payment
                    val payPalRequest = PayPalVaultRequest()
                    dropInRequest.payPalRequest = payPalRequest

                    // GooglePayRequest is used to enable Google Pay payment
                    val googlePayRequest = GooglePayRequest()
                    googlePayRequest.transactionInfo = TransactionInfo.newBuilder()
                        .setTotalPrice("1.00")
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .setCurrencyCode("USD")
                        .build()
                    googlePayRequest.isBillingAddressRequired = true
                    dropInRequest.googlePayRequest = googlePayRequest

                    val venmoRequest = VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE)
                    dropInRequest.venmoRequest = venmoRequest

                    val threeDSRequest = ThreeDSecureRequest()
                    threeDSRequest.amount = etAmount.text.toString()
                    threeDSRequest.email = "email@gmail.com"
                    threeDSRequest.versionRequested = ThreeDSecureRequest.VERSION_2
                    dropInRequest.threeDSecureRequest = threeDSRequest

                    Pockyt.createDropIn().requestPay(DropInReq(DropInActivity@this, authorization, dropInRequest)) {
                        vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, vendor:${it.dropInResult?.paymentMethodType}, nonce:${it.dropInResult?.paymentMethodNonce?.string}, deviceData:${it.dropInResult?.deviceData}")
                        if (it.isSuccessful) {
                            submitNonceToServer(jsonObject.optJSONObject("result").optString("transactionNo")
                                , it.dropInResult?.paymentMethodNonce?.string ?: ""
                                , it.dropInResult?.deviceData)
                        }
                    }
                }
            } catch (e: JSONException) {
                vLog.log("Failed to get secure-pay: ${e.message}")
                return@doPost
            }
        }
    }

    /**
     * Send nonce to your server
     * @param transactionNo transaction number
     * @param paymentMethodNonce payment method nonce
     * @param deviceData device data
     */
    private fun submitNonceToServer(transactionNo: String, paymentMethodNonce: String, deviceData: String?) {
        val reqUrl = "${HttpUtils.BASE_URL}/creditpay/v3/process"
        val apiToken = HttpUtils.API_TOKEN
        // paymentMethod: paypal_account、venmo_account、credit_card、android_pay_card
        val paymentMethod = "paypal_account"
        val reqMap = hashMapOf(
            "merchantNo" to etMerchantNo.text.toString(),
            "storeNo" to etStoreNo.text.toString(),
            "token" to apiToken,
            "paymentMethod" to paymentMethod,
            "paymentMethodNonce" to paymentMethodNonce,
            "deviceData" to (deviceData ?: ""),
            "transactionNo" to transactionNo
        )
        HttpUtils.doPost(reqUrl, reqMap) { response ->
            if (response.contains("000100")) {
                vLog.log("Payment success")
            } else {
                vLog.log("Payment failed: $response")
            }
        }
    }
}