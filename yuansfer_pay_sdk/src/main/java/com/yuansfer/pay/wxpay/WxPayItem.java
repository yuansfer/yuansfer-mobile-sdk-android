package com.yuansfer.pay.wxpay;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.yuansfer.pay.payment.PayItem;
import com.yuansfer.pay.payment.PayType;

/**
 * 微信支付请求实体
 */
public class WxPayItem extends PayItem {

    private String appId;
    private String partnerId;
    private String packageValue;
    private String nonceStr;
    private String timestamp;
    private String prepayId;
    private String sign;
    private String signType;

    private WxPayItem(String appId, String partnerId, String packageValue
            , String nonce, String timestamp, String prepayId, String sign, String signType) {
        this.appId = appId;
        this.partnerId = partnerId;
        this.packageValue = packageValue;
        this.nonceStr = nonce;
        this.timestamp = timestamp;
        this.prepayId = prepayId;
        this.sign = sign;
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
    @PayType
    public int getPayType() {
        return PayType.WECHAT_PAY;
    }

    public static class Builder {
        private String appId;
        private String partnerId;
        private String packageValue;
        private String nonceStr;
        private String timestamp;
        private String prepayId;
        private String sign;
        private String signType;

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public Builder setPackageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }

        public Builder setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public Builder setTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setPrepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }

        public Builder setSign(String sign) {
            this.sign = sign;
            return this;
        }

        public Builder setSignType(String signType) {
            this.signType = signType;
            return this;
        }

        public WxPayItem build() {
            return new WxPayItem(appId, partnerId, packageValue
                    , nonceStr, timestamp, prepayId, sign, signType);
        }

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
