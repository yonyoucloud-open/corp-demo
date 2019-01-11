package com.yonyou.iuap.corp.demo.crypto;

/**
 * 自建应用加密加签解密验签核心类
 *
 * 注意：需要更新 JRE 中 JCE 无限制权限策略文件
 *
 * <ul>
 *     <li>JDK6 的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html</li>
 *     <li>JDK7 的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 *     <li>JDK8 的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html</li>
 * </ul>
 */
public class PrivateAppCrypto extends EventCrypto {


    private PrivateAppCrypto(String appKey, String appSecret, String encodingAesKey) {
        super(appSecret, encodingAesKey, appKey);
    }

    /**
     * 构建自建应用解密验签用的工具类
     *
     * @param appKey    自建应用的 appKey
     * @param appSecret 自建应用的 appSecret
     * @return
     */
    public static PrivateAppCrypto newCrypto(String appKey, String appSecret) {
        String encodingAesKey = buildAesKeyFromSecret(appSecret);
        return new PrivateAppCrypto(appKey, appSecret, encodingAesKey);
    }

    /**
     * AES key 构建逻辑，使用 appSecret 移除掉 "-"，然后拼接 "0"，长度 43 位
     *
     * <pre>
     *     1. appSecret.replaceAll("-", "")
     *     2. appSecret + "00000000"，保证总长度 43 位
     * </pre>
     *
     *
     * @param appSecret 自建应用的 appSecret
     * @return
     */
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
