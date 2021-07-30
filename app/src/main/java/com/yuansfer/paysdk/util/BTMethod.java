package com.yuansfer.paysdk.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface BTMethod {
    String ANDROID_PAY_CARD = "android_pay_card";
    String PAYPAL_ACCOUNT = "paypal_account";
    String VENMO_ACCOUNT = "venmo_account";
    String CREDIT_CARD = "credit_card";
}
