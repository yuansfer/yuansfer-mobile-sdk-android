package com.yuansfer.pay.aliwx;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * @Author Fly
 * @CreateDate 2019/5/23 9:13
 * @Desciption 支付类型:支付宝/微信支付
 */
@Retention(RetentionPolicy.SOURCE)
public @interface AliWxType {
    int ALIPAY = 1;
    int WECHAT_PAY = 2;
}
