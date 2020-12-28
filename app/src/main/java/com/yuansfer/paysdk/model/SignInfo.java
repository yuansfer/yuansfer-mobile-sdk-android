package com.yuansfer.paysdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
/**
* @Author Fly
* @CreateDate 2019/5/27 9:31
* @Desciption 登录
*/
public class SignInfo extends ParamInfo implements Parcelable {

    private String merchantNo;
    private String storeNo;

    public SignInfo() {}

    public SignInfo(Parcel in) {
        this.setMerchantNo(in.readString());
        this.setStoreNo(in.readString());
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(String storeNo) {
        this.storeNo = storeNo;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("merchantNo", merchantNo);
        paramMap.put("storeNo", storeNo);
        return paramMap;
    }

    public static final Creator<SignInfo> CREATOR = new Creator<SignInfo>() {
        public SignInfo createFromParcel(Parcel source) {
            return new SignInfo(source);
        }

        public SignInfo[] newArray(int size) {
            return new SignInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMerchantNo());
        dest.writeString(getStoreNo());
    }

}
