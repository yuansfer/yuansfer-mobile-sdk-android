package com.yuansfer.paysdk.util;

import android.content.Context;

import com.yuansfer.pay.braintree.BrainTreePaymentMethod;
import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.model.PayProcessInfo;
import com.yuansfer.paysdk.model.SecurePayInfo;
import com.yuansfer.paysdk.okhttp.IResponseHandler;

public class YSTestApi {

    public static void callTestPrepay(Context context, IResponseHandler responseCallback) {
        //测试环境账号和token
        SecurePayInfo info = new SecurePayInfo();
        info.setMerchantNo("202333");
        info.setStoreNo("301854");
        info.setAmount(0.01);
        info.setCreditType("yip");
        info.setVendor("paypal");
        info.setReference(System.currentTimeMillis() + "");
        info.setIpnUrl("https://yuansferdev.com/callback");
        info.setDescription("test+description");
        info.setNote("note");
        ApiService.securePay(context, "17cfc0170ef1c017b4a929d233d6e65e", info, responseCallback);
    }

    public static void callTestProcess(Context context, @BrainTreePaymentMethod String paymentMethod
            , String transactionNo, String nonce, String deviceData, IResponseHandler responseCallback) {
        //测试环境账号和token
        PayProcessInfo processInfo = new PayProcessInfo();
        processInfo.setMerchantNo("202333");
        processInfo.setStoreNo("301854");
        processInfo.setPaymentMethod(paymentMethod);
        processInfo.setPaymentMethodNonce(nonce);
        processInfo.setTransactionNo(transactionNo);
        processInfo.setDeviceData(deviceData);
        ApiService.braintreePay(context, "17cfc0170ef1c017b4a929d233d6e65e", processInfo, responseCallback);
    }

}
