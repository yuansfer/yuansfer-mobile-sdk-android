package com.yuansfer.pay.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TransInitBean implements Parcelable {

    private double amount;
    private String createTime;
    private String currency;
    private String merchantNo;
    private String originalTransactionNo;
    private String paymentTime;
    private String reference;
    private double refundAmount;
    private String settleCurrency;
    private String storeNo;
    private String transactionNo;
    private String transactionStatus;
    private String transactionType;
    private double voidAmount;
    private String vendor;

    protected TransInitBean(Parcel in) {
        amount = in.readDouble();
        createTime = in.readString();
        currency = in.readString();
        merchantNo = in.readString();
        originalTransactionNo = in.readString();
        paymentTime = in.readString();
        reference = in.readString();
        refundAmount = in.readDouble();
        settleCurrency = in.readString();
        storeNo = in.readString();
        transactionNo = in.readString();
        transactionStatus = in.readString();
        transactionType = in.readString();
        voidAmount = in.readDouble();
        vendor = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(amount);
        dest.writeString(createTime);
        dest.writeString(currency);
        dest.writeString(merchantNo);
        dest.writeString(originalTransactionNo);
        dest.writeString(paymentTime);
        dest.writeString(reference);
        dest.writeDouble(refundAmount);
        dest.writeString(settleCurrency);
        dest.writeString(storeNo);
        dest.writeString(transactionNo);
        dest.writeString(transactionStatus);
        dest.writeString(transactionType);
        dest.writeDouble(voidAmount);
        dest.writeString(vendor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransInitBean> CREATOR = new Creator<TransInitBean>() {
        @Override
        public TransInitBean createFromParcel(Parcel in) {
            return new TransInitBean(in);
        }

        @Override
        public TransInitBean[] newArray(int size) {
            return new TransInitBean[size];
        }
    };

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOriginalTransactionNo() {
        return originalTransactionNo;
    }

    public void setOriginalTransactionNo(String originalTransactionNo) {
        this.originalTransactionNo = originalTransactionNo;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getSettleCurrency() {
        return settleCurrency;
    }

    public void setSettleCurrency(String settleCurrency) {
        this.settleCurrency = settleCurrency;
    }

    public String getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(String storeNo) {
        this.storeNo = storeNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getVoidAmount() {
        return voidAmount;
    }

    public void setVoidAmount(double voidAmount) {
        this.voidAmount = voidAmount;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
