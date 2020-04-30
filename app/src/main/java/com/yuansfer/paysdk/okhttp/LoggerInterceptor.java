package com.yuansfer.paysdk.okhttp;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 日志拦截
 */
public class LoggerInterceptor implements Interceptor {

    public static final String TAG = "OkHttpUtils";
    private boolean logPrint;
    private String tag;

    public LoggerInterceptor(String tag, boolean isLogPrint) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.logPrint = isLogPrint;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag) {
        this(tag, true);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            if (logPrint) {
                Log.d(tag, "request url : " + clone.request().url());
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        if (isText(mediaType)) {
                            String resp = body.string();
                            Log.d(tag, "response body's content:\n" + resp);
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else {
                            Log.d(tag, "response body's content:\n" + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null && logPrint) {
                    if (isText(mediaType)) {
                        Log.d(tag, "request body's content : \n" + bodyToString(request));
                    } else {
                        Log.d(tag, "request body's content : \n" + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            return mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("x-www-form-urlencoded");
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
