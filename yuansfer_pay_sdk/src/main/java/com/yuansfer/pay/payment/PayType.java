package com.yuansfer.pay.payment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @Author Fly
 * @CreateDate 2019/5/23 9:13
 * @Desciption 支付类型:支付宝/微信支付/Google Pay/Drop-in
 */

@Retention(RetentionPolicy.SOURCE)
public @interface PayType {
    int ALIPAY = 1;
    int WECHAT_PAY = 2;
    int GOOGLE_PAY = 3;
    int DROP_IN = 4;
}
