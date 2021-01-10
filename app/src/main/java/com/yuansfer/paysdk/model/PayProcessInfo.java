package com.yuansfer.paysdk.model;

import java.util.HashMap;

public class PayProcessInfo extends SignInfo {

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String countryCode;
    private String customerNo;
    private String email;
    private String paymentMethod;
    private String paymentMethodNonce;
    private String paymentType;
    private String phone;
    private String postalCode;
    private String recipientName;
    private String state;
    private String transactionNo;
    private String deviceData;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(String deviceData) {
        this.deviceData = deviceData;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> requestMap = super.toHashMap();
        requestMap.put("addressLine1", addressLine1);
        requestMap.put("addressLine2", addressLine2);
        requestMap.put("city", city);
        requestMap.put("countryCode", countryCode);
        requestMap.put("customerNo", customerNo);
        requestMap.put("email", email);
        requestMap.put("paymentMethod", paymentMethod);
        requestMap.put("paymentMethodNonce", paymentMethodNonce);
        requestMap.put("paymentType", paymentType);
        requestMap.put("phone", phone);
        requestMap.put("postalCode", postalCode);
        requestMap.put("recipientName", recipientName);
        requestMap.put("state", state);
        requestMap.put("transactionNo", transactionNo);
        requestMap.put("deviceData", deviceData);
        return requestMap;
    }
}
