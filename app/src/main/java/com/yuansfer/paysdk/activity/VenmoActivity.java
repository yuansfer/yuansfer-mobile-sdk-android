package com.yuansfer.paysdk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.google.gson.Gson;
import com.yuansfer.pay.api.APIHelper;
import com.yuansfer.pay.api.OnResponseListener;
import com.yuansfer.pay.bean.BaseResponse;
import com.yuansfer.pay.braintree.BTCustomPayActivity;
import com.yuansfer.paysdk.model.PayProcessRequest;
import com.yuansfer.paysdk.model.SecurePayRequest;
import com.yuansfer.paysdk.util.BTMethod;
import com.yuansfer.pay.util.ErrStatus;
import com.yuansfer.pay.YSAppPay;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.model.SecureV3Response;
import com.yuansfer.paysdk.model.SecureV3Info;
import com.yuansfer.paysdk.util.YSAuth;

public class VenmoActivity extends BTCustomPayActivity {

    private TextView mResultTxt;
    private SecureV3Info secureV3Info;
    private Button mVenmoBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pay);
        setUpAsBackTitle();
        mResultTxt = findViewById(R.id.tv_result);
        mVenmoBtn = findViewById(R.id.btn_start_pay);
        mVenmoBtn.setText("Venmo");
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("Venmo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callPrepay() {
        SecurePayRequest spRequest = new SecurePayRequest();
        spRequest.setToken(YSAuth.sToken);
        spRequest.setMerchantNo(YSAuth.MERCHANT_NO);
        spRequest.setStoreNo(YSAuth.STORE_NO);
        spRequest.setAmount(0.01);
        spRequest.setCreditType("yip");
        spRequest.setVendor("paypal");
        spRequest.setReference(System.currentTimeMillis() + "");
        spRequest.setIpnUrl("https://yuansferdev.com/callback");
        spRequest.setDescription("test+description");
        spRequest.setNote("note");
        YSAppPay.getInstance().getClientAPI().apiPost("/online/v3/secure-pay", new Gson().toJson(spRequest)
                , new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        SecureV3Response response = APIHelper.convertResponseKeepRaw(new Gson(), s, SecureV3Response.class);
                        if (response.isSuccess()) {
                            secureV3Info = response.getResult();
                            mResultTxt.setText(secureV3Info.toString());
                            YSAppPay.getInstance().bindBrainTree(VenmoActivity.this, secureV3Info.getAuthorization());
                        } else {
                            mResultTxt.setText("prepay接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mResultTxt.setText(e.getMessage());
                    }
                });
    }

    private void callPayProcess(String transactionNo, String nonce, String deviceData) {
        PayProcessRequest ppRequest = new PayProcessRequest();
        ppRequest.setToken(YSAuth.sToken);
        ppRequest.setMerchantNo(YSAuth.MERCHANT_NO);
        ppRequest.setStoreNo(YSAuth.STORE_NO);
        ppRequest.setPaymentMethod(BTMethod.VENMO_ACCOUNT);
        ppRequest.setPaymentMethodNonce(nonce);
        ppRequest.setTransactionNo(transactionNo);
        ppRequest.setDeviceData(deviceData);
        YSAppPay.getInstance().getClientAPI().apiPost("/creditpay/v3/process", new Gson().toJson(ppRequest)
                , new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        BaseResponse response = APIHelper.convertResponseKeepRaw(new Gson(), s, BaseResponse.class);
                        if (response.isSuccess()) {
                            //支付成功
                            mResultTxt.setText(response.getRet_msg());
                        } else {
                            mResultTxt.setText("process接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mResultTxt.setText(e.getMessage());
                    }
                });
    }

    public void onViewClick(View v) {
        if (secureV3Info != null) {
            YSAppPay.getInstance().requestVenmoPayment(this, false);
        }
    }

    @Override
    public void onPrepayCancel() {
        mResultTxt.setText("支付取消");
    }

    @Override
    public void onPrepayError(ErrStatus errStatus) {
        mResultTxt.setText(errStatus.getErrCode() + "/" + errStatus.getErrMsg());
    }

    @Override
    public void onPaymentConfigurationFetched(Configuration configuration) {
        if (configuration.getPayWithVenmo().isEnabled(this)) {
            mVenmoBtn.setEnabled(true);
        } else if (configuration.getPayWithVenmo().isAccessTokenValid()) {
            showDialog("Please install the Venmo app first.");
        } else {
            showDialog("Venmo is not enabled for the current merchant.");
        }
    }

    @Override
    public void onPaymentNonceFetched(VenmoAccountNonce venmoAccountNonce, String deviceData) {
        super.onPaymentNonceFetched(venmoAccountNonce, deviceData);
        mResultTxt.setText(DropInPayActivity.getDisplayString(venmoAccountNonce));
        callPayProcess(secureV3Info.getTransactionNo(), venmoAccountNonce.getNonce(), deviceData);
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getInstance().unbindBrainTree(this);
    }

}
