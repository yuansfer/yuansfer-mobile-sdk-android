package com.yuansfer.pay.aliwx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yuansfer.pay.util.ErrStatus;
import com.yuansfer.pay.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Author Fly
 * @CreateDate 2019/5/23 9:36
 * @Description 微信支付宝支付管理器
 */
public final class AliWxPayMgr {

    private static final int PAY_RESULT = 1379;
    private static final Object sLocks = new Object();
    private static AliWxPayMgr sInstance;
    private List<IAliWxPayCallback> mCallbacks;
    private IWXAPI sWxAPI;

    @SuppressLint("HandlerLeak")
    private static Handler sHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY_RESULT: {
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    LogUtils.d("alipay result=" + payResult);
                    String resultStatus = payResult.getResultStatus();
                    // 状态码详见：https://global.alipay.com/doc/global/mobile_securitypay_pay_cn
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档:
                    //https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.IXE2Zj&treeId=59&articleId=103671&docType=1
                    if (TextUtils.equals(resultStatus, "9000")) {
                        AliWxPayMgr.getInstance().dispatchPaySuccess(AliWxType.ALIPAY);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // 6001为主动取消支付
                        if (TextUtils.equals(resultStatus, "6001")) {
                            AliWxPayMgr.getInstance().dispatchPayCancel(AliWxType.ALIPAY);
                        } else {
                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            AliWxPayMgr.getInstance().dispatchPayFail(AliWxType.ALIPAY
                                    , ErrStatus.getInstance(ErrStatus.ALIPAY_COMMON_ERROR, payResult.getMemo()));
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 支付结果回调
     */
    public interface IAliWxPayCallback {

        /**
         * 支付成功
         *
         * @param payType 支付类型,1: 支付宝, 2: 微信
         */
        void onPaySuccess(@AliWxType int payType);

        /**
         * 支付失败
         *
         * @param payType   支付类型,1: 支付宝, 2: 微信
         * @param errStatus 错误状态
         */
        void onPayFail(@AliWxType int payType, ErrStatus errStatus);

        /**
         * 支付取消
         *
         * @param payType 支付类型,1: 支付宝, 2: 微信
         */
        void onPayCancel(@AliWxType int payType);
    }

    private AliWxPayMgr() {
        mCallbacks = new ArrayList<>();
    }

    /**
     * 获取单例
     *
     * @return PayResultMgr
     */
    public static AliWxPayMgr getInstance() {
        if (sInstance == null) {
            synchronized (sLocks) {
                if (sInstance == null) {
                    sInstance = new AliWxPayMgr();
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
    public void addAliWxPayCallback(IAliWxPayCallback callback) {
        synchronized (sLocks) {
            mCallbacks.add(callback);
        }
    }

    /**
     * 移除支付结果回调
     *
     * @param callback 支付回调
     */
    public void removeAliWxPayCallback(IAliWxPayCallback callback) {
        synchronized (sLocks) {
            mCallbacks.remove(callback);
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
            if (mCallbacks.size() > 0) {
                callbacks = mCallbacks.toArray();
            }
        }
        return callbacks;
    }

    /**
     * 请求支付宝支付
     * @param activity
     * @param orderInfo
     */
    public void requestAlipay(final Activity activity, final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(activity);
                // 调用支付接口，获取支付结果
                Map<String, String> result = payTask.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = PAY_RESULT;
                msg.obj = result;
                sHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
        LogUtils.d("Alipay start");
    }

    /**
     * 请求微信支付
     *
     * @param request  WxPayItem实例
     */
    public void requestWXPay(WxPayItem request) {
        requireNonNull(sWxAPI, "Wechat app is not register");
        if (!isSupportWxPay()) {
            dispatchPayFail(AliWxType.WECHAT_PAY, ErrStatus.getInstance(ErrStatus.WECHAT_UNINSTALL_ERROR
                    , "Wechat APP is not installed or version too low"));
            return;
        }
        sWxAPI.sendReq(request.getPayReq());
        LogUtils.d("Wechat Pay start");
    }

    /**
     * 注册app到微信
     *
     * @param context 上下文,applicationContext
     * @param appId   应用id
     */
    public void registerApp(Context context, String appId) {
        sWxAPI = WXAPIFactory.createWXAPI(context, appId);
        sWxAPI.registerApp(appId);
    }

    /**
     * @param payType 支付类型
     *                通知支付成功
     */
    void dispatchPaySuccess(@AliWxType int payType) {
        Object[] callbacks = collectPayResultCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                ((IAliWxPayCallback) callback).onPaySuccess(payType);
            }
        }
    }

    /**
     * 通知支付失败
     *
     * @param payType   支付类型
     * @param errStatus 错误状态
     */
    void dispatchPayFail(@AliWxType int payType, ErrStatus errStatus) {
        Object[] callbacks = collectPayResultCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                ((IAliWxPayCallback) callback).onPayFail(payType, errStatus);
            }
        }
    }

    /**
     * 通知支付已取消
     *
     * @param payType 支付类型
     */
    void dispatchPayCancel(@AliWxType int payType) {
        Object[] callbacks = collectPayResultCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                ((IAliWxPayCallback) callback).onPayCancel(payType);
            }
        }
    }

    /**
     * 处理回调Intent
     *
     * @param intent       意图
     * @param eventHandler 事件handler
     */
    void handleIntent(Intent intent, IWXAPIEventHandler eventHandler) {
        requireNonNull(sWxAPI, "Wechat app is not register");
        sWxAPI.handleIntent(intent, eventHandler);
    }

    /**
     * 是否支付微信支付
     *
     * @return true支持
     */
    private boolean isSupportWxPay() {
        return sWxAPI.isWXAppInstalled() && sWxAPI.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }

    /**
     * 判空
     *
     * @param obj     对象
     * @param message 空提示
     * @param <T>     原对象
     * @return
     */
    private static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new AndroidRuntimeException(message);
        }
        return obj;
    }
}
