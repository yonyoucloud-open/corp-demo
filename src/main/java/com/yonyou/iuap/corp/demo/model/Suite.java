package com.yonyou.iuap.corp.demo.model;

public class Suite {

    /**
     * 套件 key
     */
    private String suiteKey;

    private String suiteSecret;

    /**
     * 推送的套件票据
     */
    private String suiteTicket;

    public String getSuiteKey() {
        return suiteKey;
    }

    public void setSuiteKey(String suiteKey) {
        this.suiteKey = suiteKey;
    }

    public String getSuiteTicket() {
        return suiteTicket;
    }

    public void setSuiteTicket(String suiteTicket) {
        this.suiteTicket = suiteTicket;
    }

    public String getSuiteSecret() {
        return suiteSecret;
    }

    public void setSuiteSecret(String suiteSecret) {
        this.suiteSecret = suiteSecret;
    }
}
