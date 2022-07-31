package com.yuansfer.pay;

import android.os.Handler;

import com.ejlchina.okhttps.GsonMsgConvertor;
import com.ejlchina.okhttps.HTTP;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yuansfer.pay.api.APIConfig;
import com.yuansfer.pay.api.APIHelper;
import com.yuansfer.pay.api.APIPath;
import com.yuansfer.pay.api.IClientAPI;
import com.yuansfer.pay.api.OnResponseListener;
import com.yuansfer.pay.bean.BaseRequest;
import com.yuansfer.pay.bean.MixedCancelResponse;
import com.yuansfer.pay.bean.MixedGenRequest;
import com.yuansfer.pay.bean.MixedGenResponse;
import com.yuansfer.pay.bean.MixedQCRequest;
import com.yuansfer.pay.bean.MixedQueryResponse;
import com.yuansfer.pay.bean.TransAddRequest;
import com.yuansfer.pay.bean.TransAddResponse;
import com.yuansfer.pay.bean.TransCancelBean;
import com.yuansfer.pay.bean.TransCancelRequest;
import com.yuansfer.pay.bean.TransCancelResponse;
import com.yuansfer.pay.bean.TransDetailBean;
import com.yuansfer.pay.bean.TransDetailRequest;
import com.yuansfer.pay.bean.TransDetailResponse;
import com.yuansfer.pay.bean.TransPrepayRequest;
import com.yuansfer.pay.bean.TransPrepayResponse;
import com.yuansfer.pay.bean.TransRefundBean;
import com.yuansfer.pay.bean.TransRefundRequest;
import com.yuansfer.pay.bean.TransRefundResponse;
import com.yuansfer.pay.bean.TransStatusRequest;
import com.yuansfer.pay.bean.TransStatusResponse;
import com.yuansfer.pay.bean.TransTipRequest;
import com.yuansfer.pay.util.YSException;
import com.yuansfer.pay.util.LogUtils;
import com.yuansfer.pay.api.LogInterceptor;
import com.yuansfer.pay.api.RetryInterceptor;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class ClientAPIImpl implements IClientAPI {

    private static final String JSON_CONVERT_FAIL = "Json data convert fail";
    private static String sBaseURL = APIPath.RELEASE_URL;
    private static ClientAPIImpl sPosAPI;
    private final Handler mHandler;
    private Gson mGson;
    private HTTP mHTTP;

    private ClientAPIImpl(Handler handler) {
        mHandler = handler;
    }

    /**
     * 获取单例
     *
     * @return PosAPI
     */
    public static ClientAPIImpl get(Handler handler) {
        if (sPosAPI == null) {
            sPosAPI = new ClientAPIImpl(handler);
        }
        return sPosAPI;
    }

    /**
     * 创建网络请求实例
     */
    private synchronized void createHTTP(final APIConfig config) {
        LogUtils.d("Create HTTP, config=" + config);
        mHTTP = new HTTP.Builder().baseUrl(sBaseURL)
                .config(builder -> {
                    if (config != null && config.getRetryTimes() > 0) {
                        // 添加重试机制
                        builder.addInterceptor(new RetryInterceptor(config.getRetryTimes()));
                    }
                    if (LogUtils.logEnable) {
                        // 添加日志打印
                        builder.addInterceptor(new LogInterceptor());
                    }
                    builder.connectTimeout(config != null ? config.getConnectTimeout()
                            : APIConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    builder.writeTimeout(config != null ? config.getWriteTimeout()
                            : APIConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    builder.readTimeout(config != null ? config.getReadTimeout()
                            : APIConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                })
                .callbackExecutor(new Executor() {
                    @Override
                    public void execute(Runnable run) {
                        // 所有回调在主线程执行
                        LogUtils.d("Callback Thread:" + mHandler);
                        if (mHandler != null) {
                            mHandler.post(run);
                        }
                    }
                })
                .addMsgConvertor(new GsonMsgConvertor())
                .build();
    }

    /**
     * 配置API环境和超时时间
     *
     * @param config 配置
     */
    @Override
    public void apiConfig(APIConfig config) {
        sBaseURL = config.isSandboxEnv()
                ? APIPath.SANDBOX_URL : APIPath.RELEASE_URL;
        createHTTP(config);
    }

    /**
     * 通用的API接口请求
     *
     * @param apiPath          接口路径地址
     * @param reqJson          接口请求JSON
     * @param responseListener 接口原数据回调
     */
    public void apiPost(String apiPath, String reqJson, final OnResponseListener<String> responseListener) {
        try {
            Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
            APIHelper.checkAPIAccountParams(reqMap);
            String token = APIHelper.getTokenFromMap(reqMap);
            APIHelper.removeTokenFromMap(reqMap);
            APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
            getHTTP().async(apiPath).addBodyPara(reqMap).tag(reqJson)
                    .setOnResString(data -> {
                        LogUtils.d("API post request data:" + data);
                        responseListener.onSuccess(data);
                    })
                    .setOnException(e -> {
                        LogUtils.d("API post request fail:" + e.getMessage());
                        responseListener.onFail(e);
                    }).post();
        } catch (JsonSyntaxException e) {
            responseListener.onFail(e);
        }
    }

    /**
     * 创建交易, 针对钱包类型，如微信、支付宝、PayPal、Venmo等
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void transAdd(TransAddRequest request, final OnResponseListener<TransAddResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_ADD).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransAddResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransAddResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 预处理订单
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void transPrepay(TransPrepayRequest request, final OnResponseListener<TransPrepayResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_PREPAY).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransPrepayResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransPrepayResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 交易状态查询
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void transStatus(TransStatusRequest request, final OnResponseListener<TransStatusResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_STATUS).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransStatusResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransStatusResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 交易详情
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void transDetail(TransDetailRequest request, final OnResponseListener<TransDetailResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_DETAIL).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransDetailResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransDetailResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 更新小费
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void transTipUpdate(TransTipRequest request, final OnResponseListener<TransDetailResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_TIP_UPDATE).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransDetailResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransDetailResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 交易退款,已对信用卡和非信用卡作了区分处理
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void transRefund(final TransRefundRequest request, final OnResponseListener<TransRefundResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        TransDetailRequest detailRequest = new TransDetailRequest();
        detailRequest.setToken(request.getToken());
        detailRequest.setMerchantNo(request.getMerchantNo());
        detailRequest.setStoreNo(request.getStoreNo());
        detailRequest.setTransactionNo(request.getTransactionNo());
        detailRequest.setReference(request.getReference());
        transDetail(detailRequest, new OnResponseListener<TransDetailResponse>() {
            @Override
            public void onSuccess(TransDetailResponse transDetailResponse) {
                if (transDetailResponse.isSuccess()) {
                    TransDetailBean detailBean = transDetailResponse.getResult();
                    LogUtils.d(String.format("Trans vendor=%s, batchFlag=%d"
                            , detailBean.getVendor(), detailBean.getBatchFlag()));
                    if (("creditcard".equalsIgnoreCase(detailBean.getVendor())
                            || "debitcard".equalsIgnoreCase(detailBean.getVendor()))
                            && detailBean.getBatchFlag() == 0) {
                        requestCancel(request, responseListener);
                    } else {
                        requestRefund(request, responseListener);
                    }
                } else {
                    LogUtils.d("Trans detail query fail");
                    TransRefundResponse response = new TransRefundResponse();
                    response.setRet_code(transDetailResponse.getRet_code());
                    response.setRet_msg(transDetailResponse.getRet_msg());
                    response.setRawData(transDetailResponse.getRawData());
                    responseListener.onSuccess(response);
                }
            }

            @Override
            public void onFail(Exception e) {
                responseListener.onFail(e);
            }
        });
    }

    /**
     * 执行退款
     */
    private void requestRefund(TransRefundRequest request, final OnResponseListener<TransRefundResponse> responseListener) {
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_REFUND).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransRefundResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransRefundResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 执行撤销
     */
    private void requestCancel(TransRefundRequest request, final OnResponseListener<TransRefundResponse> responseListener) {
        TransCancelRequest cancelRequest = new TransCancelRequest();
        cancelRequest.setMerchantNo(request.getMerchantNo());
        cancelRequest.setStoreNo(request.getStoreNo());
        cancelRequest.setTransactionNo(request.getTransactionNo());
        cancelRequest.setReference(request.getReference());
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.TRANS_CANCEL).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    TransCancelResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, TransCancelResponse.class);
                    if (response != null) {
                        TransRefundResponse refundResponse = new TransRefundResponse();
                        refundResponse.setRet_code(response.getRet_code());
                        refundResponse.setRet_msg(response.getRet_msg());
                        refundResponse.setRawData(response.getRawData());
                        if (response.getResult() != null) {
                            //信用卡只支持全额撤销
                            TransRefundBean refundBean = new TransRefundBean();
                            TransCancelBean cancelBean = response.getResult();
                            refundBean.setRefundAmount(cancelBean.getAmount());
                            refundBean.setCurrency(cancelBean.getCurrency());
                            refundBean.setReference(cancelBean.getReference());
                            refundBean.setStatus(cancelBean.getStatus());
                            refundBean.setOldTransactionId(cancelBean.getTransactionNo());
                            refundBean.setAmount(cancelBean.getAmount());
                            refundResponse.setResult(refundBean);
                        }
                        responseListener.onSuccess(refundResponse);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(data -> responseListener.onFail(data)).post();
    }

    /**
     * 生成混合二维码
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void mixedCodeGenerate(MixedGenRequest request, final OnResponseListener<MixedGenResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.MIXED_CODE_GENERATE).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    MixedGenResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, MixedGenResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(responseListener::onFail).post();
    }

    /**
     * 查询混合二维码状态
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void mixedCodeQuery(MixedQCRequest request, OnResponseListener<MixedQueryResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.MIXED_CODE_QUERY).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    MixedQueryResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, MixedQueryResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(responseListener::onFail).post();
    }

    /**
     * 取消混合二维码
     *
     * @param request          请求数据
     * @param responseListener 接口数据回调
     */
    @Override
    public void mixedCodeCancel(MixedQCRequest request, OnResponseListener<MixedCancelResponse> responseListener) {
        APIHelper.checkAPIAccountParams(request);
        String token = request.getToken();
        String reqJson = getGson().toJson(request);
        Map<String, Object> reqMap = getGson().fromJson(reqJson, Map.class);
        APIHelper.removeTokenFromMap(reqMap);
        APIHelper.addVerifySign2Map(reqMap, APIHelper.getVerifySign(reqMap, token));
        getHTTP().async(APIPath.MIXED_CODE_CANCEL).addBodyPara(reqMap).tag(reqJson)
                .setOnResString(data -> {
                    MixedCancelResponse response = APIHelper.convertResponseKeepRaw(getGson(), data, MixedCancelResponse.class);
                    if (response != null) {
                        responseListener.onSuccess(response);
                    } else {
                        responseListener.onFail(new YSException(JSON_CONVERT_FAIL));
                    }
                })
                .setOnException(responseListener::onFail).post();
    }

    /**
     * 取消所有请求
     */
    public void cancelAll() {
        getHTTP().cancelAll();
    }

    /**
     * 根据tag取消单个请求
     *
     * @param t 请求request同一实例
     * @return 取消的数量
     */
    public <T extends BaseRequest> int cancel(T t) {
        String tag = getGson().toJson(t);
        LogUtils.d("Cancel request, tag:" + tag);
        return getHTTP().cancel(tag);
    }

    private Gson getGson() {
        if (mGson == null) {
            LogUtils.d("Create default Gson");
            mGson = new Gson();
        }
        return mGson;
    }

    private HTTP getHTTP() {
        if (mHTTP == null) {
            LogUtils.d("Create default HTTP");
            createHTTP(null);
        }
        return mHTTP;
    }

}
