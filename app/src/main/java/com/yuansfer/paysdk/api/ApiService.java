package com.yuansfer.paysdk.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.yuansfer.paysdk.model.SecurePayInfo;
import com.yuansfer.paysdk.okhttp.IResponseHandler;
import com.yuansfer.paysdk.okhttp.LoggerInterceptor;
import com.yuansfer.paysdk.okhttp.OkHttpUtils;
import com.yuansfer.paysdk.okhttp.UnsafeSSLFactory;
import com.yuansfer.sdk.YSAppPay;
import com.yuansfer.paysdk.model.DetailInfo;
import com.yuansfer.paysdk.model.ParamInfo;
import com.yuansfer.paysdk.model.PrepayInfo;
import com.yuansfer.paysdk.model.RefundInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @Author Fly-Android
 * @CreateDate 2019/5/24 11:43
 * @Desciption Yuansfer api service
 */
public class ApiService {

    private static final int CONN_TIMEOUT = 15;
    private static final int RW_TIMEOUT = 20;

    public interface ApiStatusCode {
        /**
         * 接口参数有误
         */
        int API_PARAM_ERROR = -1;
    }

    static {
        UnsafeSSLFactory.TrustAllManager trustAllManager = UnsafeSSLFactory.getTrustAllManager();
        OkHttpUtils.initClient(new OkHttpClient.Builder()
                .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RW_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RW_TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(UnsafeSSLFactory.createTrustAllSSLFactory(trustAllManager), trustAllManager)
                .hostnameVerifier(UnsafeSSLFactory.createTrustAllHostnameVerifier())
                .addInterceptor(new LoggerInterceptor("Yuansfer-Mobile-Pay-SDK-Android", YSAppPay.sDebug))
                .build());
    }

    /**
     * 生成参数签名
     *
     * @param token     token
     * @param paramInfo 参数实例
     */
    private static HashMap<String, String> generateSignatureMap(@NonNull String token, @NonNull ParamInfo paramInfo) {
        HashMap<String, String> paramMap = paramInfo.toHashMap();
        String[] keyArrays = paramMap.keySet().toArray(new String[]{});
        Arrays.sort(keyArrays);
        StringBuffer psb = new StringBuffer();
        for (String key : keyArrays) {
            String value = paramMap.get(key);
            if (!TextUtils.isEmpty(value)) {
                psb.append(key).append("=")
                        .append(value).append("&");
            }
        }
        psb.append(MD5.encrypt(token));
        paramMap.put("verifySign", MD5.encrypt(psb.toString()));
        Log.e("ApiService verifySign:", paramMap.get("verifySign"));
        return paramMap;
    }

    /**
     * 预下单
     *
     * @param context
     * @param token
     * @param prepayInfo
     * @param responseCallback
     */
    public static void prepay(Context context, String token, PrepayInfo prepayInfo, IResponseHandler responseCallback) {
        OkHttpUtils.get().post(context, ApiUrl.getPrePayUrl()
                , generateSignatureMap(token, prepayInfo), responseCallback);
    }

    /**
     * 查询订单状态
     */
    public static void orderStatus(Context context, String token, DetailInfo orderInfo, IResponseHandler responseHandler) {
        OkHttpUtils.get().post(context, ApiUrl.getOrderStatusUrl(), generateSignatureMap(token, orderInfo), responseHandler);
    }

    /**
     * 退款
     */
    public static void refund(Context context, String token, RefundInfo refundInfo, IResponseHandler responseHandler) {
        OkHttpUtils.get().post(context, ApiUrl.getRefundUrl(), generateSignatureMap(token, refundInfo), responseHandler);
    }

    /**
     * 多币种支付
     *
     * @param context
     * @param token
     * @param secureInfo
     * @param responseCallback
     */
    public static void securePay(Context context, String token, SecurePayInfo secureInfo, IResponseHandler responseCallback) {
        OkHttpUtils.get().post(context, ApiUrl.getSecurePayUrl()
                , generateSignatureMap(token, secureInfo), responseCallback);
    }

}
