package com.pockyt.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pockyt.demo.page.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onMyClick(view: View) {
       when(view.id) {
           R.id.btn_alipay -> startActivity(Intent(this, AlipayActivity::class.java))
           R.id.btn_wechatpay -> startActivity(Intent(this, WechatPayActivity::class.java))
           R.id.btn_drop_in -> startActivity(Intent(this, DropInActivity::class.java))
           R.id.btn_card_pay -> startActivity(Intent(this, CardPayActivity::class.java))
           R.id.btn_paypal -> startActivity(Intent(this, PayPalActivity::class.java))
           R.id.btn_venmo -> startActivity(Intent(this, VenmoActivity::class.java))
           R.id.btn_google -> startActivity(Intent(this, GooglePayActivity::class.java))
           R.id.btn_cashapp -> startActivity(Intent(this, CashAppActivity::class.java))
       }
    }
}