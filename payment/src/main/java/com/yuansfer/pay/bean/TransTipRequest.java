package com.yuansfer.pay.bean;

public class TransTipRequest extends BaseRequest{

    private String transactionNo;
    private double tip;
    private String currency = BaseRequest.DEFAULT_CURRENCY;
    private String settleCurrency = BaseRequest.DEFAULT_CURRENCY;

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
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
}
