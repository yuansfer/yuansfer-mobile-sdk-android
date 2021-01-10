package com.yuansfer.pay.alipay;

/**
 * @Author Fly
 * @CreateDate 2019/5/23 14:53
 * @Desciption 支付宝请求实体
 */
public class AlipayItem {

    private String orderInfo;

    public AlipayItem() {
    }

    public AlipayItem(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

}
