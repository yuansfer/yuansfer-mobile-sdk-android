package com.yuansfer.pay.bean;

public class TransPrepayResponse extends BaseResponse {

    private TransInitBean result;

    public TransInitBean getResult() {
        return result;
    }

    public void setResult(TransInitBean result) {
        this.result = result;
    }

}
