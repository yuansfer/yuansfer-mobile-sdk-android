package com.yuansfer.pay.alipay;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.IPayStrategy;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.util.LogUtils;

import java.util.Map;

/**
 * @Author Fly
 * @CreateDate 2019/5/23 12:01
 * @Desciption 支付宝支付
 */
public class AlipayStrategy implements IPayStrategy<AlipayItem> {

    private static final int PAY_RESULT = 1379;

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
                        PayResultMgr.getInstance().dispatchPaySuccess(PayType.ALIPAY);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // 6001为主动取消支付
                        if (TextUtils.equals(resultStatus, "6001")) {
                            PayResultMgr.getInstance().dispatchPayCancel(PayType.ALIPAY);
                        } else {
                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            PayResultMgr.getInstance().dispatchPayFail(PayType.ALIPAY
                                    , ErrStatus.getInstance("A" + resultStatus, payResult.getMemo()));
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void startPay(final Activity activity, final AlipayItem alipayItem) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(activity);
                // 调用支付接口，获取支付结果
                Map<String, String> result = payTask.payV2(alipayItem.getOrderInfo(), true);
                Message msg = new Message();
                msg.what = PAY_RESULT;
                msg.obj = result;
                sHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
        LogUtils.d("Alipay started");
    }

}
