package com.yuansfer.paysdk.model;


public class WechatResultInfo {

    private String ret_msg;
    private String ret_code;
    private WechatInfo result;

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

    public WechatInfo getResult() {
        return result;
    }

    public void setResult(WechatInfo result) {
        this.result = result;
    }

}
