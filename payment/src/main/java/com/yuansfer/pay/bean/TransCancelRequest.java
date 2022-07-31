package com.yuansfer.pay.bean;

public class TransCancelRequest extends BaseRequest {

    private String transactionNo;
    private String reference;

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
