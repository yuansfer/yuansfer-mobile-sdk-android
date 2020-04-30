package com.yuansfer.sdk.pay;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author Fly-Android
 * @CreateDate 2019/5/23 9:36
 * @Description 支付结果管理器
 */
public final class PayResultMgr {

    private static final Object sLocks = new Object();
    private static PayResultMgr sInstance;
    private List<IPayResultCallback> mPayResultCallbacks;

    /**
     * 支付结果回调
     */
    public interface IPayResultCallback {

        /**
         * 支付成功
         *
         * @param payType 支付类型
         */
        void onPaySuccess(@PayType int payType);

        /**
         * 支付失败
         *
         * @param payType 支付类型
         * @param msg     失败原因
         */
        void onPayFail(@PayType int payType, String msg);

        /**
         * 支付取消
         *
         * @param payType 支付类型
         */
        void onPayCancel(@PayType int payType);
    }

    private PayResultMgr() {
        mPayResultCallbacks = new ArrayList<>();
    }

    /**
     * 获取单例
     *
     * @return PayResultMgr
     */
    public static PayResultMgr getInstance() {
        if (sInstance == null) {
            synchronized (sLocks) {
                if (sInstance == null) {
                    sInstance = new PayResultMgr();
                }
            }
        }
        return sInstance;
    }

    /**
     * 添加支付结果回调
     *
     * @param callback 支付回调
     */
    public void addPayResultCallback(IPayResultCallback callback) {
        synchronized (sLocks) {
            mPayResultCallbacks.add(callback);
        }
    }

    /**
     * 移除支付结果回调
     *
     * @param callback 支付回调
     */
    public void removeResultCallback(IPayResultCallback callback) {
        synchronized (sLocks) {
            mPayResultCallbacks.remove(callback);
        }
    }

    /**
     * @param payType 支付类型
     *                通知支付成功
     */
    public void dispatchPaySuccess(@PayType int payType) {
        Object[] callbacks = collectPayResultCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                ((IPayResultCallback) callback).onPaySuccess(payType);
            }
        }
    }

    /**
     * 通知支付失败
     *
     * @param payType 支付类型
     * @param msg     失败原因
     */
    public void dispatchPayFail(@PayType int payType, String msg) {
        Object[] callbacks = collectPayResultCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                ((IPayResultCallback) callback).onPayFail(payType, msg);
            }
        }
    }

    /**
     * 通知支付已取消
     *
     * @param payType 支付类型
     */
    public void dispatchPayCancel(@PayType int payType) {
        Object[] callbacks = collectPayResultCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                ((IPayResultCallback) callback).onPayCancel(payType);
            }
        }
    }

    /**
     * 安全获取已注册回调集合
     *
     * @return Object数组
     */
    private Object[] collectPayResultCallbacks() {
        Object[] callbacks = null;
        synchronized (sLocks) {
            if (mPayResultCallbacks.size() > 0) {
                callbacks = mPayResultCallbacks.toArray();
            }
        }
        return callbacks;
    }

}
