package com.yuansfer.pay.cashapp;

public class OnFileItem {

    private String redirectUri;
    private String scopeId;
    private String accountReferenceId;

    public OnFileItem(String redirectUri, String scopeId, String accountReferenceId) {
        this.redirectUri = redirectUri;
        this.scopeId = scopeId;
        this.accountReferenceId = accountReferenceId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getAccountReferenceId() {
        return accountReferenceId;
    }

    public void setAccountReferenceId(String accountReferenceId) {
        this.accountReferenceId = accountReferenceId;
    }
}
