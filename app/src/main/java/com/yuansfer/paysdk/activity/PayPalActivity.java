package com.yuansfer.paysdk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
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
import com.yuansfer.paysdk.util.Logger;
import com.yuansfer.paysdk.util.YSAuth;

public class PayPalActivity extends BTCustomPayActivity {

    private Logger mLogger;
    private SecureV3Info secureV3Info;
    private Button mPayPalBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pay);
        setUpAsBackTitle();
        mLogger = new Logger(findViewById(R.id.tv_result));
        mPayPalBtn = findViewById(R.id.btn_start_pay);
        mPayPalBtn.setText("PayPal");
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("PayPal");
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
        YSAppPay.getClientAPI().apiPost("/online/v3/secure-pay", new Gson().toJson(spRequest)
                , new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        SecureV3Response response = APIHelper.convertResponseKeepRaw(new Gson(), s, SecureV3Response.class);
                        if (response.isSuccess()) {
                            secureV3Info = response.getResult();
                            mLogger.log(secureV3Info.toString());
                            YSAppPay.getBraintreePay().bindBrainTree(PayPalActivity.this, secureV3Info.getAuthorization());
                        } else {
                            mLogger.log("prepay error:" + response.getRet_code() + "/" + response.getRet_msg());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getMessage());
                    }
                });
    }

    private void callPayProcess(String transactionNo, String nonce, String deviceData) {
        PayProcessRequest ppRequest = new PayProcessRequest();
        ppRequest.setToken(YSAuth.sToken);
        ppRequest.setMerchantNo(YSAuth.MERCHANT_NO);
        ppRequest.setStoreNo(YSAuth.STORE_NO);
        ppRequest.setPaymentMethod(BTMethod.PAYPAL_ACCOUNT);
        ppRequest.setPaymentMethodNonce(nonce);
        ppRequest.setTransactionNo(transactionNo);
        ppRequest.setDeviceData(deviceData);
        YSAppPay.getClientAPI().apiPost("/creditpay/v3/process", new Gson().toJson(ppRequest)
                , new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        BaseResponse response = APIHelper.convertResponseKeepRaw(new Gson(), s, BaseResponse.class);
                        if (response.isSuccess()) {
                            //支付成功
                            mLogger.log(response.getRet_msg());
                        } else {
                            mLogger.log("process error:" + response.getRet_code() + "/" + response.getRet_msg());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getMessage());
                    }
                });
    }

    public void onViewClick(View v) {
        if (secureV3Info != null) {
            YSAppPay.getBraintreePay().requestPayPalOneTimePayment(this
                    , getPayPalRequest(secureV3Info.getAmount() + ""));
        }
    }

    private PayPalRequest getPayPalRequest(@Nullable String amount) {
        PayPalRequest request = new PayPalRequest(amount);

        request.displayName("PayPal display name");

//        request.landingPageType(PayPalRequest.LANDING_PAGE_TYPE_BILLING);
        request.landingPageType(PayPalRequest.LANDING_PAGE_TYPE_LOGIN);

        request.intent(PayPalRequest.INTENT_SALE);

        request.userAction(PayPalRequest.USER_ACTION_COMMIT);

        request.offerCredit(true);

        return request;
    }

    @Override
    public void onPrepayCancel() {
        mLogger.log("Pay cancel");
    }

    @Override
    public void onPrepayError(ErrStatus errStatus) {
        mLogger.log(errStatus.getErrCode() + "/" + errStatus.getErrMsg());
    }

    @Override
    public void onPaymentConfigurationFetched(Configuration configuration) {
        mPayPalBtn.setEnabled(true);
    }

    @Override
    public void onPaymentNonceFetched(PayPalAccountNonce payPalAccountNonce, String deviceData) {
        super.onPaymentNonceFetched(payPalAccountNonce, deviceData);
        mLogger.log(DropInPayActivity.getDisplayString(payPalAccountNonce));
        callPayProcess(secureV3Info.getTransactionNo(), payPalAccountNonce.getNonce(), deviceData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getBraintreePay().unbindBrainTree(this);
    }

}
