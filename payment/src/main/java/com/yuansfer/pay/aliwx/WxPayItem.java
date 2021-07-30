package com.yuansfer.pay.aliwx;

import com.tencent.mm.opensdk.modelpay.PayReq;

/**
 * 微信支付请求实体
 */
public class WxPayItem {

    private String appId;
    private String partnerId;
    private String packageValue;
    private String nonceStr;
    private String timestamp;
    private String prepayId;
    private String sign;
    private String signType;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public PayReq getPayReq() {
        PayReq payReq = new PayReq();
        payReq.appId = this.appId;
        payReq.partnerId = this.partnerId;
        payReq.packageValue = this.packageValue;
        payReq.nonceStr = this.nonceStr;
        payReq.timeStamp = this.timestamp;
        payReq.prepayId = this.prepayId;
        payReq.sign = this.sign;
        payReq.signType = this.signType;
        return payReq;
    }

    @Override
    public String toString() {
        return "WxPayItem{" +
                "appId='" + appId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", packageValue='" + packageValue + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", prepayId='" + prepayId + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
