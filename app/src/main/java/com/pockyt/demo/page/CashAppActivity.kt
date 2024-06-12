package com.pockyt.demo.page

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.Pockyt
import com.pockyt.pay.cashapp.CashAppRequestData
import com.pockyt.pay.req.CashAppReq
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CashAppActivity: AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etMerchantNo: EditText
    private lateinit var etStoreNo: EditText
    private lateinit var vLog: ViewLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashapp_page)
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
            R.id.btn_cashapp -> {
                sendCheckout()
            }
        }
    }

    /**
     * Call the server's prepay api to create a transaction.
     */
    private fun sendCheckout() {
        val reqUrl = "${HttpUtils.BASE_URL}/micropay/v3/prepay"
        val apiToken = HttpUtils.API_TOKEN
        val reqMap = hashMapOf(
            "merchantNo" to etMerchantNo.text.toString(),
            "storeNo" to etStoreNo.text.toString(),
            "token" to apiToken,
            "amount" to etAmount.text.toString(),
            "vendor" to "cashapppay",
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
                    vLog.log("Failed to get prepay: $response")
                    return@doPost
                }
                val resultObject = jsonObject.optJSONObject("result")
                val clientId = resultObject.getString("clientId")
                val scopeId: String = resultObject.optString("scopeId")
                val transactionNo: String = resultObject.optString("transactionNo")
                val merchantNo: String = resultObject.optString("merchantNo")
                if ("cit" == resultObject.optString("creditType")) {
                    requestCashAppPay(clientId, CashAppRequestData.OnFileAction(scopeId, merchantNo), transactionNo)
                } else {
                    val amount = resultObject.optDouble("amount")
                    requestCashAppPay(clientId, CashAppRequestData.OneTimeAction(amount, scopeId), transactionNo)
                }
            } catch (e: JSONException) {
                vLog.log("Failed to get prepay: ${e.message}")
                return@doPost
            }
        }
    }

    private fun requestCashAppPay(clientId: String, requestData: CashAppRequestData, transactionNo: String) {
        val request = CashAppReq(clientId, requestData, sandboxEnv = true)
        Pockyt.createCashApp().requestPay(request) {
            if (it.isSuccessful) {
                // Payment Approved
                vLog.log("Payment approved")
                queryTransactionResult(transactionNo)
            } else {
                vLog.log("Payment failed: ${it.respMsg}")
            }
        }
    }

    /**
     * Query the transaction result by transactionNo.
     * transactionNo is returned by the prepay api.
     */
    private fun queryTransactionResult(transactionNo: String) {
        // ...
    }
}