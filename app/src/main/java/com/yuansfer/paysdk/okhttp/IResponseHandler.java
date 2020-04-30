package com.yuansfer.paysdk.okhttp;

public interface IResponseHandler {

    void onFailure(int statusCode, String errorMsg);

    void onProgress(long var1, long var3);
}
