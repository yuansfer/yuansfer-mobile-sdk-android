package com.yuansfer.sdk.pay;


/**
 * @Author Fly-Android
 * @CreateDate 2019/5/23 14:20
 * @Desciption 支付实体
 */
public abstract class PayItem {

     /**
      * 支付类别
      * @return see @PayType
      */
     @PayType
     public abstract int getPayType();

}
