package com.yuansfer.paysdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.api.ApiUrl;
import com.yuansfer.paysdk.model.AlipayResultInfo;
import com.yuansfer.paysdk.model.SecurePayInfo;
import com.yuansfer.paysdk.model.SecureResultInfo;
import com.yuansfer.paysdk.model.WechatInfo;
import com.yuansfer.paysdk.model.WechatResultInfo;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;
import com.yuansfer.paysdk.okhttp.IResponseHandler;
import com.yuansfer.paysdk.okhttp.RawResponseHandler;
import com.yuansfer.sdk.YSAppPay;
import com.yuansfer.paysdk.model.DetailInfo;
import com.yuansfer.paysdk.model.PrepayInfo;
import com.yuansfer.paysdk.model.RefundInfo;
import com.yuansfer.sdk.pay.PayItem;
import com.yuansfer.sdk.pay.PayResultMgr;
import com.yuansfer.sdk.pay.PayType;
import com.yuansfer.sdk.pay.alipay.AlipayItem;
import com.yuansfer.sdk.pay.wxpay.WxPayItem;
import com.yuansfer.sdk.util.ResStringGet;


public class MainActivity extends AppCompatActivity implements PayResultMgr.IPayResultCallback {

    String mToken;
    String mMerchantNo = "200043";
    String mStoreNo = "300014";
    TextView tvApiResult, tvApiTitle;
    EditText etAlipayRef, etWechatRef, etStatusRef, etRefundRef, etOnlineRef;
    RadioGroup rgEnv;
    Spinner spCurrency;

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
        etOnlineRef = findViewById(R.id.edt_secure_pay);
        spCurrency = findViewById(R.id.sp_multi_currency);
        setDefaultEditRef(etAlipayRef);
        setDefaultEditRef(etRefundRef);
        setDefaultEditRef(etStatusRef);
        setDefaultEditRef(etWechatRef);
        setDefaultEditRef(etOnlineRef);
        YSAppPay.initialize(getApplicationContext());
        YSAppPay.registerPayResultCallback(this);
        YSAppPay.setDebugMode();
        setEnvListener();
    }

    private void setEnvListener() {
        rgEnv.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_release) {
                //生产配置
                mToken = "8fca880f7fe434597625535c7834769d";
                ApiUrl.setEnvMode(true);
                YSAppPay.setAliEnv(true);
            } else {
                //测试配置
                mToken = "5cbfb079f15b150122261c8537086d77a";
                ApiUrl.setEnvMode(false);
                YSAppPay.setAliEnv(false);
            }
        });
        ((RadioButton) rgEnv.findViewById(R.id.rb_test)).setChecked(true);
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
            case R.id.btn_secure_pay:
                callSecurePay();
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
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setReference(getEditRef(etStatusRef));
        queryOrder(mToken, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                setDefaultEditRef(etStatusRef);
                tvApiResult.setText(result);
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiResult.setText(errorMsg);
            }
        });
    }

    private void callRefund() {
        RefundInfo info = new RefundInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setAmount(0.01);
        info.setReference(getEditRef(etRefundRef));
        refund(mToken, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                setDefaultEditRef(etRefundRef);
                tvApiResult.setText(result);
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiResult.setText(errorMsg);
            }
        });
    }


    private void callSecurePay() {
        SecurePayInfo info = new SecurePayInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setAmount(13);
        info.setCurrency(spCurrency.getSelectedItem().toString());
        info.setVendor("alipay");
        info.setTimeout(30);
        info.setReference(getEditRef(etOnlineRef));
        info.setIpnUrl("http://alipay.yunkeguan.com/ttest/test");
        info.setCallbackUrl("http://alipay.yunkeguan.com/ttest/test2");
        info.setDescription("test+description");
        info.setNote("note");
        ApiService.securePay(this.getApplicationContext(), mToken, info, new GsonResponseHandler<SecureResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiResult.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, SecureResultInfo response) {
                setDefaultEditRef(etOnlineRef);
                if ("000100".equals(response.getRet_code())) {
                    tvApiResult.setText("跳转WAP:" + response.getResult().getCashierUrl());
                    launchCashierUrl(response.getResult().getCashierUrl());
                } else {
                    tvApiResult.setText(response.getRet_msg());
                }
            }

            private void launchCashierUrl(String paymentRedirectUrl) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentRedirectUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callAlipay() {
        PrepayInfo info = new PrepayInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setPayType(PayType.ALIPAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://alipay.yuansfer.yunkeguan.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(getEditRef(etAlipayRef));
        prepay(mToken, info, new GsonResponseHandler<AlipayResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiResult.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, AlipayResultInfo response) {
                setDefaultEditRef(etAlipayRef);
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
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setPayType(PayType.WXPAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://wx.yuansfer.yunkeguan.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(getEditRef(etWechatRef));
        prepay(mToken, info, new GsonResponseHandler<WechatResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                tvApiResult.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, WechatResultInfo response) {
                setDefaultEditRef(etWechatRef);
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

    private String getEditRef(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString())
                ? editText.getHint().toString() : editText.getText().toString();
    }

    private void setDefaultEditRef(EditText editRef) {
        editRef.setHint(System.currentTimeMillis() + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.unregisterPayResultCallback(this);
    }

}
