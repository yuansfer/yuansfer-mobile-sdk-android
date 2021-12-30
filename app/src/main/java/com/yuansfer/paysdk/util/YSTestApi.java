package com.yuansfer.paysdk.util;

import android.content.Context;

import com.yuansfer.pay.aliwx.AliWxType;
import com.yuansfer.paysdk.api.ApiService;
import com.yuansfer.paysdk.model.AutoDebitInfo;
import com.yuansfer.paysdk.model.DetailInfo;
import com.yuansfer.paysdk.model.PayProcessInfo;
import com.yuansfer.paysdk.model.PrepayInfo;
import com.yuansfer.paysdk.model.RefundInfo;
import com.yuansfer.paysdk.model.SecurePayInfo;
import com.yuansfer.paysdk.okhttp.IResponseHandler;

/**
 * 测试接口，正式使用时应由商家app服务端发起请求
 */
public class YSTestApi {

    public static final String TEST_TOKEN_ALIWX = "xxx";
    public static final String PRODUCTION_TOKEN_ALIWX = "xxx";
    public static final String TEST_TOKEN_BT = "xxx";
    private static final String sAWMerchantNo = "200043";
    private static final String sAWStoreNo = "300014";
    private static final String sBTMerchantNo = "202333";
    private static final String sBTStoreNo = "301854";
    public static String sToken = TEST_TOKEN_ALIWX;

    public static void requestAlipay(Context context, String reference, IResponseHandler callback) {
        PrepayInfo info = new PrepayInfo();
        info.setMerchantNo(sAWMerchantNo);
        info.setStoreNo(sAWStoreNo);
        info.setPayType(AliWxType.ALIPAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://alipay.yuansfer.yunkeguan.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(reference);
        ApiService.prepay(context, sToken, info, callback);
    }

    public static void requestWechatPay(Context context, String reference, IResponseHandler callback) {
        PrepayInfo info = new PrepayInfo();
        info.setMerchantNo(sAWMerchantNo);
        info.setStoreNo(sAWStoreNo);
        info.setPayType(AliWxType.WECHAT_PAY);
        info.setAmount(0.01);
        info.setIpnUrl("https://app.yuansfer.com/wx");
        info.setDescription("description");
        info.setNote("note");
        info.setReference(reference);
        ApiService.prepay(context, sToken, info, callback);
    }

    public static void requestTransDetail(Context context, String reference, IResponseHandler callback) {
        DetailInfo info = new DetailInfo();
        info.setMerchantNo(sAWMerchantNo);
        info.setStoreNo(sAWStoreNo);
        info.setReference(reference);
        ApiService.orderStatus(context, sToken, info, callback);
    }

    public static void requestRefund(Context context, String reference, IResponseHandler callback) {
        RefundInfo info = new RefundInfo();
        info.setMerchantNo(sAWMerchantNo);
        info.setStoreNo(sAWStoreNo);
        info.setAmount(0.01);
        info.setReference(reference);
        ApiService.refund(context, sToken, info, callback);
    }

    public static void callAutoDebit(Context context, String vendor, String reference, IResponseHandler callback) {
        AutoDebitInfo info = new AutoDebitInfo();
        info.setMerchantNo(sAWMerchantNo);
        info.setStoreNo(sAWStoreNo);
        info.setAutoIpnUrl("https://app.yuansfer.com/ad");
        info.setAutoRedirectUrl("https://app.yuansfer.com/ad");
        info.setAutoReference(reference);
        info.setNote("note");
        info.setOsType("ANDROID");
        info.setOsVersion("10");
        info.setTerminal("APP");
        info.setVendor(vendor);
        ApiService.autoDebit(context, sToken, info, callback);
    }

    public static void requestBTPrepay(Context context, IResponseHandler responseCallback) {
        SecurePayInfo info = new SecurePayInfo();
        info.setMerchantNo(sBTMerchantNo);
        info.setStoreNo(sBTStoreNo);
        info.setAmount(0.01);
        info.setCreditType("yip");
        info.setVendor("paypal");
        info.setReference(System.currentTimeMillis() + "");
        info.setIpnUrl("https://yuansferdev.com/callback");
        info.setDescription("test+description");
        info.setNote("note");
        ApiService.securePay(context, TEST_TOKEN_BT, info, responseCallback);
    }

    public static void requestBTProcess(Context context, @BTMethod String paymentMethod
            , String transactionNo, String nonce, String deviceData, IResponseHandler responseCallback) {
        PayProcessInfo processInfo = new PayProcessInfo();
        processInfo.setMerchantNo(sBTMerchantNo);
        processInfo.setStoreNo(sBTStoreNo);
        processInfo.setPaymentMethod(paymentMethod);
        processInfo.setPaymentMethodNonce(nonce);
        processInfo.setTransactionNo(transactionNo);
        processInfo.setDeviceData(deviceData);
        ApiService.braintreePay(context, TEST_TOKEN_BT, processInfo, responseCallback);
    }

}
