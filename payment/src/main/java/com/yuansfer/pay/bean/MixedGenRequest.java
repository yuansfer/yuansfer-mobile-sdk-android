package com.yuansfer.pay.bean;

public class MixedGenRequest extends BaseRequest {

    private String currency = DEFAULT_CURRENCY;
    private String settleCurrency = DEFAULT_CURRENCY;
    //注意timeout为int时gson转map会变为double
    private String timeout = "120";
    private double saleAmount;
    private double tax;
    private String reference;
    private String ipnUrl;
    private boolean needTip;
    private boolean needQrcode;

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

    public double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }

    public boolean isNeedTip() {
        return needTip;
    }

    public void setNeedTip(boolean needTip) {
        this.needTip = needTip;
    }

    public boolean isNeedQrcode() {
        return needQrcode;
    }

    public void setNeedQrcode(boolean needQrcode) {
        this.needQrcode = needQrcode;
    }

    public int getTimeout() {
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setTimeout(int timeout) {
        this.timeout = String.valueOf(timeout);
    }
}
