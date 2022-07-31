package com.yuansfer.pay.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TransRefundBean implements Parcelable {

    private double amount;
    private String currency;
    private String exchangeRate;
    private String reference;
    private double refundAmount;
    private String refundReference;
    private String refundTransactionId;
    private String settleCurrency;
    private String oldTransactionId;
    private String status;

    public TransRefundBean() {
    }

    protected TransRefundBean(Parcel in) {
        amount = in.readDouble();
        currency = in.readString();
        exchangeRate = in.readString();
        reference = in.readString();
        refundAmount = in.readDouble();
        refundReference = in.readString();
        refundTransactionId = in.readString();
        settleCurrency = in.readString();
        oldTransactionId = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(amount);
        dest.writeString(currency);
        dest.writeString(exchangeRate);
        dest.writeString(reference);
        dest.writeDouble(refundAmount);
        dest.writeString(refundReference);
        dest.writeString(refundTransactionId);
        dest.writeString(settleCurrency);
        dest.writeString(oldTransactionId);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransRefundBean> CREATOR = new Creator<TransRefundBean>() {
        @Override
        public TransRefundBean createFromParcel(Parcel in) {
            return new TransRefundBean(in);
        }

        @Override
        public TransRefundBean[] newArray(int size) {
            return new TransRefundBean[size];
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

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
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

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    public String getRefundTransactionId() {
        return refundTransactionId;
    }

    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public String getSettleCurrency() {
        return settleCurrency;
    }

    public void setSettleCurrency(String settleCurrency) {
        this.settleCurrency = settleCurrency;
    }

    public String getOldTransactionId() {
        return oldTransactionId;
    }

    public void setOldTransactionId(String oldTransactionId) {
        this.oldTransactionId = oldTransactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
