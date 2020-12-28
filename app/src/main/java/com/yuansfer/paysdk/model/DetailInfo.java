package com.yuansfer.paysdk.model;

import android.os.Parcel;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * @Author Fly
 * @CreateDate 2019/5/29 15:44
 * @Desciption 订单状态
 */
public class DetailInfo extends SignInfo {

    private String reference;
    private String transactionNo;

    public DetailInfo() {
    }

    public DetailInfo(Parcel in) {
        super(in);
        this.setReference(in.readString());
        this.setTransactionNo(in.readString());
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

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> paramMap = super.toHashMap();
        if (!TextUtils.isEmpty(reference)) {
            paramMap.put("reference", reference);
        }
        if (!TextUtils.isEmpty(transactionNo)) {
            paramMap.put("transactionNo", transactionNo);
        }
        return paramMap;
    }

    public static final Creator<DetailInfo> CREATOR = new Creator<DetailInfo>() {
        public DetailInfo createFromParcel(Parcel source) {
            return new DetailInfo(source);
        }

        public DetailInfo[] newArray(int size) {
            return new DetailInfo[size];
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
    }

}
