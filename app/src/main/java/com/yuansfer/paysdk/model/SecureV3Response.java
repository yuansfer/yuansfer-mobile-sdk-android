package com.yuansfer.paysdk.model;

import com.yuansfer.pay.bean.BaseResponse;

public class SecureV3Response extends BaseResponse {

    private SecureV3Info result;

    public SecureV3Info getResult() {
        return result;
    }

    public void setResult(SecureV3Info result) {
        this.result = result;
    }
}
