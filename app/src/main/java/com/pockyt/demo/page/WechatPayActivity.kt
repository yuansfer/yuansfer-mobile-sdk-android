package com.pockyt.demo.page

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.Pockyt
import com.pockyt.pay.req.WechatPayReq
import com.pockyt.pay.wechatpay.WechatPayStrategy
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

class WechatPayActivity: AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etMerchantNo: EditText
    private lateinit var etStoreNo: EditText
    private lateinit var btSendPay: Button
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
        btSendPay = findViewById(R.id.btn_send_pay)

        WechatPayStrategy.registerApi(this, HttpUtils.APP_ID)
        btSendPay.isEnabled = WechatPayStrategy.isInstalled()
                && WechatPayStrategy.isSupportPayApi()
    }

    fun onMyClick(view: View) {
        when (view.id) {
            R.id.btn_send_pay -> {
                val reqUrl = "${HttpUtils.BASE_URL}/micropay/v3/prepay"
                val apiToken = HttpUtils.API_TOKEN
                val reqMap = hashMapOf(
                    "merchantNo" to etMerchantNo.text.toString(),
                    "storeNo" to etStoreNo.text.toString(),
                    "token" to apiToken,
                    "amount" to etAmount.text.toString(),
                    "vendor" to "wechatpay",
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
                        jsonObject.optJSONObject("result")?.let { result ->
                            Pockyt.createWechatPay().requestPay(
                                WechatPayReq(
                                appId = result.optString("appid"),
                                partnerId = result.optString("partnerid"),
                                prepayId = result.optString("prepayid"),
                                packageValue = result.optString("package"),
                                nonceStr = result.optString("noncestr"),
                                timeStamp = result.optString("timestamp"),
                                sign = result.optString("sign")
                            )) {
                                vLog.log("Paid:${it.isSuccessful}, cancelled:${it.isCancelled}, $it")
                            }
                        }
                    } catch (e: JSONException) {
                        vLog.log("Failed to get prepay: ${e.message}")
                        return@doPost
                    }
                }
            }
        }
    }
}