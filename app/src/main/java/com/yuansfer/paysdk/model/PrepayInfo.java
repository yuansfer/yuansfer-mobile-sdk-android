package com.yuansfer.paysdk.model;

import android.os.Parcel;
import android.text.TextUtils;

import com.yuansfer.sdk.pay.PayType;

import java.util.HashMap;

/**
 * @Author Fly-Android
 * @CreateDate 2019/5/27 9:30
 * @Desciption 预下单请求
 */
public class PrepayInfo extends SignInfo {

    private int payType;
    private double amount;
    private String ipnUrl;
    private String reference;
    private String description;
    private String note;
    private String appid;
    private String terminal = "APP";
    private String currency = "USD";

    public PrepayInfo() {
    }

    public PrepayInfo(Parcel in) {
        super(in);
        setPayType(in.readInt());
        setAmount(in.readDouble());
        setIpnUrl(in.readString());
        setReference(in.readString());
        setDescription(in.readString());
        setNote(in.readString());
        setAppid(in.readString());
        setTerminal(in.readString());
        setCurrency(in.readString());
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> paramMap = super.toHashMap();
        paramMap.put("vendor", payType == PayType.WXPAY
                ? "wechatpay" : "alipay");
        paramMap.put("amount", amount + "");
        paramMap.put("ipnUrl", ipnUrl);
        paramMap.put("reference", reference);
        paramMap.put("description", description);
        paramMap.put("note", note);
        paramMap.put("terminal", terminal);
        paramMap.put("currency", currency);
        if (payType == PayType.WXPAY && !TextUtils.isEmpty(appid)) {
            paramMap.put("appid", appid);
        }
        return paramMap;
    }

    public static final Creator<PrepayInfo> CREATOR = new Creator<PrepayInfo>() {
        public PrepayInfo createFromParcel(Parcel source) {
            return new PrepayInfo(source);
        }

        public PrepayInfo[] newArray(int size) {
            return new PrepayInfo[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(getPayType());
        dest.writeDouble(getAmount());
        dest.writeString(getIpnUrl());
        dest.writeString(getReference());
        dest.writeString(getDescription());
        dest.writeString(getNote());
        dest.writeString(getTerminal());
        dest.writeString(getCurrency());
    }

    @Override
    public String toString() {
        return "PrepayInfo{" +
                "payType=" + payType +
                ", amount=" + amount +
                ", ipnUrl='" + ipnUrl + '\'' +
                ", reference='" + reference + '\'' +
                ", description='" + description + '\'' +
                ", note='" + note + '\'' +
                ", appid='" + appid + '\'' +
                ", terminal='" + terminal + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
