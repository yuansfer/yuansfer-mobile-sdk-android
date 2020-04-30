package com.yuansfer.sdk.pay;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @Author Fly-Android
 * @CreateDate 2019/5/23 9:13
 * @Desciption 支付类型:支付宝/微信支付
 */

@IntDef({PayType.ALIPAY, PayType.WXPAY})
@Retention(RetentionPolicy.SOURCE)
public @interface PayType {
    int ALIPAY = 1;
    int WXPAY = 2;
}
