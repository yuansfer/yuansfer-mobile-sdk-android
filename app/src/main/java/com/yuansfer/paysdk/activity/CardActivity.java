package com.yuansfer.paysdk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.cardform.OnCardFormFieldFocusedListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.view.CardForm;
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

public class CardActivity extends BTCustomPayActivity implements
        OnCardFormFieldFocusedListener, OnCardFormSubmitListener {

    private SecureV3Info secureV3Info;
    private Button mCardBtn;
    private CardForm mCardForm;
    private Logger mLogger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);
        setUpAsBackTitle();
        mLogger = new Logger(findViewById(R.id.tv_result));
        mCardBtn = findViewById(R.id.btn_start_pay);
        mCardBtn.setText("Card Pay");
        mCardForm = findViewById(R.id.card_form);
        mCardForm.setOnFormFieldFocusedListener(this);
        mCardForm.setOnCardFormSubmitListener(this);
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("DebitCard or CreditCard");
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
                            YSAppPay.getBraintreePay().bindBrainTree(CardActivity.this, secureV3Info.getAuthorization());
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
        ppRequest.setPaymentMethod(BTMethod.CREDIT_CARD);
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
            CardBuilder cardBuilder = new CardBuilder()
                    .cardNumber(mCardForm.getCardNumber())
                    .expirationMonth(mCardForm.getExpirationMonth())
                    .expirationYear(mCardForm.getExpirationYear())
                    .cvv(mCardForm.getCvv())
                    .validate(false) // TODO GQL currently only returns the bin if validate = false
                    .postalCode(mCardForm.getPostalCode());
            YSAppPay.getBraintreePay().requestCardPayment(this, cardBuilder);
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
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Card Pay")
                .setup(this);
        mCardBtn.setEnabled(true);
    }

    @Override
    public void onPaymentNonceFetched(CardNonce cardNonce, String deviceData) {
        super.onPaymentNonceFetched(cardNonce, deviceData);
        mLogger.log(DropInPayActivity.getDisplayString(cardNonce));
        callPayProcess(secureV3Info.getTransactionNo(), cardNonce.getNonce(), deviceData);
    }

    @Override
    public void onCardFormFieldFocused(View field) {

    }

    @Override
    public void onCardFormSubmit() {
        onViewClick(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getBraintreePay().unbindBrainTree(this);
    }

}
