package com.yuansfer.pay.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseRequest implements Parcelable {

    /**
     * 默认货币单位为美元
     */
    public static final String DEFAULT_CURRENCY = "USD";
    /**
     * 商家No.
     */
    private String merchantNo;
    /**
     * 商店No.
     */
    private String storeNo;
    /**
     * api令牌
     */
    private String token;

    public BaseRequest(){}

    protected BaseRequest(Parcel in) {
        merchantNo = in.readString();
        storeNo = in.readString();
        token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(merchantNo);
        dest.writeString(storeNo);
        dest.writeString(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseRequest> CREATOR = new Creator<BaseRequest>() {
        @Override
        public BaseRequest createFromParcel(Parcel in) {
            return new BaseRequest(in);
        }

        @Override
        public BaseRequest[] newArray(int size) {
            return new BaseRequest[size];
        }
    };

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
