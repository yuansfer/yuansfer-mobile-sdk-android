package com.yuansfer.paysdk.model;

public class SecureV3Info {

    private double amount;
    private String authorization;
    private String currency;
    private String reference;
    private String flow;
    private String time;
    private String intent;
    private String transactionNo;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    @Override
    public String toString() {
        return "SecureV3Info{" +
                "amount=" + amount +
                ", authorization='" + authorization + '\'' +
                ", currency='" + currency + '\'' +
                ", reference='" + reference + '\'' +
                ", flow='" + flow + '\'' +
                ", time='" + time + '\'' +
                ", intent='" + intent + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                '}';
    }
}
