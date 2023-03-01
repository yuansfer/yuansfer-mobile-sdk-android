package com.yuansfer.pay;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.GooglePayment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.Venmo;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PayPalRequest;
import com.yuansfer.pay.braintree.BTCustomPayActivity;
import com.yuansfer.pay.braintree.BTDropInActivity;
import com.yuansfer.pay.braintree.IBraintreePay;
import com.yuansfer.pay.util.ErrStatus;

class BraintreePayImpl implements IBraintreePay {

    private static BraintreePayImpl instance;

    private BraintreePayImpl() {
    }

    public static BraintreePayImpl get() {
        if (instance == null) {
            instance = new BraintreePayImpl();
        }
        return instance;
    }

    @Override
    public <T extends BTCustomPayActivity> void bindBrainTree(T activity, String authorization) {
        try {
            activity.setBrainTreeFragment(BraintreeFragment.newInstance(activity, authorization));
        } catch (InvalidArgumentException e) {
            activity.onPrepayError(ErrStatus.getInstance(ErrStatus.BT_INIT_ERROR, e.getMessage()));
        }
    }

    @Override
    public <T extends BTCustomPayActivity> void unbindBrainTree(T activity) {
        BraintreeFragment braintreeFragment = activity.getBrainTreeFragment();
        if (braintreeFragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .remove(braintreeFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public <T extends BTCustomPayActivity> void requestGooglePayment(T activity, GooglePaymentRequest googlePaymentRequest) {
        GooglePayment.requestPayment(activity.getBrainTreeFragment(), googlePaymentRequest);
    }

    @Override
    public <T extends BTCustomPayActivity> void requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestOneTimePayment(activity.getBrainTreeFragment(), payPalRequest);
    }

    @Override
    public <T extends BTCustomPayActivity> void requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest) {
        PayPal.requestBillingAgreement(activity.getBrainTreeFragment(), payPalRequest);
    }

    @Override
    public <T extends BTCustomPayActivity> void requestVenmoPayment(T activity, boolean vault) {
        Venmo.authorizeAccount(activity.getBrainTreeFragment(), vault);
    }

    @Override
    public <T extends BTCustomPayActivity> void requestCardPayment(T activity, CardBuilder cardBuilder) {
        Card.tokenize(activity.getBrainTreeFragment(), cardBuilder);
    }

    @Override
    public <T extends BTDropInActivity> void requestDropInPayment(T activity, String authorization, DropInRequest dropInRequest) {
        dropInRequest.clientToken(authorization);
        activity.startActivityForResult(dropInRequest.getIntent(activity), BTDropInActivity.REQUEST_CODE);
    }
}
