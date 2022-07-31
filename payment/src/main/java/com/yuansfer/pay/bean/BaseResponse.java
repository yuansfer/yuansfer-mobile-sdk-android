package com.yuansfer.pay.bean;

public class BaseResponse {

    /**
     * 请求成功状态码
     */
    public static final String API_SUCCESS_CODE = "000100";
    /**
     * 状态码
     */
    private String ret_code;
    /**
     * 状态描述
     */
    private String ret_msg;

    /**
     * 接口原始数据
     */
    private String rawData;

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    /**
     * api接口是否成功
     *
     * @return true/false
     */
    public boolean isSuccess() {
        return API_SUCCESS_CODE.equals(ret_code);
    }
}
