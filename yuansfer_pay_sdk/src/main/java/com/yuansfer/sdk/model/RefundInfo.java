package com.yuansfer.sdk.model;

import android.os.Parcel;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * @Author Fly-Android
 * @CreateDate 2019/5/29 15:53
 * @Desciption 退款
 */
public class RefundInfo extends SignInfo {

    private double amount;
    private double rmbAmount;
    private String reference;
    private String transactionNo;
    private String refundReference;

    public RefundInfo() {
    }

    public RefundInfo(Parcel in) {
        super(in);
        this.setReference(in.readString());
        this.setTransactionNo(in.readString());
        this.setAmount(in.readDouble());
        this.setRmbAmount(in.readDouble());
        this.setRefundReference(in.readString());
    }

    public double getAmount() {
        return amount;
    }

    /**
     * amount和rmbAmount二选一
     *
     * @param amount 美元金额
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRmbAmount() {
        return rmbAmount;
    }

    /**
     * amount和rmbAmount二选一
     *
     * @param rmbAmount 人民币金额
     */
    public void setRmbAmount(double rmbAmount) {
        this.rmbAmount = rmbAmount;
    }

    public String getReference() {
        return reference;
    }

    /**
     * reference和transactionNo二选一
     *
     * @param reference 流水号
     */

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    /**
     * reference和transactionNo二选一
     *
     * @param transactionNo 订单号
     */
    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> paramMap = super.toHashMap();
        if (!TextUtils.isEmpty(reference)) {
            paramMap.put("reference", reference);
        }
        if (!TextUtils.isEmpty(transactionNo)) {
            paramMap.put("transactionNo", transactionNo);
        }
        if (amount > 0.0D) {
            paramMap.put("amount", amount + "");
        }
        if (rmbAmount > 0.0D) {
            paramMap.put("rmbAmount", rmbAmount + "");
        }
        if (!TextUtils.isEmpty(refundReference)) {
            paramMap.put("refundReference", refundReference);
        }
        return paramMap;
    }

    public static final Creator<RefundInfo> CREATOR = new Creator<RefundInfo>() {
        public RefundInfo createFromParcel(Parcel source) {
            return new RefundInfo(source);
        }

        public RefundInfo[] newArray(int size) {
            return new RefundInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getReference());
        dest.writeString(getTransactionNo());
        dest.writeDouble(getAmount());
        dest.writeDouble(getRmbAmount());
        dest.writeString(getRefundReference());
    }

}
