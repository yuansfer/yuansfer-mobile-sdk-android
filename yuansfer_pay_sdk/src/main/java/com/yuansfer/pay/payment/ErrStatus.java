package com.yuansfer.pay.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 错误描述
 *
 * errCode类型：
 * 微信->W开头
 * 支付宝->A开头
 * Google Pay->G开头
 */
public class ErrStatus implements Parcelable {

    private String errCode;
    private String errMsg;

    public static ErrStatus getInstance(String errCode, String errMsg) {
        return new ErrStatus(errCode, errMsg);
    }

    private ErrStatus(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    protected ErrStatus(Parcel in) {
        errCode = in.readString();
        errMsg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errCode);
        dest.writeString(errMsg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ErrStatus> CREATOR = new Creator<ErrStatus>() {
        @Override
        public ErrStatus createFromParcel(Parcel in) {
            return new ErrStatus(in);
        }

        @Override
        public ErrStatus[] newArray(int size) {
            return new ErrStatus[size];
        }
    };

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
