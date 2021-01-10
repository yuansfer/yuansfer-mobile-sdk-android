package com.yuansfer.pay.googlepay;

import com.google.android.gms.wallet.WalletConstants;

import java.util.ArrayList;
import java.util.List;

public class YSGooglePayItem {

    private String currency = "USD";
    private double totalPrice;
    private boolean allowPrepaidCards;
    private boolean billingAddressRequired;
    private boolean emailRequired;
    private boolean phoneNumberRequired;
    private boolean shippingAddressRequired;
    private boolean shippingAddressRequirements;
    private List<String> addAllowedCountryCodes;
    private String googleMerchantId;
    private int totalPriceStatus = WalletConstants.TOTAL_PRICE_STATUS_FINAL;
    private int billingAddressFormat = WalletConstants.BILLING_ADDRESS_FORMAT_FULL;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isAllowPrepaidCards() {
        return allowPrepaidCards;
    }

    public void setAllowPrepaidCards(boolean allowPrepaidCards) {
        this.allowPrepaidCards = allowPrepaidCards;
    }

    public boolean isBillingAddressRequired() {
        return billingAddressRequired;
    }

    public void setBillingAddressRequired(boolean billingAddressRequired) {
        this.billingAddressRequired = billingAddressRequired;
    }

    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

    public boolean isPhoneNumberRequired() {
        return phoneNumberRequired;
    }

    public void setPhoneNumberRequired(boolean phoneNumberRequired) {
        this.phoneNumberRequired = phoneNumberRequired;
    }

    public boolean isShippingAddressRequired() {
        return shippingAddressRequired;
    }

    public void setShippingAddressRequired(boolean shippingAddressRequired) {
        this.shippingAddressRequired = shippingAddressRequired;
    }

    public boolean isShippingAddressRequirements() {
        return shippingAddressRequirements;
    }

    public void setShippingAddressRequirements(boolean shippingAddressRequirements) {
        this.shippingAddressRequirements = shippingAddressRequirements;
    }

    public List<String> getAddAllowedCountryCodes() {
        if (addAllowedCountryCodes == null) {
            addAllowedCountryCodes = new ArrayList<>();
            addAllowedCountryCodes.add("-");
        }
        return addAllowedCountryCodes;
    }

    public void setAddAllowedCountryCodes(List<String> addAllowedCountryCodes) {
        this.addAllowedCountryCodes = addAllowedCountryCodes;
    }

    public String getGoogleMerchantId() {
        return googleMerchantId;
    }

    public void setGoogleMerchantId(String googleMerchantId) {
        this.googleMerchantId = googleMerchantId;
    }

    public int getTotalPriceStatus() {
        return totalPriceStatus;
    }

    public void setTotalPriceStatus(int totalPriceStatus) {
        this.totalPriceStatus = totalPriceStatus;
    }

    public int getBillingAddressFormat() {
        return billingAddressFormat;
    }

    public void setBillingAddressFormat(int billingAddressFormat) {
        this.billingAddressFormat = billingAddressFormat;
    }
}
