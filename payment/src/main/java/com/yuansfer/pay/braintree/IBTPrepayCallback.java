package com.yuansfer.pay.braintree;

import com.braintreepayments.api.models.Configuration;
import com.yuansfer.pay.util.ErrStatus;

public interface IBTPrepayCallback {

    void onPaymentConfigurationFetched(Configuration configuration);

    void onPrepayCancel();

    void onPrepayError(ErrStatus errStatus);

}
