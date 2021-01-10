package com.yuansfer.paysdk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.yuansfer.pay.googlepay.YSGooglePayActivity;
import com.yuansfer.pay.googlepay.YSGooglePayItem;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.payment.YSAppPay;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.model.CommonResultInfo;
import com.yuansfer.paysdk.model.PayProcessInfo;
import com.yuansfer.paysdk.model.SecurePayInfo;
import com.yuansfer.paysdk.model.SecureResultV3Info;
import com.yuansfer.paysdk.model.SecureV3Info;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;

public class GooglePayActivity extends YSGooglePayActivity implements PayResultMgr.IPayResultCallback {

    private TextView mResultTxt;
    private SecureV3Info secureV3Info;
    private Button mBtnGooglePay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlepay);
        setUpAsBackTitle();
        mResultTxt = findViewById(R.id.tv_result);
        mBtnGooglePay = findViewById(R.id.btn_google_pay);
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("Google Pay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callPrepay() {
        SecurePayInfo info = new SecurePayInfo();
        info.setMerchantNo("202333");
        info.setStoreNo("301854");
        info.setAmount(0.01);
        info.setCreditType("yip");
        info.setVendor("paypal");
        info.setReference(System.currentTimeMillis()+"");
        info.setIpnUrl("https://yuansferdev.com/callback");
        info.setDescription("test+description");
        info.setNote("note");
        ApiService.securePay(this.getApplicationContext(), "17cfc0170ef1c017b4a929d233d6e65e", info, new GsonResponseHandler<SecureResultV3Info>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, SecureResultV3Info response) {
                if ("000100".equals(response.getRet_code())) {
                    secureV3Info = response.getResult();
                    mResultTxt.setText(secureV3Info.toString());
                    YSAppPay.getInstance().bindGooglePay(GooglePayActivity.this, secureV3Info.getAuthorization());
                } else {
                    mResultTxt.setText("prepay接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                }
            }

        });
    }

    private void callPayProcess(String transactionNo, String nonce, String deviceData) {
        PayProcessInfo processInfo = new PayProcessInfo();
        processInfo.setMerchantNo("202333");
        processInfo.setStoreNo("301854");
        processInfo.setPaymentMethod("android_pay_card");
        processInfo.setPaymentMethodNonce(nonce);
        processInfo.setTransactionNo(transactionNo);
        processInfo.setDeviceData(deviceData);
        ApiService.braintreePay(this.getApplicationContext(), "17cfc0170ef1c017b4a929d233d6e65e", processInfo, new GsonResponseHandler<CommonResultInfo>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, CommonResultInfo response) {
                if ("000100".equals(response.getRet_code())) {
                    //google pay支付成功
                    mResultTxt.setText(response.getRet_msg());
                } else {
                    mResultTxt.setText("process接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                }
            }
        });
    }

    public void onViewClick(View v) {
        if (secureV3Info != null) {
            YSGooglePayItem googlePayItem = new YSGooglePayItem();
            googlePayItem.setTotalPrice(secureV3Info.getAmount());
            YSAppPay.getInstance().startGooglePay(GooglePayActivity.this, googlePayItem);
        }
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

    @Override
    public void onReadyToPay() {
        LogUtils.d("Google Pay服务可用");
        mBtnGooglePay.setEnabled(true);
    }

    @Override
    public void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(googlePaymentCardNonce));
        callPayProcess(secureV3Info.getTransactionNo(), googlePaymentCardNonce.getNonce(), deviceData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getInstance().unbindGooglePay(this);
    }

}
