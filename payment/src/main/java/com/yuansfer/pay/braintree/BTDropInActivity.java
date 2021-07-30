package com.yuansfer.pay.braintree;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.Configuration;
import com.yuansfer.pay.util.LogUtils;
/**
 * @author fly
 * @date 2020/12/23
 * @desc 集成Braintree带UI方式，以底部弹窗呈现
 */
public abstract class BTDropInActivity extends AppCompatActivity implements IBTNonceCallback, IBTPrepayCallback {

    private static final String TAG = "BTDropInActivity";
    public static final int REQUEST_CODE = 1379;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                BTListenerHandler.handlerPaymentMethodNonceCreated(result.getPaymentMethodNonce()
                        , result.getDeviceData(), this);
            } else if (resultCode == RESULT_CANCELED) {
                LogUtils.d(TAG, "Drop-In UI Pay canceled");
                onPrepayCancel();
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                LogUtils.d(TAG, "Drop-In UI Pay error:" + error.getMessage());
                BTListenerHandler.handleError(error, this);
            }
        }
    }

    @Override
    public void onPaymentConfigurationFetched(Configuration configuration) {

    }

}
