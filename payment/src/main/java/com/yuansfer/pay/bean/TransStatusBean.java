package com.yuansfer.pay.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TransStatusBean implements Parcelable {

    private double amount;
    private String currency;
    private String settleCurrency;
    private String merchantNo;
    private String reference;
    private String status;
    private String transactionType;
    private String transactionNo;

    protected TransStatusBean(Parcel in) {
        amount = in.readDouble();
        currency = in.readString();
        settleCurrency = in.readString();
        merchantNo = in.readString();
        reference = in.readString();
        status = in.readString();
        transactionType = in.readString();
        transactionNo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(amount);
        dest.writeString(currency);
        dest.writeString(settleCurrency);
        dest.writeString(merchantNo);
        dest.writeString(reference);
        dest.writeString(status);
        dest.writeString(transactionType);
        dest.writeString(transactionNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransStatusBean> CREATOR = new Creator<TransStatusBean>() {
        @Override
        public TransStatusBean createFromParcel(Parcel in) {
            return new TransStatusBean(in);
        }

        @Override
        public TransStatusBean[] newArray(int size) {
            return new TransStatusBean[size];
        }
    };

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSettleCurrency() {
        return settleCurrency;
    }

    public void setSettleCurrency(String settleCurrency) {
        this.settleCurrency = settleCurrency;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }
}
