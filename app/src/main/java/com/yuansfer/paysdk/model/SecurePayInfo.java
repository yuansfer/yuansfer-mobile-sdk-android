package com.yuansfer.paysdk.model;

import java.util.HashMap;

public class SecurePayInfo extends SignInfo {

    private double amount;
    private String currency = "USD";
    private String settleCurrency = "USD";
    private String vendor;
    private String ipnUrl;
    private String callbackUrl;
    private String reference;
    private String terminal = "APP";
    private String description;
    private String note;
    private String osType = "ANDROID";
    private int timeout = 120;
    private String goodsInfo;
    private String creditType;
    private int paymentCount;
    private String frequency;

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

    public String getSettleCurrency() {
        return settleCurrency;
    }

    public void setSettleCurrency(String settleCurrency) {
        this.settleCurrency = settleCurrency;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
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

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public int getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap map = super.toHashMap();
        map.put("amount", amount + "");
        map.put("currency", currency);
        map.put("settleCurrency", settleCurrency);
        map.put("vendor", vendor);
        map.put("ipnUrl", ipnUrl);
        map.put("callbackUrl", callbackUrl);
        map.put("reference", reference);
        map.put("terminal", terminal);
        map.put("description", description);
        map.put("note", note);
        map.put("osType", osType);
        map.put("timeout", timeout + "");
        map.put("goodsInfo", goodsInfo);
        map.put("creditType", creditType);
        map.put("paymentCount", paymentCount + "");
        map.put("frequency", frequency);
        return map;
    }

}
