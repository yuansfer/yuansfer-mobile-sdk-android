package com.pockyt.demo.page

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.*
import com.pockyt.demo.R
import com.pockyt.demo.api.HttpUtils
import com.pockyt.demo.util.ViewLog
import com.pockyt.pay.PockytPay
import com.pockyt.pay.req.CardReq

class CardPayActivity: AppCompatActivity() {

    private lateinit var etCardNum: EditText
    private lateinit var etExpDate: EditText
    private lateinit var etCVV: EditText
    private lateinit var vLog: ViewLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_page)
        vLog = ViewLog(findViewById(R.id.tv_result))
        etCardNum = findViewById(R.id.et_number_no)
        etExpDate = findViewById(R.id.et_expire_date)
        etCVV = findViewById(R.id.et_cvv)
    }

    fun onMyClick(view: View) {
        when (view.id) {
            R.id.btn_card_pay -> { sendPay() }
            R.id.btn_3d_pay -> { send3DPay() }
        }
    }

    /**
     * Call the server's secure-pay api to get the authorization,
     * and then use the authorization to pay.
     * Here use hardcode for the authorization, please read DropInActivity.kt for details.
     */
    private fun sendPay() {
        val card = Card()
        card.number = etCardNum.text.toString()
        card.expirationDate = etExpDate.text.toString()
        card.cvv = etCVV.text.toString()
        val request = CardReq(this, HttpUtils.CLIENT_TOKEN, card, true)
        PockytPay.cardPay.requestPay(request) {
            vLog.log("Obtained nonce:${it.isSuccessful}, desc:${it.respMsg}, nonce:${it.cardNonce?.string}, deviceData:${it.deviceData}")
            if (it.isSuccessful) {
                submitNonceToServer("Your transactionNo", it.cardNonce!!.string, it.deviceData)
            }
        }
    }

    /**
     * Call the server's secure-pay api to get the authorization,
     * and then use the authorization to pay.
     * Here use hardcode for the authorization, please read DropInActivity.kt for details.
     */
    private fun send3DPay() {

        val address = ThreeDSecurePostalAddress()
        address.givenName = "Jill" // ASCII-printable characters required, else will throw a validation error
        address.surname = "Doe" // ASCII-printable characters required, else will throw a validation error
        address.phoneNumber = "5551234567"
        address.streetAddress = "555 Smith St"
        address.extendedAddress = "#2"
        address.locality = "Chicago"
        address.region = "IL" // ISO-3166-2 code
        address.postalCode = "12345"
        address.countryCodeAlpha2 = "US"

        // For best results, provide as many additional elements as possible.
        val additionalInformation = ThreeDSecureAdditionalInformation()
        additionalInformation.shippingAddress = address

        val threeDSecureRequest = ThreeDSecureRequest()
        threeDSecureRequest.amount = "10"
        threeDSecureRequest.email = "test@email.com"
        threeDSecureRequest.billingAddress = address
        threeDSecureRequest.versionRequested = ThreeDSecureRequest.VERSION_2
        threeDSecureRequest.additionalInformation = additionalInformation

        val card = Card()
        card.number = etCardNum.text.toString()
        card.expirationDate = etExpDate.text.toString()
        card.cvv = etCVV.text.toString()
        val request = CardReq(this, HttpUtils.CLIENT_TOKEN, card, true, threeDSecureRequest)
        PockytPay.cardPay.requestPay(request) {
            vLog.log("Obtained nonce:${it.isSuccessful}, desc:${it.respMsg}, 3d secure nonce:${it.cardNonce?.string}, deviceData:${it.deviceData}")
            if (it.isSuccessful) {
                submitNonceToServer("Your transactionNo", it.cardNonce!!.string, it.deviceData)
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