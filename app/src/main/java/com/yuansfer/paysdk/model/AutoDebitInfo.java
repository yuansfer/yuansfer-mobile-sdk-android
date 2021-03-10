package com.yuansfer.paysdk.model;

import java.util.HashMap;

public class AutoDebitInfo extends SignInfo {

    private String note;
    private String osType = "ANDROID";
    private String osVersion;
    private String autoIpnUrl;
    private String autoRedirectUrl;
    private String autoReference;
    private String terminal = "APP";
    private String vendor;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAutoIpnUrl() {
        return autoIpnUrl;
    }

    public void setAutoIpnUrl(String autoIpnUrl) {
        this.autoIpnUrl = autoIpnUrl;
    }

    public String getAutoRedirectUrl() {
        return autoRedirectUrl;
    }

    public void setAutoRedirectUrl(String autoRedirectUrl) {
        this.autoRedirectUrl = autoRedirectUrl;
    }

    public String getAutoReference() {
        return autoReference;
    }

    public void setAutoReference(String autoReference) {
        this.autoReference = autoReference;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap map = super.toHashMap();
        map.put("note", note);
        map.put("osType", osType);
        map.put("osVersion", osVersion);
        map.put("autoIpnUrl", autoIpnUrl);
        map.put("autoRedirectUrl", autoRedirectUrl);
        map.put("autoReference", autoReference);
        map.put("terminal",terminal);
        map.put("vendor", vendor);
        return map;
    }

}
