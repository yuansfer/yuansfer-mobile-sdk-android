package com.yuansfer.pay.api;

/**
 * @author fly
 * @desc API接口响应回调
 */
public interface OnResponseListener<T> {
    /**
     * 请求成功
     *
     * @param t 响应实体
     */
    void onSuccess(T t);

    /**
     * 请求失败
     *
     * @param e 异常
     */
    void onFail(Exception e);
}
