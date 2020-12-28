package com.yuansfer.pay.alipay;

import com.yuansfer.pay.payment.PayItem;
import com.yuansfer.pay.payment.PayType;

/**
 * @Author Fly
 * @CreateDate 2019/5/23 14:53
 * @Desciption 支付宝请求实体
 */
public class AlipayItem extends PayItem {

    private String orderInfo;

    public AlipayItem() {
    }

    public AlipayItem(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    @Override
    @PayType
    public int getPayType() {
        return PayType.ALIPAY;
    }

}
