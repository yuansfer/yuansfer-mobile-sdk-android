package com.yuansfer.sdk.pay.wxpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yuansfer.sdk.YSAppPay;
import com.yuansfer.sdk.pay.PayType;
import com.yuansfer.sdk.pay.PayResultMgr;

/**
 * @Author Fly-Android
 * @CreateDate 2019/5/23 10:11
 * @Desciption 微信支付回调WXPayEntryActivity, 原先需要在app包下定义WXPayEntryActivity类文件用于
 * 接收支付的结果信息,此WxPayResultActivity将接管处理,app无需再配置WXPayEntryActivity
 */
public class WxPayResultActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        if (YSAppPay.sDebug) {
            Log.d(YSAppPay.SDK_TAG, "wxpay start to send request");
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        if (YSAppPay.sDebug) {
            Log.d(YSAppPay.SDK_TAG
                    , String.format("wechat pay onResp call, type=%s,errCode=%s", resp.getType(), resp.errCode));
        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                PayResultMgr.getInstance().dispatchPaySuccess(PayType.WXPAY);
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                PayResultMgr.getInstance().dispatchPayCancel(PayType.WXPAY);
            } else {
                PayResultMgr.getInstance().dispatchPayFail(PayType.WXPAY, resp.errStr);
            }
            finish();
        }
    }

}
