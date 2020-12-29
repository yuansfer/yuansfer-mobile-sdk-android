package com.yuansfer.pay.payment;

import android.app.Activity;

/**
 * @Author Fly
 * @CreateDate 2019/5/23 14:21
 * @Desciption 支付Strategy
 */
public interface IPayStrategy<T extends PayItem> {

    /**
     * 发起支付
     *
     * @param activity activity对象
     * @param t        支付实体
     */
    void startPay(Activity activity, T t);
}
