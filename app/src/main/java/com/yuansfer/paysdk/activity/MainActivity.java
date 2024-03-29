package com.yuansfer.paysdk.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.yuansfer.pay.YSAppPay;
import com.yuansfer.pay.api.APIConfig;
import com.yuansfer.pay.api.OnResponseListener;
import com.yuansfer.pay.bean.MixedCancelResponse;
import com.yuansfer.pay.bean.MixedGenRequest;
import com.yuansfer.pay.bean.MixedGenResponse;
import com.yuansfer.pay.bean.MixedQCRequest;
import com.yuansfer.pay.bean.MixedQueryResponse;
import com.yuansfer.pay.bean.TransAddRequest;
import com.yuansfer.pay.bean.TransAddResponse;
import com.yuansfer.pay.bean.TransDetailRequest;
import com.yuansfer.pay.bean.TransDetailResponse;
import com.yuansfer.pay.bean.TransInitBean;
import com.yuansfer.pay.bean.TransPrepayRequest;
import com.yuansfer.pay.bean.TransPrepayResponse;
import com.yuansfer.pay.bean.TransRefundRequest;
import com.yuansfer.pay.bean.TransRefundResponse;
import com.yuansfer.pay.bean.TransStatusRequest;
import com.yuansfer.pay.bean.TransStatusResponse;
import com.yuansfer.pay.bean.TransTipRequest;
import com.yuansfer.paysdk.R;
import com.yuansfer.paysdk.model.AutoDebitRequest;
import com.yuansfer.paysdk.model.MicroPayRequest;
import com.yuansfer.pay.util.ErrStatus;
import com.yuansfer.pay.aliwx.AliWxPayMgr;
import com.yuansfer.pay.aliwx.AliWxType;
import com.yuansfer.pay.aliwx.WxPayItem;
import com.yuansfer.paysdk.util.Logger;
import com.yuansfer.paysdk.util.YSAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ActionBar.OnNavigationListener
        , AliWxPayMgr.IAliWxPayCallback {

    private EditText mAlipayEdt, mWechatPayEdt, mMultiEdt;
    private Spinner mCurrencySpn;
    private TransInitBean mTransactionBean;
    private Logger mLogger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YSAppPay.getAliWxPay().registerAliWxPayCallback(this);
        setContentView(R.layout.activity_main);
        initViews();
        setupActionBar();
        YSAppPay.setLogEnable(true);
    }

    private void initViews() {
        mLogger = new Logger(findViewById(R.id.tv_logger));
        mAlipayEdt = findViewById(R.id.edt_ali);
        mWechatPayEdt = findViewById(R.id.edt_wx);
        mMultiEdt = findViewById(R.id.edt_secure_pay);
        mCurrencySpn = findViewById(R.id.sp_multi_currency);
        setDefaultEditRef(mAlipayEdt);
        setDefaultEditRef(mWechatPayEdt);
        setDefaultEditRef(mMultiEdt);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.environments,
                android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, this);
        actionBar.setSelectedNavigationItem(0);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        String env = getResources().getStringArray(R.array.environments)[itemPosition];
        if ("TEST".equals(env)) {
            YSAuth.sToken = YSAuth.TEST_TOKEN;
            YSAppPay.getAliWxPay().setAliEnv(false);
            YSAppPay.getClientAPI().apiConfig(new APIConfig.Builder()
                    .setSandboxEnv(true).build());
        } else {
            YSAuth.sToken = YSAuth.PRODUCTION_TOKEN;
            YSAppPay.getAliWxPay().setAliEnv(true);
            YSAppPay.getClientAPI().apiConfig(new APIConfig.Builder()
                    .setSandboxEnv(false).build());
        }
        return true;
    }

    public void onViewClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        switch (view.getId()) {
            case R.id.btn_ali:
                MicroPayRequest info = new MicroPayRequest();
                info.setToken(YSAuth.sToken);
                info.setMerchantNo(YSAuth.MERCHANT_NO);
                info.setStoreNo(YSAuth.STORE_NO);
                info.setVendor("alipay");
                info.setAmount(0.01);
                info.setIpnUrl("https://xxxx.com/alipay/notify");
                info.setDescription("description");
                info.setNote("note");
                info.setReference(getEditRef(mAlipayEdt));
                YSAppPay.getClientAPI().apiPost("/micropay/v3/prepay", new Gson().toJson(info)
                        , new OnResponseListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                setDefaultEditRef(mAlipayEdt);
                                try {
                                    JSONObject dataObj = new JSONObject(s);
                                    if (dataObj.has("result")) {
                                        JSONObject resultObj = dataObj.getJSONObject("result");
                                        YSAppPay.getAliWxPay().requestAliPayment(MainActivity.this
                                                , resultObj.getString("payInfo"));
                                    }
                                } catch (JSONException e) {
                                    mLogger.log(e.getMessage());
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                mLogger.log(e.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.btn_wx:
                MicroPayRequest wxRequest = new MicroPayRequest();
                wxRequest.setToken(YSAuth.sToken);
                wxRequest.setMerchantNo(YSAuth.MERCHANT_NO);
                wxRequest.setStoreNo(YSAuth.STORE_NO);
                wxRequest.setVendor("wechatpay");
                wxRequest.setAmount(0.01);
                wxRequest.setIpnUrl("https://xxxx.com/wx/notify");
                wxRequest.setDescription("description");
                wxRequest.setNote("note");
                wxRequest.setReference(getEditRef(mWechatPayEdt));
                YSAppPay.getClientAPI().apiPost("/micropay/v3/prepay", new Gson().toJson(wxRequest)
                        , new OnResponseListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                setDefaultEditRef(mWechatPayEdt);
                                try {
                                    JSONObject dataObj = new JSONObject(s);
                                    if (dataObj.has("result")) {
                                        JSONObject resultObj = dataObj.getJSONObject("result");
                                        YSAppPay.getAliWxPay().registerWXAPP(getApplicationContext()
                                                , resultObj.getString("appid"));
                                        WxPayItem wxPayItem = new WxPayItem();
                                        wxPayItem.setAppId(resultObj.getString("appid"));
                                        wxPayItem.setPackageValue(resultObj.getString("package"));
                                        wxPayItem.setPrepayId(resultObj.getString("prepayid"));
                                        wxPayItem.setPartnerId(resultObj.getString("partnerid"));
                                        wxPayItem.setNonceStr(resultObj.getString("noncestr"));
                                        wxPayItem.setSign(resultObj.getString("sign"));
                                        wxPayItem.setTimestamp(resultObj.getString("timestamp"));
                                        YSAppPay.getAliWxPay().requestWechatPayment(wxPayItem);
                                    }
                                } catch (JSONException e) {
                                    mLogger.log(e.getMessage());
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                mLogger.log(e.getLocalizedMessage());
                            }
                        });
               break;
            case R.id.btn_secure_pay:
                AutoDebitRequest autoRequest = new AutoDebitRequest();
                autoRequest.setToken(YSAuth.sToken);
                autoRequest.setMerchantNo(YSAuth.MERCHANT_NO);
                autoRequest.setStoreNo(YSAuth.STORE_NO);
                autoRequest.setAutoIpnUrl("https://app.yuansfer.com/ad");
                autoRequest.setAutoRedirectUrl("https://app.yuansfer.com/ad");
                autoRequest.setAutoReference(getEditRef(mMultiEdt));
                autoRequest.setOsType("ANDROID");
                autoRequest.setOsVersion("10");
                autoRequest.setNote("note");
                autoRequest.setTerminal("APP");
                autoRequest.setVendor(mCurrencySpn.getSelectedItem().toString());
                YSAppPay.getClientAPI().apiPost("/auto-debit/v3/consult", new Gson().toJson(autoRequest)
                        , new OnResponseListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                try {
                                    JSONObject retObj = new JSONObject(s);
                                    if ("000100".equals(retObj.optString("ret_code"))) {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(retObj.optJSONObject("result").optString("authUrl")));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            mLogger.log(e.getMessage());
                                        }
                                    } else {
                                        mLogger.log(retObj.optString("ret_msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                mLogger.log(e.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.btn_google_pay:
                startActivity(new Intent(this, GooglePayActivity.class));
                break;
            case R.id.btn_dropin_ui:
                startActivity(new Intent(this, DropInPayActivity.class));
                break;
            case R.id.btn_paypal:
                startActivity(new Intent(this, PayPalActivity.class));
                break;
            case R.id.btn_venmo:
                startActivity(new Intent(this, VenmoActivity.class));
                break;
            case R.id.btn_creditcard:
                startActivity(new Intent(this, CardActivity.class));
                break;
            case R.id.btn_cashapp:
                CashAppPayActivity.launchActivity(this, YSAuth.sToken == YSAuth.PRODUCTION_TOKEN);
                break;
            case R.id.btn_add:
                TransAddRequest request1 = new TransAddRequest();
                request1.setAmount(0.01);
                request1.setMerchantNo(YSAuth.MERCHANT_NO);
                request1.setStoreNo(YSAuth.STORE_NO);
                request1.setToken(YSAuth.sToken);
                YSAppPay.getClientAPI().transAdd(request1, new OnResponseListener<TransAddResponse>() {
                    @Override
                    public void onSuccess(TransAddResponse response) {
                        mLogger.log(response.getRawData());
                        if (response.isSuccess()) {
                            mTransactionBean = response.getResult();
                        } else {
                            mTransactionBean = null;
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        mTransactionBean = null;
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_prepay:
                if (mTransactionBean == null) {
                    Toast.makeText(this, "click ADD button", Toast.LENGTH_LONG).show();
                    return;
                }
                TransPrepayRequest request2 = new TransPrepayRequest();
                request2.setToken(YSAuth.sToken);
                request2.setMerchantNo(YSAuth.MERCHANT_NO);
                request2.setStoreNo(YSAuth.STORE_NO);
                //request2.setReference("189189");
                request2.setTransactionNo(mTransactionBean.getTransactionNo());
                request2.setPaymentBarcode("1111");
                YSAppPay.getClientAPI().transPrepay(request2, new OnResponseListener<TransPrepayResponse>() {
                    @Override
                    public void onSuccess(TransPrepayResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });

                break;
            case R.id.btn_tran_query:
                if (mTransactionBean == null) {
                    Toast.makeText(this, "click ADD button", Toast.LENGTH_LONG).show();
                    return;
                }
                TransStatusRequest request3 = new TransStatusRequest();
                request3.setToken(YSAuth.sToken);
                request3.setMerchantNo(YSAuth.MERCHANT_NO);
                request3.setStoreNo(YSAuth.STORE_NO);
                request3.setTransactionNo(mTransactionBean.getTransactionNo());
                YSAppPay.getClientAPI().transStatus(request3, new OnResponseListener<TransStatusResponse>() {
                    @Override
                    public void onSuccess(TransStatusResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_tran_detail:
                TransDetailRequest request4 = new TransDetailRequest();
                request4.setToken(YSAuth.sToken);
                request4.setMerchantNo(YSAuth.MERCHANT_NO);
                request4.setStoreNo(YSAuth.STORE_NO);
                request4.setTransactionNo("306243301721625547");
                YSAppPay.getClientAPI().transDetail(request4, new OnResponseListener<TransDetailResponse>() {
                    @Override
                    public void onSuccess(TransDetailResponse transDetailResponse) {
                        mLogger.log(transDetailResponse.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_tran_refund:
                TransRefundRequest request5 = new TransRefundRequest();
                request5.setToken(YSAuth.sToken);
                request5.setMerchantNo(YSAuth.MERCHANT_NO);
                request5.setStoreNo(YSAuth.STORE_NO);
                request5.setRefundAmount(0.01);
                request5.setTransactionNo("299312701745541694");
                YSAppPay.getClientAPI().transRefund(request5, new OnResponseListener<TransRefundResponse>() {
                    @Override
                    public void onSuccess(TransRefundResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_tran_tip:
                TransTipRequest request7 = new TransTipRequest();
                request7.setToken(YSAuth.sToken);
                request7.setMerchantNo(YSAuth.MERCHANT_NO);
                request7.setStoreNo(YSAuth.STORE_NO);
                request7.setTip(0.02);
                request7.setTransactionNo("306243301721625547");
                YSAppPay.getClientAPI().transTipUpdate(request7, new OnResponseListener<TransDetailResponse>() {
                    @Override
                    public void onSuccess(TransDetailResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_api_post:
                TransDetailRequest request8 = new TransDetailRequest();
                request8.setToken(YSAuth.sToken);
                request8.setMerchantNo(YSAuth.MERCHANT_NO);
                request8.setStoreNo(YSAuth.STORE_NO);
                request8.setTransactionNo("297553669861880847");
                YSAppPay.getClientAPI().apiPost("/app-instore/v3/detail", new Gson().toJson(request8)
                        , new OnResponseListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                mLogger.log(s);
                            }

                            @Override
                            public void onFail(Exception e) {
                                mLogger.log(e.getLocalizedMessage());
                            }
                        });
                break;
            case R.id.btn_mix_gen:
                MixedGenRequest request9 = new MixedGenRequest();
                request9.setToken(YSAuth.sToken);
                request9.setMerchantNo(YSAuth.MERCHANT_NO);
                request9.setStoreNo(YSAuth.STORE_NO);
                request9.setSaleAmount(0.01);
                request9.setNeedTip(true);
                request9.setNeedQrcode(true);
                request9.setIpnUrl("http://zk-tys.yunkeguan.com/ttest/test");
                request9.setReference(Calendar.getInstance().getTimeInMillis()+"");
                YSAppPay.getClientAPI().mixedCodeGenerate(request9, new OnResponseListener<MixedGenResponse>() {
                    @Override
                    public void onSuccess(MixedGenResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_mix_query:
                MixedQCRequest request10 = new MixedQCRequest();
                request10.setToken(YSAuth.sToken);
                request10.setMerchantNo(YSAuth.MERCHANT_NO);
                request10.setStoreNo(YSAuth.STORE_NO);
                request10.setRecordNo("303658570649926081");
                YSAppPay.getClientAPI().mixedCodeQuery(request10, new OnResponseListener<MixedQueryResponse>() {
                    @Override
                    public void onSuccess(MixedQueryResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
            case R.id.btn_mix_cancel:
                MixedQCRequest request11 = new MixedQCRequest();
                request11.setToken(YSAuth.sToken);
                request11.setMerchantNo(YSAuth.MERCHANT_NO);
                request11.setStoreNo(YSAuth.STORE_NO);
                request11.setRecordNo("303658570649926081");
                YSAppPay.getClientAPI().mixedCodeCancel(request11, new OnResponseListener<MixedCancelResponse>() {
                    @Override
                    public void onSuccess(MixedCancelResponse response) {
                        mLogger.log(response.getRawData());
                    }

                    @Override
                    public void onFail(Exception e) {
                        mLogger.log(e.getLocalizedMessage());
                    }
                });
                break;
        }
    }

    @Override
    public void onPaySuccess(int payType) {
        mLogger.log(payType == AliWxType.ALIPAY ? "Alipay succeed" : "WechatPay succeed");
    }

    @Override
    public void onPayFail(@AliWxType int payType, ErrStatus errStatus) {
        mLogger.log(String.format(payType == AliWxType.ALIPAY ? "Alipay failed:%s" : "WechatPay failed:%s"
                , errStatus.getErrCode() + "/" + errStatus.getErrMsg()));
    }

    @Override
    public void onPayCancel(int payType) {
        mLogger.log(payType == AliWxType.ALIPAY ? "Alipay canceled" : "WechatPay canceled");
    }

    private String getEditRef(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString())
                ? editText.getHint().toString() : editText.getText().toString();
    }

    private void setDefaultEditRef(EditText editRef) {
        editRef.setHint(System.currentTimeMillis() + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSAppPay.getAliWxPay().unregisterAliWxPayCallback(this);
    }

}
