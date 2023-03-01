package com.yuansfer.pay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yuansfer.pay.cashapp.ICashAppPay;
import com.yuansfer.pay.cashapp.ICashPayCallback;
import com.yuansfer.pay.cashapp.OnFileItem;
import com.yuansfer.pay.cashapp.OneTimeItem;
import com.yuansfer.pay.util.YSException;

import app.cash.paykit.core.CashAppPayKit;
import app.cash.paykit.core.CashAppPayKitFactory;
import app.cash.paykit.core.CashAppPayKitListener;
import app.cash.paykit.core.PayKitState;
import app.cash.paykit.core.exceptions.PayKitIntegrationException;
import app.cash.paykit.core.models.sdk.PayKitCurrency;
import app.cash.paykit.core.models.sdk.PayKitPaymentAction;

class CashAppPayImpl implements ICashAppPay,CashAppPayKitListener {

    private static CashAppPayImpl instance;
    private CashAppPayKit cashAppPayKit;
    private ICashPayCallback cashAppCallback;
    private Handler handler;

    private CashAppPayImpl(Handler handler, String clientId, boolean production) {
        this.handler = handler;
        if (production) {
            cashAppPayKit = CashAppPayKitFactory.INSTANCE.create(clientId);
        } else {
            cashAppPayKit = CashAppPayKitFactory.INSTANCE.createSandbox(clientId);
        }
    }

    public static ICashAppPay get(Handler handler, String clientId, boolean production) {
        if (instance == null) {
            instance = new CashAppPayImpl(handler, clientId, production);
        }
        return instance;
    }

    @Override
    public void registerPayEventCallback(ICashPayCallback callback) {
        cashAppCallback = callback;
        cashAppPayKit.registerForStateUpdates(this);
    }

    @Override
    public void unRegisterPayEventCallback() {
        cashAppCallback = null;
        cashAppPayKit.unregisterFromStateUpdates();
    }

    @Override
    public void requestCashAppPayInBackground(OneTimeItem item) throws YSException{
        PayKitCurrency payKitCurrency = PayKitCurrency.USD;
        if (!payKitCurrency.getBackendValue().equals(item.getCurrency())) {
            throw new YSException("Not supported in non-US dollar currency");
        }
        PayKitPaymentAction.OneTimeAction action = new PayKitPaymentAction.OneTimeAction(item.getRedirectUri()
                    , payKitCurrency, (int)(item.getAmount() * 100), item.getScopeId());
        cashAppPayKit.createCustomerRequest(action);
    }

    @Override
    public void requestCashAppPayInBackground(OnFileItem item) {
        PayKitPaymentAction.OnFileAction action = new PayKitPaymentAction.OnFileAction(item.getRedirectUri()
                    , item.getScopeId(), item.getAccountReferenceId());
        cashAppPayKit.createCustomerRequest(action);
    }

    @Override
    public void authorizeCashAppPay(Activity activity) throws YSException{
        try {
            cashAppPayKit.authorizeCustomerRequest(activity);
        } catch (Exception e) {
            throw new YSException(e.getMessage());
        }
    }

    @Override
    public void payKitStateDidChange(@NonNull PayKitState payKitState) {
        if (cashAppCallback == null) {
            return;
        }
        handler.post(() -> {
            if (payKitState.getClass() == PayKitState.ReadyToAuthorize.class) {
                cashAppCallback.onReadyToAuthorize();
            } else if (payKitState.getClass() == PayKitState.Approved.class) {
                cashAppCallback.onApproved();
            } else if (payKitState.getClass() == PayKitState.Declined.class) {
                cashAppCallback.onDeclined();
            }  else {
                cashAppCallback.onEventUpdate(payKitState.getClass().getSimpleName());
            }
        });
    }
}
