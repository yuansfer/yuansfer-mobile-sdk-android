package com.yuansfer.pay.bean;

public class TransAddRequest extends BaseRequest {

    private double amount;
    private String currency = BaseRequest.DEFAULT_CURRENCY;
    private String settleCurrency = BaseRequest.DEFAULT_CURRENCY;
    private String reference;

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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
