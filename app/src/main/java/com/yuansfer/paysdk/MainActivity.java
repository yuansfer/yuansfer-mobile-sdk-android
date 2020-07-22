package com.yuansfer.paysdk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.api.ApiUrl;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;
import com.yuansfer.paysdk.okhttp.IResponseHandler;
import com.yuansfer.paysdk.okhttp.RawResponseHandler;
import com.yuansfer.sdk.YSAppPay;
import com.yuansfer.sdk.model.DetailInfo;
import com.yuansfer.sdk.model.PrepayInfo;
import com.yuansfer.sdk.model.RefundInfo;
import com.yuansfer.sdk.pay.PayItem;
import com.yuansfer.sdk.pay.PayResultMgr;
import com.yuansfer.sdk.pay.PayType;
import com.yuansfer.sdk.pay.alipay.AlipayItem;
import com.yuansfer.sdk.pay.wxpay.WxPayItem;
import com.yuansfer.sdk.util.ResStringGet;


public class MainActivity extends AppCompatActivity implements PayResultMgr.IPayResultCallback {

    //测试token
    private static String TEST_TOKEN;
    private static final String TEST_MERCHANT_NO = "200043";
    private static final String TEST_STORE_NO = "300014";
    private TextView tvApiResult, tvApiTitle;
    private EditText etAlipayRef, etWechatRef, etStatusRef, etRefundRef;
    private RadioGroup rgEnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rgEnv = findViewById(R.id.rg_env);
        tvApiTitle = findViewById(R.id.tv_result_title);
        tvApiResult = findViewById(R.id.tv_result_value);
        etAlipayRef = findViewById(R.id.edt_ali);
        etWechatRef = findViewById(R.id.edt_wx);
        etStatusRef = findViewById(R.id.edt_order_status);
        etRefundRef = findViewById(R.id.edt_order_refund);
        YSAppPay.initialize(getApplicationContext());
        YSAppPay.registerPayResultCallback(this);
        YSAppPay.setDebugMode();
        setEnvListener();
    }

    private void setEnvListener() {
        rgEnv.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_release) {
                Log.e("MainActivity", "当前为生产环境");
                TEST_TOKEN = "8fca880f7fe434597625535c7834769d";
                //设置生产服务器
                ApiUrl.setEnvMode(true);
                //启用支付宝沙箱模式,注意要已集成了支付宝sdk
                EnvUtils.setEnv(EnvUtils.EnvEnum.ONLINE);
            } else {
                Log.e("MainActivity", "当前为测试环境");
                TEST_TOKEN = "5cbfb079f15b150122261c8537086d77a";
                //设置测试服务器
                ApiUrl.setEnvMode(false);
                //启用支付宝沙箱模式,注意要已集成了支付宝sdk
                EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
            }
        });
        ((RadioButton)rgEnv.findViewById(R.id.rb_test)).setChecked(true);
    }

    public void onViewClick(View view) {
        hideKeyboard();
        switch (view.getId()) {
            case R.id.btn_ali:
                //alipay
                callAlipay();
                break;
            case R.id.btn_wx:
                //wechatpay
                callWechatPay();
                break;
            case R.id.btn_order_status:
                //query order status
                callOrderStatus();
                break;
            case R.id.btn_refund:
                //order refund
                callRefund();
                break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void callOrderStatus() {
        DetailInfo info = new DetailInfo();
        info.setMerchantNo(TEST_MERCHANT_NO);
        info.setStoreNo(TEST_STORE_NO);
        info.setReference(etStatusRef.getText().toString());
        queryOrder(TEST_TOKEN, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                tvApiTitle.setText("queryOrderStatus result");
                tvApiResult.setText(result);
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiTitle.setText("queryOrderStatus result");
                tvApiResult.setText(errorMsg);
            }
        });
    }

    private void callRefund() {
        RefundInfo info = new RefundInfo();
        info.setMerchantNo(TEST_MERCHANT_NO);
        info.setStoreNo(TEST_STORE_NO);
        info.setAmount(0.01);
        info.setReference(etRefundRef.getText().toString());
        refund(TEST_TOKEN, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                tvApiTitle.setText("refund result");
                tvApiResult.setText(result);
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiTitle.setText("refund result");
                tvApiResult.setText(errorMsg);
            }
        });
    }

    private void callAlipay() {
        PrepayInfo info = new PrepayInfo();
        info.setMerchantNo(TEST_MERCHANT_NO);
        info.setStoreNo(TEST_STORE_NO);
        info.setPayType(PayType.ALIPAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://wx.yuansfer.yunkeguan.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(etAlipayRef.getText().toString());
        prepay(TEST_TOKEN, info, new GsonResponseHandler<AlipayResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiTitle.setText("alipay result");
                tvApiResult.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, AlipayResultInfo response) {
                tvApiTitle.setText("alipay result");
                if ("000100".equals(response.getRet_code())) {
                    tvApiResult.setText(response.getResult().getPayInfo());
                    YSAppPay.getInstance().startPay(MainActivity.this
                            , new AlipayItem(response.getResult().getPayInfo()));
                } else {
                    tvApiResult.setText(response.getRet_msg());
                }
            }
        });
    }

    private void callWechatPay() {
        PrepayInfo info = new PrepayInfo();
        info.setMerchantNo(TEST_MERCHANT_NO);
        info.setStoreNo(TEST_STORE_NO);
        info.setPayType(PayType.WXPAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://wx.yuansfer.yunkeguan.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(etWechatRef.getText().toString());
        prepay(TEST_TOKEN, info, new GsonResponseHandler<WechatResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiTitle.setText("wechat pay result");
                tvApiResult.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, WechatResultInfo response) {
                tvApiTitle.setText("wechat pay result");
                if ("000100".equals(response.getRet_code())) {
                    tvApiResult.setText(response.getResult().toString());
                    YSAppPay.getInstance().supportWxPay(response.getResult().getAppid());
                    launchWechat(response.getResult());
                } else {
                    tvApiResult.setText(response.getRet_msg());
                }
            }
        });
    }

    /**
     * order prepay
     *
     * @param token
     * @param prepayInfo
     * @param responseCallback
     */
    private void prepay(@NonNull String token
            , @NonNull PrepayInfo prepayInfo, @NonNull IResponseHandler responseCallback) {
        ApiService.prepay(this.getApplicationContext(), token, prepayInfo, responseCallback);
    }

    /**
     * excute order refund
     *
     * @param token
     * @param refundInfo
     * @param responseCallback
     */
    private void refund(@NonNull String token
            , @NonNull RefundInfo refundInfo, @NonNull IResponseHandler responseCallback) {
        if (!TextUtils.isEmpty(refundInfo.getReference()) && !TextUtils.isEmpty(refundInfo.getTransactionNo())) {
            responseCallback.onFailure(ApiService.ApiStatusCode.API_PARAM_ERROR, ResStringGet.getString(com.yuansfer.sdk.R.string.refund_params_error));
            return;
        }
        if (refundInfo.getAmount() > 0.00D && refundInfo.getRmbAmount() > 0.00D) {
            responseCallback.onFailure(ApiService.ApiStatusCode.API_PARAM_ERROR, ResStringGet.getString(com.yuansfer.sdk.R.string.refund_amount_error));
            return;
        }
        ApiService.refund(this.getApplicationContext(), token, refundInfo, responseCallback);
    }

    /**
     * query order detail
     *
     * @param token
     * @param orderInfo
     * @param responseCallback
     */
    private void queryOrder(@NonNull String token
            , @NonNull DetailInfo orderInfo, @NonNull IResponseHandler responseCallback) {
        if (!TextUtils.isEmpty(orderInfo.getReference()) && !TextUtils.isEmpty(orderInfo.getTransactionNo())) {
            responseCallback.onFailure(ApiService.ApiStatusCode.API_PARAM_ERROR, ResStringGet.getString(com.yuansfer.sdk.R.string.target_param_no_error));
            return;
        }
        ApiService.orderStatus(this.getApplicationContext(), token, orderInfo, responseCallback);
    }

    private void launchWechat(WechatInfo wechatInfo) {
        PayItem payItem = new WxPayItem.Builder()
                .setAppId(wechatInfo.getAppid())
                .setPackageValue(wechatInfo.getPackageName())
                .setPrepayId(wechatInfo.getPrepayid())
                .setPartnerId(wechatInfo.getPartnerid())
                .setNonceStr(wechatInfo.getNoncestr())
                .setSign(wechatInfo.getSign())
                .setTimestamp(wechatInfo.getTimestamp()).build();
        YSAppPay.getInstance().startPay(MainActivity.this, payItem);
    }

    @Override
    public void onPaySuccess(int payType) {
        Toast.makeText(this, "pay success, type=" + payType, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPayFail(int payType, String msg) {
        Toast.makeText(this, "pay fail=" + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPayCancel(int payType) {
        Toast.makeText(this, "pay cancelled，type=" + payType, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.unregisterPayResultCallback(this);
    }

}
