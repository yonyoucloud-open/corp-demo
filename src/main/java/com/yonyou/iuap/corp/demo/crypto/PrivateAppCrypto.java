package com.yonyou.iuap.corp.demo.crypto;


public class PrivateAppCrypto extends IsvEventCrypto {


    public PrivateAppCrypto(String appKey, String appSecret, String encodingAesKey) {
        super(appSecret, encodingAesKey, appKey);
    }

    public static PrivateAppCrypto newCrypto(String appKey, String appSecret) {
        String encodingAesKey = buildAesKeyFromSecret(appSecret);
        return new PrivateAppCrypto(appKey, appSecret, encodingAesKey);
    }

    public static String buildAesKeyFromSecret(String appSecret) {
        String encodingAesKey = appSecret;
        encodingAesKey = encodingAesKey.replaceAll("-", "");
        if (encodingAesKey.length() == 43) {
            return encodingAesKey;
        }

        if (encodingAesKey.length() > 43) {
            return encodingAesKey.substring(0, 43);
        }

        StringBuilder stringBuilder = new StringBuilder(encodingAesKey);
        while (stringBuilder.length() < 43) {
            stringBuilder.append("0");
        }

        return stringBuilder.toString();
    }
}
