package com.yuansfer.paysdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.models.BinData;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.LocalPaymentResult;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.braintreepayments.api.models.VisaCheckoutAddress;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.payment.YSAppPay;
import com.yuansfer.pay.dropin.YSDropInPayActivity;
import com.yuansfer.pay.googlepay.YSGooglePayItem;

public class DropInPayActivity extends YSDropInPayActivity implements PayResultMgr.IPayResultCallback {

    private TextView mResultTxt;

    public static void launchActivity(Context context, String authorization) {
        Intent intent = new Intent(context, DropInPayActivity.class);
        intent.putExtra("auth", authorization);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropin);
        setUpAsBackTitle();
        mResultTxt = findViewById(R.id.tv_result);
    }

    private void setUpAsBackTitle() {
        getSupportActionBar().setTitle("Drop-In UI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onViewClick(View v) {
        YSGooglePayItem googlePayItem = new YSGooglePayItem();
        googlePayItem.setCurrency("USD");
        googlePayItem.setTotalPrice(0.01);
        YSAppPay.getInstance().startDropInPayment(this, getIntent()
                .getStringExtra("auth"), new DropInRequest(), googlePayItem);
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
    public void onPaymentMethodResult(CardNonce cardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(cardNonce));
    }

    @Override
    public void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(payPalAccountNonce));
    }

    @Override
    public void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(googlePaymentCardNonce));
    }

    @Override
    public void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(visaCheckoutNonce));
    }

    @Override
    public void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(venmoAccountNonce));
    }

    @Override
    public void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData) {
        mResultTxt.setText(DropInPayActivity.getDisplayString(localPaymentResult));
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
