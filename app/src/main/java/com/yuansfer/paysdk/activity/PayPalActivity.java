package com.yuansfer.paysdk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.yuansfer.pay.braintree.BrainTreePayActivity;
import com.yuansfer.pay.braintree.BrainTreePaymentMethod;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.payment.YSAppPay;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.model.CommonResultInfo;
import com.yuansfer.paysdk.model.PayProcessInfo;
import com.yuansfer.paysdk.model.SecurePayInfo;
import com.yuansfer.paysdk.model.SecureResultV3Info;
import com.yuansfer.paysdk.model.SecureV3Info;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;
import com.yuansfer.paysdk.util.YSTestApi;

public class PayPalActivity extends BrainTreePayActivity implements PayResultMgr.IPayResultCallback {

    private TextView mResultTxt;
    private SecureV3Info secureV3Info;
    private Button mPayPalBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pay);
        setUpAsBackTitle();
        mResultTxt = findViewById(R.id.tv_result);
        mPayPalBtn = findViewById(R.id.btn_start_pay);
        mPayPalBtn.setText("PayPal");
        callPrepay();
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

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("PayPal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callPrepay() {
        YSTestApi.callTestPrepay(getApplicationContext(), new GsonResponseHandler<SecureResultV3Info>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, SecureResultV3Info response) {
                if ("000100".equals(response.getRet_code())) {
                    secureV3Info = response.getResult();
                    mResultTxt.setText(secureV3Info.toString());
                    YSAppPay.getInstance().bindBrainTree(PayPalActivity.this, secureV3Info.getAuthorization());
                } else {
                    mResultTxt.setText("prepay接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                }
            }

        });
    }

    private void callPayProcess(String transactionNo, String nonce, String deviceData) {
        YSTestApi.callTestProcess(getApplicationContext(), BrainTreePaymentMethod.PAYPAL_ACCOUNT
                , transactionNo, nonce, deviceData, new GsonResponseHandler<CommonResultInfo>() {

                    @Override
                    public void onFailure(int statusCode, String errorMsg) {
                        mResultTxt.setText(errorMsg);
                    }

                    @Override
                    public void onSuccess(int statusCode, CommonResultInfo response) {
                        if ("000100".equals(response.getRet_code())) {
                            //支付成功
                            mResultTxt.setText("process接口成功:" + response.getRet_msg());
                        } else {
                            mResultTxt.setText("process接口报错:" + response.getRet_code() + "/" + response.getRet_msg());
                        }
                    }
                });
    }

    public void onViewClick(View v) {
        if (secureV3Info != null) {
            YSAppPay.getInstance().requestPayPalOneTimePayment(this
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
    public void onPaymentConfigurationFetched(Configuration configuration) {
        mPayPalBtn.setEnabled(true);
    }

    @Override
    public void onPaymentNonceFetched(PayPalAccountNonce payPalAccountNonce, String deviceData) {
        super.onPaymentNonceFetched(payPalAccountNonce, deviceData);
        mResultTxt.setText(DropInPayActivity.getDisplayString(payPalAccountNonce));
        callPayProcess(secureV3Info.getTransactionNo(), payPalAccountNonce.getNonce(), deviceData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getInstance().unbindBrainTree(this);
    }

}
