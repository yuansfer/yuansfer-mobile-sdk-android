package com.yuansfer.pay.api;

import com.yuansfer.pay.util.LogUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author fly
 * @desc 超时重试配置
 */
public class RetryInterceptor implements Interceptor {

    private int mMaxRetryTimes;

    public RetryInterceptor(int retryTimes) {
        this.mMaxRetryTimes = retryTimes;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        int currentRetryTimes = 0;
        while (true) {
            try {
                return chain.proceed(chain.request());
            } catch (SocketTimeoutException e) {
                if (currentRetryTimes >= mMaxRetryTimes) {
                    throw e;
                }
                currentRetryTimes++;
                LogUtils.d("Timeout retry [" + currentRetryTimes + "] times");
            }
        }
    }

}

