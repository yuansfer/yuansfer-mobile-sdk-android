package com.yuansfer.pay.cashapp;

public class OneTimeItem {

    private String redirectUri;
    private String scopeId;
    private String currency;
    private double amount;

    public OneTimeItem(String redirectUri, String scopeId, String currency, double amount) {
        this.redirectUri = redirectUri;
        this.scopeId = scopeId;
        this.currency = currency;
        this.amount = amount;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
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
}
