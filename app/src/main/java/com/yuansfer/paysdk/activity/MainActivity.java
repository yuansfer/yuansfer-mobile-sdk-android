package com.yuansfer.paysdk.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.yuansfer.pay.payment.YSAppPay;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.api.ApiUrl;
import com.yuansfer.paysdk.model.AlipayResultInfo;
import com.yuansfer.paysdk.model.AutoDebitInfo;
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
import com.yuansfer.pay.wxpay.WxPayItem;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements ActionBar.OnNavigationListener
        , PayResultMgr.IPayResultCallback {

    private static final String TEST_TOKEN = "5cbfb079f15b150122261c8537086d77a";
    private static final String PRODUCTION_TOKEN = "8fca880f7fe434597625535c7834769d";
    private static final String TEST_AUTHORIZATION = "sandbox_ktnjwfdk_wfm342936jkm7dg6";
    private static final String PRODUCTION_AUTHORIZATION = "sandbox_ktnjwfdk_wfm342936jkm7dg6";
    private String mToken = TEST_TOKEN;
    private String mAuthorization = TEST_AUTHORIZATION;
    private String mMerchantNo = "200043";
    private String mStoreNo = "300014";
    private TextView mResultTxt;
    private EditText mAlipayEdt, mWechatPayEdt, mOrderEdt, mRefundEdt, mMultiEdt;
    private Spinner mCurrencySpn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupActionBar();
        YSAppPay.setLogEnable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        YSAppPay.registerPayResultCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        YSAppPay.unregisterPayResultCallback(this);
    }

    private void initViews() {
        mResultTxt = findViewById(R.id.tv_result);
        mAlipayEdt = findViewById(R.id.edt_ali);
        mWechatPayEdt = findViewById(R.id.edt_wx);
        mOrderEdt = findViewById(R.id.edt_order_status);
        mRefundEdt = findViewById(R.id.edt_order_refund);
        mMultiEdt = findViewById(R.id.edt_secure_pay);
        mCurrencySpn = findViewById(R.id.sp_multi_currency);
        setDefaultEditRef(mAlipayEdt);
        setDefaultEditRef(mRefundEdt);
        setDefaultEditRef(mOrderEdt);
        setDefaultEditRef(mWechatPayEdt);
        setDefaultEditRef(mMultiEdt);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.environments,
                android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, this);
        actionBar.setSelectedNavigationItem(0);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        String env = getResources().getStringArray(R.array.environments)[itemPosition];
        if ("TEST".equals(env)) {
            mToken = TEST_TOKEN;
            mAuthorization = TEST_AUTHORIZATION;
            ApiUrl.setEnvMode(false);
            YSAppPay.setAliEnv(false);
        } else {
            mToken = PRODUCTION_TOKEN;
            mAuthorization = PRODUCTION_AUTHORIZATION;
            ApiUrl.setEnvMode(true);
            YSAppPay.setAliEnv(true);
            Toast.makeText(this, "Braintree等支付Demo中均使用的沙箱账号，后期再添加生产账号进行测试", Toast.LENGTH_LONG).show();
        }
        return true;
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
                callAutoDebit();
                break;
            case R.id.btn_google_pay:
                callGooglePay();
                break;
            case R.id.btn_dropin_ui:
                callDropInUI();
                break;
            case R.id.btn_paypal:
                callPayPal();
                break;
            case R.id.btn_venmo:
                callVenmo();
                break;
            case R.id.btn_creditcard:
                callCredit();
                break;
        }
    }

    private void callCredit() {
        startActivity(new Intent(this, CardActivity.class));
    }

    private void callVenmo() {
        startActivity(new Intent(this, VenmoActivity.class));
    }

    private void callPayPal() {
        startActivity(new Intent(this, PayPalActivity.class));
    }

    private void callGooglePay() {
        startActivity(new Intent(this, GooglePayActivity.class));
    }

    private void callDropInUI() {
        startActivity(new Intent(this, DropInPayActivity.class));
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

    private void callAutoDebit() {
        AutoDebitInfo info = new AutoDebitInfo();
        info.setMerchantNo(mMerchantNo);
        info.setStoreNo(mStoreNo);
        info.setAutoIpnUrl("http://alipay.yunkeguan.com/ttest/test");
        info.setAutoRedirectUrl("http://alipay.yunkeguan.com/ttest/test2");
        info.setAutoReference(getEditRef(mMultiEdt));
        info.setNote("note");
        info.setOsType("ANDROID");
        info.setOsVersion("10");
        info.setTerminal("APP");
        info.setVendor(mCurrencySpn.getSelectedItem().toString());
        ApiService.autoDebit(this.getApplicationContext(), mToken, info, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String result) {
                try {
                    JSONObject retObj = new JSONObject(result);
                    if ("000100".equals(retObj.optString("ret_code"))) {
                        launchCashierUrl(retObj.optJSONObject("result").optString("authUrl"));
                    } else {
                        mResultTxt.setText(retObj.optString("ret_msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(statusCode+":"+errorMsg);
            }
        });
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
                    YSAppPay.getInstance().requestAliPayment(MainActivity.this
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
        info.setIpnUrl("https://app.yuansfer.com/wx");
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
        YSAppPay.getInstance().requestWechatPayment(MainActivity.this, new WxPayItem.Builder()
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

}
