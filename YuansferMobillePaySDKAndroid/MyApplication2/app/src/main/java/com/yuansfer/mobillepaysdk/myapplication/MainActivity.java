package com.yuansfer.mobillepaysdk.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yuansfer.mobillepaysdk.android.YuansferPay;
import com.yuansfer.mobillepaysdk.librarydemo.LibraryTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YuansferPay yuansferPay = new YuansferPay(MainActivity.this, "200043","300404","d0452f48854c29a7c07cfafe8128fd22");
        Button pay = (Button) findViewById(R.id.id_pay);
        Button refund = (Button) findViewById(R.id.id_refund);
        Button orderState = (Button) findViewById(R.id.id_order_state);
        Button exchangeRate = (Button) findViewById(R.id.id_exchangeRate);
        pay.setOnClickListener(new ClickListener(yuansferPay));
        refund.setOnClickListener(new ClickListener(yuansferPay));
        orderState.setOnClickListener(new ClickListener(yuansferPay));
        exchangeRate.setOnClickListener(new ClickListener(yuansferPay));
    }


    public class ClickListener implements View.OnClickListener {

        private YuansferPay yuansferPay;
        public ClickListener(YuansferPay yuansferPay) {
            this.yuansferPay = yuansferPay;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.id_pay:
                    Toast.makeText(MainActivity.this, "支付", Toast.LENGTH_SHORT).show();
                    doPay(yuansferPay);
                    break;
                case R.id.id_refund:
                    Toast.makeText(MainActivity.this, "退款", Toast.LENGTH_SHORT).show();
                    refund(yuansferPay);
                    break;
                case R.id.id_order_state:
                    Toast.makeText(MainActivity.this, "订单状态", Toast.LENGTH_SHORT).show();
                    orderState(yuansferPay);
                    break;
                case R.id.id_exchangeRate:
                    Toast.makeText(MainActivity.this, "汇率", Toast.LENGTH_SHORT).show();
                    exchangeRate(yuansferPay);
                    break;
            }
        }
    };

    /**
     * 支付
     */
    public void doPay(YuansferPay yuansferPay) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("rmbAmount", "0.1");
        params.put("ipnUrl", "https://wx.yuansfer.yunkeguan.com/wx");
        params.put("timeout", "120");
        params.put("goodsInfo", "[{\"goods_name\":\"小炒牛肉\",\"quantity\":\"1\"}]");
        params.put("reference", getOutTradeNo());

        String payResult = yuansferPay.securepay(params);
    }

    /**
     * 退款接口
     */
    public void refund(YuansferPay yuansferPay) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("rmbAmount", "0.1");
        params.put("reference", "0324183950-1641");

        String refundResult = yuansferPay.securepayRefund(params);
    }

    /**
     * 查询订单状态
     */
    public void orderState(YuansferPay yuansferPay) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("reference", "0324183950-1641");

        String orderState = yuansferPay.orderState(params);
    }

    /**
     * 查询汇率
     *
     */
    public void exchangeRate(YuansferPay yuansferPay) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("date", "20190324");

        String queryResult = yuansferPay.exchangeRate(params);
    }


    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        switch (key = key + r.nextInt()) {
        }


        key = key.substring(0, 15);
        return key;
    }
}
