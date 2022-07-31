package com.yuansfer.pay.api;


import com.yuansfer.pay.bean.BaseRequest;
import com.yuansfer.pay.bean.MixedCancelResponse;
import com.yuansfer.pay.bean.MixedGenRequest;
import com.yuansfer.pay.bean.MixedGenResponse;
import com.yuansfer.pay.bean.MixedQCRequest;
import com.yuansfer.pay.bean.MixedQueryResponse;
import com.yuansfer.pay.bean.TransAddRequest;
import com.yuansfer.pay.bean.TransAddResponse;
import com.yuansfer.pay.bean.TransDetailRequest;
import com.yuansfer.pay.bean.TransDetailResponse;
import com.yuansfer.pay.bean.TransPrepayRequest;
import com.yuansfer.pay.bean.TransPrepayResponse;
import com.yuansfer.pay.bean.TransRefundRequest;
import com.yuansfer.pay.bean.TransRefundResponse;
import com.yuansfer.pay.bean.TransStatusRequest;
import com.yuansfer.pay.bean.TransStatusResponse;
import com.yuansfer.pay.bean.TransTipRequest;

/**
 * @author fly
 * @desc 支付交易的在线API
 */
public interface IClientAPI {

    /**
     * 配置API环境和超时时间
     *
     * @param config 配置
     */
    void apiConfig(APIConfig config);

    /**
     * 通用的API接口请求
     *
     * @param apiPath          接口路径地址
     * @param reqJson          接口请求JSON
     * @param responseListener 接口原数据回调
     */
    void apiPost(String apiPath, String reqJson, OnResponseListener<String> responseListener);

    /**
     * 创建交易, 针对钱包类型，如微信、支付宝、PayPal、Venmo等
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    void transAdd(TransAddRequest request, OnResponseListener<TransAddResponse> responseListener);

    /**
     * 预处理订单
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    void transPrepay(TransPrepayRequest request, OnResponseListener<TransPrepayResponse> responseListener);

    /**
     * 交易状态查询
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    void transStatus(TransStatusRequest request, OnResponseListener<TransStatusResponse> responseListener);

    /**
     * 交易详情
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    void transDetail(TransDetailRequest request, OnResponseListener<TransDetailResponse> responseListener);

    /**
     * 交易退款,已对信用卡和非信用卡作了区分处理
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    void transRefund(TransRefundRequest request, OnResponseListener<TransRefundResponse> responseListener);

    /**
     * 更新小费
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    void transTipUpdate(TransTipRequest request, OnResponseListener<TransDetailResponse> responseListener);

    /**
     * 生成混合二维码
     * @param request           请求数据
     * @param responseListener  接口数据回调
     */
    void mixedCodeGenerate(MixedGenRequest request, OnResponseListener<MixedGenResponse> responseListener);

    /**
     * 查询混合二维码状态
     * @param request           请求数据
     * @param responseListener  接口数据回调
     */
    void mixedCodeQuery(MixedQCRequest request, OnResponseListener<MixedQueryResponse> responseListener);

    /**
     * 取消混合二维码
     * @param request           请求数据
     * @param responseListener  接口数据回调
     */
    void mixedCodeCancel(MixedQCRequest request, OnResponseListener<MixedCancelResponse> responseListener);

    /**
     * 取消所有请求
     */
    void cancelAll();

    /**
     * 根据tag取消单个请求
     *
     * @param t 请求request同一实例
     * @return 取消的数量
     */
    <T extends BaseRequest> int cancel(T t);

}
