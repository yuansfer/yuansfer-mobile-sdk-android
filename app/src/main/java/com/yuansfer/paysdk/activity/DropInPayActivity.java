package com.yuansfer.paysdk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.models.BinData;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutAddress;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.yuansfer.pay.braintree.BrainTreePaymentMethod;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.payment.YSAppPay;
import com.yuansfer.pay.braintree.BrainTreeDropInActivity;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.model.CommonResultInfo;
import com.yuansfer.paysdk.model.SecureResultV3Info;
import com.yuansfer.paysdk.model.SecureV3Info;
import com.yuansfer.paysdk.okhttp.GsonResponseHandler;
import com.yuansfer.paysdk.util.YSTestApi;

public class DropInPayActivity extends BrainTreeDropInActivity implements PayResultMgr.IPayResultCallback {

    private TextView mResultTxt;
    private Button mBtnPay;
    private SecureV3Info secureV3Info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropin);
        setUpAsBackTitle();
        mResultTxt = findViewById(R.id.tv_result);
        mBtnPay = findViewById(R.id.btn_pay_start);
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
        getSupportActionBar().setTitle("Drop-In UI");
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
                    mBtnPay.setEnabled(true);
                } else {
                    mResultTxt.setText("prepay接口报错:" + response.getRet_code() + "/" + response.getRet_msg());
                }
            }

        });
    }

    public void onViewClick(View v) {
        DropInRequest dropInRequest = new DropInRequest();
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice("0.01")
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .setCurrencyCode("USD")
                        .build())
                // We recommend collecting and passing billing address information
                // with all Google Pay transactions as a best practice.
                .billingAddressRequired(true)
                // Optional in sandbox; if set in sandbox, this value must be
                // a valid production Google Merchant ID.
                .googleMerchantId("merchant-id-from-google");

        dropInRequest.googlePaymentRequest(googlePaymentRequest);
        YSAppPay.getInstance().requestDropInPayment(this, secureV3Info.getAuthorization(), dropInRequest);
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
    public void onPaymentNonceFetched(CardNonce cardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(cardNonce));
        callProcess(BrainTreePaymentMethod.CREDIT_CARD, secureV3Info.getTransactionNo()
                , cardNonce.getNonce(), deviceData);
    }

    @Override
    public void onPaymentNonceFetched(PayPalAccountNonce payPalAccountNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(payPalAccountNonce));
        callProcess(BrainTreePaymentMethod.CREDIT_CARD, secureV3Info.getTransactionNo()
                , payPalAccountNonce.getNonce(), deviceData);
    }

    @Override
    public void onPaymentNonceFetched(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(googlePaymentCardNonce));
        callProcess(BrainTreePaymentMethod.CREDIT_CARD, secureV3Info.getTransactionNo()
                , googlePaymentCardNonce.getNonce(), deviceData);
    }

    @Override
    public void onPaymentNonceFetched(VisaCheckoutNonce visaCheckoutNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(visaCheckoutNonce));
        callProcess(BrainTreePaymentMethod.CREDIT_CARD, secureV3Info.getTransactionNo()
                , visaCheckoutNonce.getNonce(), deviceData);
    }

    @Override
    public void onPaymentNonceFetched(VenmoAccountNonce venmoAccountNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(venmoAccountNonce));
        callProcess(BrainTreePaymentMethod.CREDIT_CARD, secureV3Info.getTransactionNo()
                , venmoAccountNonce.getNonce(), deviceData);
    }

    @Override
    public void onPaymentNonceFetched(LocalPaymentResult localPaymentResult, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(localPaymentResult));
        callProcess(BrainTreePaymentMethod.CREDIT_CARD, secureV3Info.getTransactionNo()
                , localPaymentResult.getNonce(), deviceData);
    }

    private void callProcess(@BrainTreePaymentMethod String paymentMethod
            , String transactionNo, String nonce, String deviceData) {
        YSTestApi.callTestProcess(getApplicationContext(), paymentMethod
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

    public static String getDisplayString(BinData binData) {
        return "Bin Data: \n" +
                "         - Prepaid: " + binData.getHealthcare() + "\n" +
                "         - Healthcare: " + binData.getHealthcare() + "\n" +
                "         - Debit: " + binData.getDebit() + "\n" +
                "         - Durbin Regulated: " + binData.getDurbinRegulated() + "\n" +
                "         - Commercial: " + binData.getCommercial() + "\n" +
                "         - Payroll: " + binData.getPayroll() + "\n" +
                "         - Issuing Bank: " + binData.getIssuingBank() + "\n" +
                "         - Country of Issuance: " + binData.getCountryOfIssuance() + "\n" +
                "         - Product Id: " + binData.getProductId();
    }

    public static String getDisplayString(CardNonce nonce) {
        return "Card Last Two: " + nonce.getLastTwo() + "\n" +
                getDisplayString(nonce.getBinData()) + "\n" +
                "3DS: \n" +
                "         - isLiabilityShifted: " + nonce.getThreeDSecureInfo().isLiabilityShifted() + "\n" +
                "         - isLiabilityShiftPossible: " + nonce.getThreeDSecureInfo().isLiabilityShiftPossible() + "\n" +
                "         - wasVerified: " + nonce.getThreeDSecureInfo().wasVerified();
    }

    public static String getDisplayString(PayPalAccountNonce nonce) {
        return "First name: " + nonce.getFirstName() + "\n" +
                "Last name: " + nonce.getLastName() + "\n" +
                "Email: " + nonce.getEmail() + "\n" +
                "Phone: " + nonce.getPhone() + "\n" +
                "Payer id: " + nonce.getPayerId() + "\n" +
                "Client metadata id: " + nonce.getClientMetadataId() + "\n" +
                "Billing address: " + formatAddress(nonce.getBillingAddress()) + "\n" +
                "Shipping address: " + formatAddress(nonce.getShippingAddress());
    }

    public static String getDisplayString(GooglePaymentCardNonce nonce) {
        return "Underlying Card Last Two: " + nonce.getLastTwo() + "\n" +
                "Card Description: " + nonce.getDescription() + "\n" +
                "Email: " + nonce.getEmail() + "\n" +
                "Billing address: " + formatAddress(nonce.getBillingAddress()) + "\n" +
                "Shipping address: " + formatAddress(nonce.getShippingAddress()) + "\n" +
                getDisplayString(nonce.getBinData());
    }

    public static String getDisplayString(VisaCheckoutNonce nonce) {
        return "User data\n" +
                "First name: " + nonce.getUserData().getUserFirstName() + "\n" +
                "Last name: " + nonce.getUserData().getUserLastName() + "\n" +
                "Full name: " + nonce.getUserData().getUserFullName() + "\n" +
                "User name: " + nonce.getUserData().getUsername() + "\n" +
                "Email: " + nonce.getUserData().getUserEmail() + "\n" +
                "Billing Address: " + formatAddress(nonce.getBillingAddress()) + "\n" +
                "Shipping Address: " + formatAddress(nonce.getShippingAddress()) + "\n" +
                getDisplayString(nonce.getBinData());
    }

    public static String getDisplayString(VenmoAccountNonce nonce) {
        return "Username: " + nonce.getUsername();
    }

    public static String getDisplayString(LocalPaymentResult nonce) {
        return "First name: " + nonce.getGivenName() + "\n" +
                "Last name: " + nonce.getSurname() + "\n" +
                "Email: " + nonce.getEmail() + "\n" +
                "Phone: " + nonce.getPhone() + "\n" +
                "Payer id: " + nonce.getPayerId() + "\n" +
                "Client metadata id: " + nonce.getClientMetadataId() + "\n" +
                "Billing address: " + formatAddress(nonce.getBillingAddress()) + "\n" +
                "Shipping address: " + formatAddress(nonce.getShippingAddress());
    }

    private static String formatAddress(PostalAddress address) {
        return address.getRecipientName() + " " +
                address.getStreetAddress() + " " +
                address.getExtendedAddress() + " " +
                address.getLocality() + " " +
                address.getRegion() + " " +
                address.getPostalCode() + " " +
                address.getCountryCodeAlpha2();
    }

    private static String formatAddress(VisaCheckoutAddress address) {
        return address.getFirstName() + " " +
                address.getLastName() + " " +
                address.getStreetAddress() + " " +
                address.getExtendedAddress() + " " +
                address.getLocality() + " " +
                address.getPostalCode() + " " +
                address.getRegion() + " " +
                address.getCountryCode() + " " +
                address.getPhoneNumber();
    }

}
