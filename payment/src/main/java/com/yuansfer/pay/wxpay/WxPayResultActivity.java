package com.yuansfer.pay.wxpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yuansfer.pay.payment.ErrStatus;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
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
        WxPayStrategy.getInstance().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        WxPayStrategy.getInstance().handleIntent(intent, this);
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
                PayResultMgr.getInstance().dispatchPaySuccess(PayType.WECHAT_PAY);
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                PayResultMgr.getInstance().dispatchPayCancel(PayType.WECHAT_PAY);
            } else {
                PayResultMgr.getInstance().dispatchPayFail(PayType.WECHAT_PAY
                        , ErrStatus.getInstance("W001", resp.errStr));
            }
            finish();
        }
    }

}
