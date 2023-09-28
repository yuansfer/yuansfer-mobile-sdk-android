package com.yuansfer.pay.api;

public class APIPath {

    /**
     * 生产环境
     */
    public static final String RELEASE_URL = "https://mapi.yuansfer.com";
    /**
     * 沙箱环境
     */
    public static final String SANDBOX_URL = "https://mapi.yuansfer.yunkeguan.com";
    /**
     * 创建一个交易订单
     */
    public static final String TRANS_ADD = "/app-instore/v3/add";

    /**
     * 交易处理，同步接口
     */
    public static final String TRANS_PREPAY = "/app-instore/v3/prepay";

    /**
     * 交易状态查询
     */
    public static final String TRANS_STATUS = "/app-data-search/v3/tran-query";

    /**
     * 交易详情
     */
    public static final String TRANS_DETAIL = "/app-instore/v3/detail";

    /**
     * 交易退款
     */
    public static final String TRANS_REFUND = "/app-data-search/v3/refund";

    /**
     * 交易撤销
     */
    public static final String TRANS_CANCEL = "/app-data-search/v3/cancel";

    /**
     * 更新信用卡小费
     */
    public static final String TRANS_TIP_UPDATE = "/app-instore/v3/tip-adjustment";

    /**
     * 生成混合二维码
     */
    public static final String MIXED_CODE_GENERATE = "/appUnicode/generateMixedCode";

    /**
     * 混合码查询
     */
    public static final String MIXED_CODE_QUERY = "/appUnicode/mixedQuery";

    /**
     * 混合码取消
     */
    public static final String MIXED_CODE_CANCEL = "/appUnicode/mixedCancel";
}
