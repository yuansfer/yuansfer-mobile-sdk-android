package com.yuansfer.paysdk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.GooglePayment;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
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
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.model.SecureV3Response;
import com.yuansfer.paysdk.model.SecureV3Info;
import com.yuansfer.paysdk.util.Logger;
import com.yuansfer.paysdk.util.YSAuth;

public class GooglePayActivity extends BTCustomPayActivity {

    private Logger mLogger;
    private SecureV3Info secureV3Info;
    private Button mBtnGooglePay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pay);
        setUpAsBackTitle();
        mLogger = new Logger(findViewById(R.id.tv_result));
        mBtnGooglePay = findViewById(R.id.btn_start_pay);
        mBtnGooglePay.setText("Google Pay");
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("Google Pay");
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
                            YSAppPay.getBraintreePay().bindBrainTree(GooglePayActivity.this, secureV3Info.getAuthorization());
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
        ppRequest.setPaymentMethod(BTMethod.ANDROID_PAY_CARD);
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
            GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                    .transactionInfo(TransactionInfo.newBuilder()
                            .setTotalPrice(secureV3Info.getAmount() + "")
                            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                            .setCurrencyCode("USD")
                            .build())
                    .billingAddressRequired(true)
                    .googleMerchantId("merchant-id-from-google");
            YSAppPay.getBraintreePay().requestGooglePayment(GooglePayActivity.this, googlePaymentRequest);
        }
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
        if (configuration.getGooglePayment().isEnabled(this)) {
            GooglePayment.isReadyToPay(getBrainTreeFragment(), new BraintreeResponseListener<Boolean>() {
                @Override
                public void onResponse(Boolean isReadyToPay) {
                    if (isReadyToPay) {
                        LogUtils.d("Google Pay service available");
                        mBtnGooglePay.setEnabled(true);
                    } else {
                        mLogger.log("Google Payments are not available. The following issues could be the cause:\n\n" +
                                "No user is logged in to the device.\n\n" +
                                "Google Play Services is missing or out of date.");
                    }
                }
            });
        } else {
            mLogger.log("Google Payments are not available. The following issues could be the cause:\n\n" +
                    "Google Payments are not enabled for the current merchant.\n\n" +
                    "Google Play Services is missing or out of date.");
        }
    }

    @Override
    public void onPaymentNonceFetched(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        mLogger.log(DropInPayActivity.getDisplayString(googlePaymentCardNonce));
        callPayProcess(secureV3Info.getTransactionNo(), googlePaymentCardNonce.getNonce(), deviceData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getBraintreePay().unbindBrainTree(this);
    }

}
