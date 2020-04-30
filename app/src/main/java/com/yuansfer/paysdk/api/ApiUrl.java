package com.yuansfer.paysdk.api;


/**
 * @Author Fly-Android
 * @CreateDate 2019/5/27 9:29
 * @Desciption api接口地址
 */
public class ApiUrl {

    private static final String API_SERVER = "https://mapi.yuansfer.com/";
    private static final String API_SERVER_TEST = "https://mapi.yuansfer.yunkeguan.com/";
    private static String API_SERVER_CURRENT = API_SERVER;

    /**
     * 功能接口Path
     */
    public interface ApiPath {
        //预下单
        String PRE_PAY = "micropay/v2/prepay";
        //查询订单状态
        String ORDER_STATUS = "micropay/v2/reference-query";
        //退款
        String REFUND = "micropay/v2/refund";
    }

    /**
     * 设置测试模式环境
     */
    public static void setTestMode() {
        ApiUrl.API_SERVER_CURRENT = ApiUrl.API_SERVER_TEST;
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
