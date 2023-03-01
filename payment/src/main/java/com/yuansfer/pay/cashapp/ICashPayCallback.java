package com.yuansfer.pay.cashapp;

public interface ICashPayCallback {
    void onReadyToAuthorize();
    void onApproved();
    void onDeclined();
    void onEventUpdate(String event);
}
