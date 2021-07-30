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
import com.yuansfer.pay.braintree.BTCustomPayActivity;
import com.yuansfer.paysdk.util.BTMethod;
import com.yuansfer.pay.ErrStatus;
import com.yuansfer.pay.YSAppPay;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.model.CommonResultInfo;
import com.yuansfer.paysdk.model.SecureResultV3Info;
import com.yuansfer.paysdk.model.SecureV3Info;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;
import com.yuansfer.paysdk.util.YSTestApi;

public class GooglePayActivity extends BTCustomPayActivity {

    private TextView mResultTxt;
    private SecureV3Info secureV3Info;
    private Button mBtnGooglePay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pay);
        setUpAsBackTitle();
        mResultTxt = findViewById(R.id.tv_result);
        mBtnGooglePay = findViewById(R.id.btn_start_pay);
        mBtnGooglePay.setText("Google Pay");
        callPrepay();
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("Google Pay");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void callPrepay() {
        YSTestApi.requestBTPrepay(getApplicationContext(), new GsonResponseHandler<SecureResultV3Info>() {

            @Override
            public void onFailure(int statusCode, String errorMsg) {
                mResultTxt.setText(errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, SecureResultV3Info response) {
                if ("000100".equals(response.getRet_code())) {
                    secureV3Info = response.getResult();
                    mResultTxt.setText(secureV3Info.toString());
                    YSAppPay.getInstance().bindBrainTree(GooglePayActivity.this, secureV3Info.getAuthorization());
                } else {
                    mResultTxt.setText("prepay接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                }
            }

        });
    }

    private void callPayProcess(String transactionNo, String nonce, String deviceData) {
        YSTestApi.requestBTProcess(getApplicationContext(), BTMethod.ANDROID_PAY_CARD
                , transactionNo, nonce, deviceData, new GsonResponseHandler<CommonResultInfo>() {

                    @Override
                    public void onFailure(int statusCode, String errorMsg) {
                        mResultTxt.setText(errorMsg);
                    }

                    @Override
                    public void onSuccess(int statusCode, CommonResultInfo response) {
                        if ("000100".equals(response.getRet_code())) {
                            //google pay支付成功
                            mResultTxt.setText("process接口成功:" + response.getRet_msg());
                        } else {
                            mResultTxt.setText("process接口报错" + response.getRet_code() + "/" + response.getRet_msg());
                        }
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
                    // We recommend collecting and passing billing address information
                    // with all Google Pay transactions as a best practice.
                    .billingAddressRequired(true)
                    // Optional in sandbox; if set in sandbox, this value must be
                    // a valid production Google Merchant ID.
                    .googleMerchantId("merchant-id-from-google");
            YSAppPay.getInstance().requestGooglePayment(GooglePayActivity.this, googlePaymentRequest);
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
        if (configuration.getGooglePayment().isEnabled(this)) {
            GooglePayment.isReadyToPay(getBrainTreeFragment(), new BraintreeResponseListener<Boolean>() {
                @Override
                public void onResponse(Boolean isReadyToPay) {
                    if (isReadyToPay) {
                        LogUtils.d("Google Pay服务可用");
                        mBtnGooglePay.setEnabled(true);
                    } else {
                        mResultTxt.setText("Google Payments are not available. The following issues could be the cause:\n\n" +
                                "No user is logged in to the device.\n\n" +
                                "Google Play Services is missing or out of date.");
                    }
                }
            });
        } else {
            mResultTxt.setText("Google Payments are not available. The following issues could be the cause:\n\n" +
                    "Google Payments are not enabled for the current merchant.\n\n" +
                    "Google Play Services is missing or out of date.");
        }
    }

    @Override
    public void onPaymentNonceFetched(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(googlePaymentCardNonce));
        callPayProcess(secureV3Info.getTransactionNo(), googlePaymentCardNonce.getNonce(), deviceData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getInstance().unbindBrainTree(this);
    }

}
