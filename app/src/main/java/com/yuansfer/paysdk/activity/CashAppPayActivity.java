package com.yuansfer.paysdk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.yuansfer.pay.YSAppPay;
import com.yuansfer.pay.api.OnResponseListener;
import com.yuansfer.pay.cashapp.ICashAppPay;
import com.yuansfer.pay.cashapp.ICashPayCallback;
import com.yuansfer.pay.cashapp.OnFileItem;
import com.yuansfer.pay.cashapp.OneTimeItem;
import com.yuansfer.pay.util.YSException;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.model.MicroPayRequest;
import com.yuansfer.paysdk.util.Logger;
import com.yuansfer.paysdk.util.YSAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CashAppPayActivity extends AppCompatActivity implements ICashPayCallback {

    private Logger mLogger;
    private Button btnCashPay;
    private boolean isProduction;
    private ICashAppPay cashAppPay;
    // intent-filter/data
    private final String redirectUrl = "cashpaykit://checkout";

    public static void launchActivity(Context context, boolean production) {
        Intent intent = new Intent(context, CashAppPayActivity.class);
        intent.putExtra("production", production);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpAsBackTitle();
        setContentView(R.layout.activity_all_pay);
        mLogger = new Logger(findViewById(R.id.tv_result));
        btnCashPay = findViewById(R.id.btn_start_pay);
        btnCashPay.setText("Cash App Pay");
        isProduction = getIntent().getBooleanExtra("production", true);
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("Cash App Pay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callPrepay() {
        MicroPayRequest info = new MicroPayRequest();
        info.setToken(YSAuth.sToken);
        info.setMerchantNo(YSAuth.MERCHANT_NO);
        info.setStoreNo(YSAuth.STORE_NO);
        info.setVendor("cashapppay");
        info.setAmount(0.01);
        info.setIpnUrl("https://xxxx.com/cashapppay/notify");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(Calendar.getInstance().getTimeInMillis()+"");
        YSAppPay.getClientAPI().apiPost("/micropay/v3/prepay", new Gson().toJson(info)
                , new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mLogger.log(s);
                        handlePrepay(s);
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getMessage());
                    }
                });
    }

    private void handlePrepay(String result) {
        try {
            JSONObject dataObj = new JSONObject(result);
            if (dataObj.has("result")) {
                JSONObject resultObj = dataObj.getJSONObject("result");
                if (isProduction) {
                    cashAppPay = YSAppPay.getCashAppPay(resultObj.optString("clientId"));
                } else {
                    cashAppPay = YSAppPay.getSandboxCashAppPay(resultObj.optString("clientId"));
                }
                cashAppPay.registerPayEventCallback(CashAppPayActivity.this);
                String scopeId = resultObj.optString("scopeId");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if ("cit".equals(resultObj.optString("creditType"))) {
                            cashAppPay.requestCashAppPayInBackground(new OnFileItem(redirectUrl
                                    , scopeId, resultObj.optString("merchantNo")));
                        } else {
                            try {
                                cashAppPay.requestCashAppPayInBackground(new OneTimeItem(redirectUrl
                                        , scopeId, resultObj.optString("currency"), resultObj.optDouble("amount")));
                            } catch (YSException e) {
                                mLogger.log(e.getMessage());
                            }
                        }
                    }
                }).start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mLogger.log(e.getMessage());
        }
    }

    public void onViewClick(View view) {
        if (cashAppPay != null) {
            try {
                cashAppPay.authorizeCashAppPay(this);
            } catch (YSException e) {
                mLogger.log(e.getMessage());
            }
        }
    }

    private void setPayFinished() {
        btnCashPay.setEnabled(false);
//        if (cashAppPay != null) {
//            cashAppPay.unRegisterPayEventCallback();
//        }
    }

    @Override
    public void onReadyToAuthorize() {
        btnCashPay.setEnabled(true);
        mLogger.log("Ready to authorize");
    }

    @Override
    public void onApproved() {
        mLogger.log("Pay success");
        setPayFinished();
    }

    @Override
    public void onDeclined() {
        mLogger.log("Pay declined");
        setPayFinished();
    }

    @Override
    public void onEventUpdate(String event) {
        mLogger.log("Pay event:" + event);
    }
}
