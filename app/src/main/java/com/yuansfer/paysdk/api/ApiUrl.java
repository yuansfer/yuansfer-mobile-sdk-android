package com.yuansfer.paysdk.api;


/**
 * @Author Fly
 * @CreateDate 2019/5/27 9:29
 * @Desciption api接口地址
 */
public class ApiUrl {

    private static final String API_SERVER = "https://mapi.yuansfer.com/";
    private static final String API_SERVER_TEST = "https://mapi.yuansfer.yunkeguan.com/";
    private static String API_SERVER_CURRENT = API_SERVER;

    public interface ApiPath {
        //预下单
        String PRE_PAY = "micropay/v2/prepay";
        //查询订单状态
        String ORDER_STATUS = "micropay/v2/reference-query";
        //退款
        String REFUND = "micropay/v2/refund";
        //线上支付宝多币种接口
        String ONLINE_SECURE_PAY = "online/v3/secure-pay";
        //braintree支付
        String PAY_PROCESS = "creditpay/v3/process";
    }

    /**
     * 设置环境
     * @param release true生产false测试
     */
    public static void setEnvMode(boolean release) {
        ApiUrl.API_SERVER_CURRENT = release ? API_SERVER : API_SERVER_TEST;
    }

    /**
     * 预下单接口
     *
     * @return
     */
    static String getPrePayUrl() {
        return API_SERVER_CURRENT + ApiPath.PRE_PAY;
    }

    /**
     * 线上支付宝多币种接口
     * @return
     */
    static String getSecurePayUrl() {
        return API_SERVER_CURRENT + ApiPath.ONLINE_SECURE_PAY;
    }

    /**
     * 线上支付宝多币种接口
     * @return
     */
    static String getPayProcessUrl() {
        return API_SERVER_CURRENT + ApiPath.PAY_PROCESS;
    }

    /**
     * 订单状态接口
     *
     * @return
     */
    static String getOrderStatusUrl() {
        return API_SERVER_CURRENT + ApiPath.ORDER_STATUS;
    }

    /**
     * 退款接口
     *
     * @return
     */
    static String getRefundUrl() {
        return API_SERVER_CURRENT + ApiPath.REFUND;
    }

}
