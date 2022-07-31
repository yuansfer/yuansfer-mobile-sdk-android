package com.yuansfer.pay.bean;

public class TransPrepayRequest extends BaseRequest {

    private String transactionNo;
    private String reference;
    private String paymentBarcode;

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

    public String getPaymentBarcode() {
        return paymentBarcode;
    }

    public void setPaymentBarcode(String paymentBarcode) {
        this.paymentBarcode = paymentBarcode;
    }

}
