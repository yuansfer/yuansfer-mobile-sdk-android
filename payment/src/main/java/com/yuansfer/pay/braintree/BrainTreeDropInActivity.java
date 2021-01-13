package com.yuansfer.pay.braintree;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.Configuration;
import com.yuansfer.pay.payment.PayResultMgr;
import com.yuansfer.pay.payment.PayType;
import com.yuansfer.pay.braintree.IBrainTreeCallback;
import com.yuansfer.pay.braintree.BrainTreeListenerHandler;
import com.yuansfer.pay.util.LogUtils;

public abstract class BrainTreeDropInActivity extends AppCompatActivity implements IBrainTreeCallback {

    private static final String TAG = "YSDropInPayActivity";
    public static final int REQUEST_CODE = 1379;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
                BrainTreeListenerHandler.handlerPaymentMethodNonceCreated(result.getPaymentMethodNonce()
                        , result.getDeviceData(), this);
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
                LogUtils.d(TAG, "Drop-In UI Pay canceled");
                PayResultMgr.getInstance().dispatchPayCancel(PayType.GOOGLE_PAY);
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                LogUtils.d(TAG, "Drop-In UI Pay error:" + error.getMessage());
                BrainTreeListenerHandler.handleError(error);
            }
        }
    }

    @Override
    public final void onPaymentConfigurationFetched(Configuration configuration) {

    }
}
