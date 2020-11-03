package com.yuansfer.paysdk.model;

public class SecureResultInfo {

    private String ret_msg;
    private String ret_code;
    private SecureInfo result;

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public SecureInfo getResult() {
        return result;
    }

    public void setResult(SecureInfo result) {
        this.result = result;
    }
}
