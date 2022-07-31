package com.yuansfer.pay.api;

import com.yuansfer.pay.util.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author fly
 * @desc 日志拦截输出API数据
 */
public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            LogUtils.d("\n");
            LogUtils.d("----------Start----------------");
            LogUtils.d( String.format("| %s", request));
            LogUtils.d( String.format("| Response:%s", content));
            LogUtils.d( "----------End:" + duration + "毫秒----------");
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = chain.request();
        // 必须同步返回，拦截器内无法执行异步操作
        return chain.proceed(request);
    }

}
