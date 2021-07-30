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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.yuansfer.pay.YSAppPay;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.api.ApiUrl;
import com.yuansfer.paysdk.model.AlipayResultInfo;
import com.yuansfer.paysdk.model.WechatResultInfo;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;
import com.yuansfer.paysdk.okhttp.RawResponseHandler;
import com.yuansfer.pay.ErrStatus;
import com.yuansfer.pay.aliwx.AliWxPayMgr;
import com.yuansfer.pay.aliwx.AliWxType;
import com.yuansfer.pay.aliwx.WxPayItem;
import com.yuansfer.paysdk.util.YSTestApi;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements ActionBar.OnNavigationListener
        , AliWxPayMgr.IAliWxPayCallback {

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
        YSAppPay.registerAliWxPayCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        YSAppPay.unregisterAliWxPayCallback(this);
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
            YSTestApi.sToken = YSTestApi.TEST_TOKEN_ALIWX;
            ApiUrl.setEnvMode(false);
            YSAppPay.setAliEnv(false);
        } else {
            YSTestApi.sToken = YSTestApi.PRODUCTION_TOKEN_ALIWX;
            ApiUrl.setEnvMode(true);
            YSAppPay.setAliEnv(true);
        }
        return true;
    }

    public void onViewClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        switch (view.getId()) {
            case R.id.btn_ali:
                YSTestApi.requestAlipay(getApplicationContext(), getEditRef(mAlipayEdt), new GsonResponseHandler<AlipayResultInfo>() {

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
                                    , response.getResult().getPayInfo());
                        } else {
                            mResultTxt.setText(response.getRet_msg());
                        }
                    }
                });
                break;
            case R.id.btn_wx:
                YSTestApi.requestWechatPay(getApplicationContext(), getEditRef(mWechatPayEdt), new GsonResponseHandler<WechatResultInfo>() {

                    @Override
                    public void onFailure(int statusCode, String errorMsg) {
                        mResultTxt.setText(errorMsg);
                    }

                    @Override
                    public void onSuccess(int statusCode, WechatResultInfo response) {
                        setDefaultEditRef(mWechatPayEdt);
                        if ("000100".equals(response.getRet_code())) {
                            mResultTxt.setText(response.getResult().toString());
                            YSAppPay.getInstance().registerWXAPP(getApplicationContext()
                                    , response.getResult().getAppid());
                            WxPayItem wxPayItem = new WxPayItem();
                            wxPayItem.setAppId(response.getResult().getAppid());
                            wxPayItem.setPackageValue(response.getResult().getPackageName());
                            wxPayItem.setPrepayId(response.getResult().getPrepayid());
                            wxPayItem.setPartnerId(response.getResult().getPartnerid());
                            wxPayItem.setNonceStr(response.getResult().getNoncestr());
                            wxPayItem.setSign(response.getResult().getSign());
                            wxPayItem.setTimestamp(response.getResult().getTimestamp());
                            YSAppPay.getInstance().requestWechatPayment(wxPayItem);
                        } else {
                            mResultTxt.setText(response.getRet_msg());
                        }
                    }
                });
                break;
            case R.id.btn_order_status:
                YSTestApi.requestTransDetail(getApplicationContext(), getEditRef(mOrderEdt), new RawResponseHandler() {
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
                break;
            case R.id.btn_refund:
                YSTestApi.requestRefund(getApplicationContext(), getEditRef(mRefundEdt), new RawResponseHandler() {
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
                break;
            case R.id.btn_secure_pay:
                YSTestApi.callAutoDebit(getApplicationContext(), mCurrencySpn.getSelectedItem().toString()
                        , getEditRef(mMultiEdt), new RawResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, String result) {
                                try {
                                    JSONObject retObj = new JSONObject(result);
                                    if ("000100".equals(retObj.optString("ret_code"))) {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(retObj.optJSONObject("result").optString("authUrl")));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        mResultTxt.setText(retObj.optString("ret_msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, String errorMsg) {
                                mResultTxt.setText(statusCode + ":" + errorMsg);
                            }
                        });
                break;
            case R.id.btn_google_pay:
                startActivity(new Intent(this, GooglePayActivity.class));
                break;
            case R.id.btn_dropin_ui:
                startActivity(new Intent(this, DropInPayActivity.class));
                break;
            case R.id.btn_paypal:
                startActivity(new Intent(this, PayPalActivity.class));
                break;
            case R.id.btn_venmo:
                startActivity(new Intent(this, VenmoActivity.class));
                break;
            case R.id.btn_creditcard:
                startActivity(new Intent(this, CardActivity.class));
                break;
        }
    }

    @Override
    public void onPaySuccess(int payType) {
        mResultTxt.setText(payType == AliWxType.ALIPAY ? "支付宝支付成功" : "微信支付成功");
    }

    @Override
    public void onPayFail(@AliWxType int payType, ErrStatus errStatus) {
        mResultTxt.setText(String.format(payType == AliWxType.ALIPAY ? "支付宝支付失败:%s" : "微信支付失败:%s"
                , errStatus.getErrCode() + "/" + errStatus.getErrMsg()));
    }

    @Override
    public void onPayCancel(int payType) {
        mResultTxt.setText(payType == AliWxType.ALIPAY ? "支付宝支付取消" : "微信支付取消");
    }

    private String getEditRef(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString())
                ? editText.getHint().toString() : editText.getText().toString();
    }

    private void setDefaultEditRef(EditText editRef) {
        editRef.setHint(System.currentTimeMillis() + "");
    }

}
