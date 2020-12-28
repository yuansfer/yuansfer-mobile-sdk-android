package com.yuansfer.paysdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.payment.YSAppPay;
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
import com.yuansfer.paysdk.model.DetailInfo;
import com.yuansfer.paysdk.model.PrepayInfo;
import com.yuansfer.paysdk.model.RefundInfo;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.alipay.AlipayItem;
import com.yuansfer.pay.googlepay.YSGooglePayActivity;
import com.yuansfer.pay.googlepay.YSGooglePayItem;
import com.yuansfer.pay.wxpay.WxPayItem;


public class MainActivity extends YSGooglePayActivity implements PayResultMgr.IPayResultCallback {

    private String mToken;
    private String mAuthorization;
    private String mMerchantNo = "200043";
    private String mStoreNo = "300014";
    private TextView mResultTxt;
    private EditText mAlipayEdt, mWechatPayEdt, mOrderEdt, mRefundEdt, mMultiEdt, mGooglePayEdt;
    private Button mGooglePayBtn;
    private RadioGroup mEvnRG;
    private Spinner mCurrencySpn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setEnvSwitch();
        YSAppPay.setLogEnable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        YSAppPay.registerPayResultCallback(this);
        YSAppPay.getInstance().bindGooglePay(this, mAuthorization);
    }

    @Override
    protected void onStop() {
        super.onStop();
        YSAppPay.unregisterPayResultCallback(this);
        YSAppPay.getInstance().unbindGooglePay(this);
    }

    private void initViews() {
        mEvnRG = findViewById(R.id.rg_env);
        mResultTxt = findViewById(R.id.tv_result);
        mAlipayEdt = findViewById(R.id.edt_ali);
        mWechatPayEdt = findViewById(R.id.edt_wx);
        mOrderEdt = findViewById(R.id.edt_order_status);
        mRefundEdt = findViewById(R.id.edt_order_refund);
        mMultiEdt = findViewById(R.id.edt_secure_pay);
        mCurrencySpn = findViewById(R.id.sp_multi_currency);
        mGooglePayEdt = findViewById(R.id.edt_google_pay_amount);
        mGooglePayBtn = findViewById(R.id.btn_google_pay);
        setDefaultEditRef(mAlipayEdt);
        setDefaultEditRef(mRefundEdt);
        setDefaultEditRef(mOrderEdt);
        setDefaultEditRef(mWechatPayEdt);
        setDefaultEditRef(mMultiEdt);
    }

    private void setEnvSwitch() {
        mEvnRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_release) {
                //生产配置
                mToken = "8fca880f7fe434597625535c7834769d";
                mAuthorization = "production authorization";
                ApiUrl.setEnvMode(true);
                YSAppPay.setAliEnv(true);
            } else {
                //测试配置
                mToken = "5cbfb079f15b150122261c8537086d77a";
                mAuthorization = "sandbox_ktnjwfdk_wfm342936jkm7dg6";
                ApiUrl.setEnvMode(false);
                YSAppPay.setAliEnv(false);
            }
        });
        ((RadioButton) mEvnRG.findViewById(R.id.rb_test)).setChecked(true);
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
            case R.id.btn_google_pay:
                callGooglePay();
                break;
            case R.id.btn_dropin_ui:
                callDropInUI();
                break;
        }
    }

    private void callDropInUI() {
        DropInPayActivity.launchActivity(this, mAuthorization);
    }

    private void callGooglePay() {
        YSGooglePayItem googlePayItem = new YSGooglePayItem();
        googlePayItem.setTotalPrice(Double.parseDouble(mGooglePayEdt.getText().toString()));
        YSAppPay.getInstance().startGooglePay(this, googlePayItem);
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
        info.setReference(getEditRef(mOrderEdt));
        queryOrder(mToken, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                setDefaultEditRef(mOrderEdt);
                mResultTxt.setText(result);
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }
        });
    }

    private void callRefund() {
        RefundInfo info = new RefundInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setAmount(0.01);
        info.setReference(getEditRef(mRefundEdt));
        refund(mToken, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                setDefaultEditRef(mRefundEdt);
                mResultTxt.setText(result);
            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }
        });
    }


    private void callSecurePay() {
        SecurePayInfo info = new SecurePayInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setAmount(13);
        info.setCurrency(mCurrencySpn.getSelectedItem().toString());
        info.setVendor("alipay");
        info.setTimeout(30);
        info.setReference(getEditRef(mMultiEdt));
        info.setIpnUrl("http://alipay.yunkeguan.com/ttest/test");
        info.setCallbackUrl("http://alipay.yunkeguan.com/ttest/test2");
        info.setDescription("test+description");
        info.setNote("note");
        ApiService.securePay(this.getApplicationContext(), mToken, info, new GsonResponseHandler<SecureResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, SecureResultInfo response) {
                setDefaultEditRef(mMultiEdt);
                if ("000100".equals(response.getRet_code())) {
                    mResultTxt.setText("跳转WAP:" + response.getResult().getCashierUrl());
                    launchCashierUrl(response.getResult().getCashierUrl());
                } else {
                    mResultTxt.setText(response.getRet_msg());
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
        info.setReference(getEditRef(mAlipayEdt));
        prepay(mToken, info, new GsonResponseHandler<AlipayResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, AlipayResultInfo response) {
                setDefaultEditRef(mAlipayEdt);
                if ("000100".equals(response.getRet_code())) {
                    mResultTxt.setText(response.getResult().getPayInfo());
                    YSAppPay.getInstance().startAlipay(MainActivity.this
                            , new AlipayItem(response.getResult().getPayInfo()));
                } else {
                    mResultTxt.setText(response.getRet_msg());
                }
            }
        });
    }

    private void callWechatPay() {
        PrepayInfo info = new PrepayInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setPayType(PayType.WECHAT_PAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://wx.yuansfer.yunkeguan.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(getEditRef(mWechatPayEdt));
        prepay(mToken, info, new GsonResponseHandler<WechatResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, WechatResultInfo response) {
                setDefaultEditRef(mWechatPayEdt);
                if ("000100".equals(response.getRet_code())) {
                    mResultTxt.setText(response.getResult().toString());
                    launchWechat(response.getResult());
                } else {
                    mResultTxt.setText(response.getRet_msg());
                }
            }
        });
    }

    private void prepay(@NonNull String token
            , @NonNull PrepayInfo prepayInfo, @NonNull IResponseHandler responseCallback) {
        ApiService.prepay(this.getApplicationContext(), token, prepayInfo, responseCallback);
    }

    private void refund(@NonNull String token
            , @NonNull RefundInfo refundInfo, @NonNull IResponseHandler responseCallback) {
        ApiService.refund(this.getApplicationContext(), token, refundInfo, responseCallback);
    }

    private void queryOrder(@NonNull String token
            , @NonNull DetailInfo orderInfo, @NonNull IResponseHandler responseCallback) {
        ApiService.orderStatus(this.getApplicationContext(), token, orderInfo, responseCallback);
    }

    private void launchWechat(WechatInfo wechatInfo) {
        YSAppPay.getInstance().startWechatPay(MainActivity.this, new WxPayItem.Builder()
                .setAppId(wechatInfo.getAppid())
                .setPackageValue(wechatInfo.getPackageName())
                .setPrepayId(wechatInfo.getPrepayid())
                .setPartnerId(wechatInfo.getPartnerid())
                .setNonceStr(wechatInfo.getNoncestr())
                .setSign(wechatInfo.getSign())
                .setTimestamp(wechatInfo.getTimestamp()).build());
    }

    @Override
    public void onPaySuccess(int payType) {
        mResultTxt.setText("支付成功");
    }

    @Override
    public void onPayFail(@PayType int payType, ErrStatus errStatus) {
        mResultTxt.setText(errStatus.getErrCode() + "/" + errStatus.getErrMsg());
    }

    @Override
    public void onPayCancel(int payType) {
        mResultTxt.setText("支付取消");
    }

    private String getEditRef(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString())
                ? editText.getHint().toString() : editText.getText().toString();
    }

    private void setDefaultEditRef(EditText editRef) {
        editRef.setHint(System.currentTimeMillis() + "");
    }

    @Override
    public void onReadyToPay() {
        mGooglePayBtn.setEnabled(true);
    }

    @Override
    public void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(googlePaymentCardNonce));
    }

}
