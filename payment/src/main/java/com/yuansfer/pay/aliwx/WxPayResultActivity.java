package com.yuansfer.pay.aliwx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yuansfer.pay.ErrStatus;
import com.yuansfer.pay.util.LogUtils;

/**
 * @Author Fly
 * @CreateDate 2019/5/23 10:11
 * @Desciption 微信支付回调WXPayEntryActivity, 原先需要在app包下定义WXPayEntryActivity类文件用于
 * 接收支付的结果信息,此WxPayResultActivity将接管处理,app无需再配置WXPayEntryActivity
 */
public class WxPayResultActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AliWxPayMgr.getInstance().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AliWxPayMgr.getInstance().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        LogUtils.d("Wechat Pay start to send request");
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtils.d(String.format("Wechat Pay onResp call, type=%s,errCode=%s", resp.getType(), resp.errCode));
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                AliWxPayMgr.getInstance().dispatchPaySuccess(AliWxType.WECHAT_PAY);
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                AliWxPayMgr.getInstance().dispatchPayCancel(AliWxType.WECHAT_PAY);
            } else {
                AliWxPayMgr.getInstance().dispatchPayFail(AliWxType.WECHAT_PAY
                        , ErrStatus.getInstance(ErrStatus.WECHAT_COMMON_ERROR, resp.errStr));
            }
            finish();
        }
    }

}
