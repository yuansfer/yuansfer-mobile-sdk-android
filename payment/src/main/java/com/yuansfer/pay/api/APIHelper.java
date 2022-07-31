package com.yuansfer.pay.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yuansfer.pay.bean.BaseRequest;
import com.yuansfer.pay.bean.BaseResponse;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.pay.util.MD5;

import java.util.Arrays;
import java.util.Map;

public class APIHelper {

    /**
     * 检查账号是否为空
     *
     * @param request
     */
    public static void checkAPIAccountParams(BaseRequest request) {
        if (TextUtils.isEmpty(request.getToken())) {
            throw new IllegalArgumentException("Token is null");
        }
        if (TextUtils.isEmpty(request.getMerchantNo())) {
            throw new IllegalArgumentException("MerchantNo is null");
        }
        if (TextUtils.isEmpty(request.getStoreNo())) {
            throw new IllegalArgumentException("StoreNo is null");
        }
    }

    /**
     * 检查账号是否为空
     *
     * @param request
     */
    public static void checkAPIAccountParams(Map<String, Object> request) {
        if (TextUtils.isEmpty("token")) {
            throw new IllegalArgumentException("Token is null");
        }
        if (request.get("merchantNo") == null) {
            throw new IllegalArgumentException("MerchantNo is null");
        }
        if (request.get("storeNo") == null) {
            throw new IllegalArgumentException("storeNo is null");
        }
    }

    /**
     * 生成校验sign
     *
     * @param reqMap 请求Map
     * @param token  令牌
     * @return 签名串
     */
    public static String getVerifySign(Map<String, Object> reqMap, String token) {
        String[] keyArrays = reqMap.keySet().toArray(new String[]{});
        Arrays.sort(keyArrays);
        StringBuffer psb = new StringBuffer();
        for (String key : keyArrays) {
            Object value = reqMap.get(key);
            if (isNotEmpty(value)) {
                psb.append(key).append("=")
                        .append(value).append("&");
            }
        }
        psb.append(MD5.encrypt(token));
        return MD5.encrypt(psb.toString());
    }

    /**
     * 将接口数据转换为Bean，同时保留原数据
     *
     * @param gson    Gson
     * @param rawData 原始数据
     * @param clazz   目标类型
     * @return Response Bean对象，失败为null
     */
    public static <T extends BaseResponse> T convertResponseKeepRaw(Gson gson, String rawData, Class<T> clazz) {
        T response = null;
        try {
            response = gson.fromJson(rawData, clazz);
            response.setRawData(rawData);
        } catch (JsonSyntaxException e) {
            LogUtils.d("Convert response data fail:" + e.getMessage());
        }
        return response;
    }

    /**
     * 添加signature到Map
     *
     * @param reqMap     请求Map
     * @param verifySign 签名串
     */
    public static void addVerifySign2Map(Map<String, Object> reqMap, String verifySign) {
        reqMap.put("verifySign", verifySign);
    }

    /**
     * 移除Map里的token
     *
     * @param reqMap 请求Map
     */
    public static void removeTokenFromMap(Map<String, Object> reqMap) {
        reqMap.remove("token");
    }

    /**
     * 从Map获取token
     *
     * @param reqMap 请求Map
     * @return token
     */
    public static String getTokenFromMap(Map<String, Object> reqMap) {
        return reqMap.get("token").toString();
    }

    private static boolean isNotEmpty(Object object) {
        return object != null && object.toString().length() > 0;
    }
}
