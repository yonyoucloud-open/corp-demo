package com.yonyou.iuap.corp.demo.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * 加密加签解密验签核心类
 *
 * 注意：需要更新 JRE 中 JCE 无限制权限策略文件
 *
 * <ul>
 *     <li>JDK6 的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html</li>
 *     <li>JDK7 的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 *     <li>JDK8 的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html</li>
 * </ul>
 */
public class EventCrypto {

    private static Charset CHARSET = Charset.forName("utf-8");

    private static ObjectMapper mapper = new ObjectMapper();

    private byte[] aesKey;

    private String token;

    private String suiteKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventCrypto.class);

    /**
     * 构造函数
     *
     * @param token          开放平台上，开发者设置的token
     * @param encodingAesKey 开放平台上，开发者设置的EncodingAESKey
     * @param suiteKey       套件的 suiteKey
     * @throws CryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public EventCrypto(String token, String encodingAesKey, String suiteKey)
            throws CryptoException {
        if (encodingAesKey.length() != 43) {
            LOGGER.error("无效的 AES key");
            throw new CryptoException(ErrorCode.INVALID_AES_SYMMETRIC_KEY);
        }
        this.token = token;
        this.suiteKey = suiteKey;
        aesKey = Base64.getDecoder().decode(encodingAesKey + "=");
    }

    // 生成4个字节的网络字节序
    private byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    // 还原4个字节的网络字节序
    private int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    // 随机生成16位字符串
    public String getRandomStr() {
        String base =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws CryptoException aes 加密失败
     */

    public String encrypt(String randomStr, String text) throws CryptoException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] suiteKeyBytes = suiteKey.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + corpid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(suiteKeyBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            LOGGER.error("AES 加密失败，cause: {}", e.toString());
            throw new CryptoException(ErrorCode.AES_ENCRYPT_FAILED, e);
        }
    }


    /**
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws CryptoException aes解密失败
     */
    public String decrypt(String text) throws CryptoException {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv =
                    new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.getDecoder().decode(text);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            LOGGER.error("AES 解密失败, cause: {}", e.toString());
            throw new CryptoException(ErrorCode.AES_DECRYPT_FAILED, e);
        }

        String message, from_suiteKey;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和corpId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            message = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            from_suiteKey = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            LOGGER.error("无效的 AES key");
            throw new CryptoException(ErrorCode.INVALID_AES_SYMMETRIC_KEY, e);
        }

        // suiteKey 不相同的情况
        if (!from_suiteKey.equals(suiteKey)) {
            LOGGER.error("suiteKey 校验失败");
            throw new CryptoException(ErrorCode.INVALID_SUITE_KEY);
        }
        return message;
    }


    /**
     * 将要发送的消息加密加前，打包为要发送的格式
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成 json 格式</li>
     * </ol>
     *
     * @param msg       要发送的未加密消息体
     * @param timestamp unix 时间戳
     * @param nonce     随机串
     * @return 加密后的可以直接回复用户的密文，包括 msgSignature, timestamp, nonce,
     * encrypt 的 json 格式的字符串
     * @throws CryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String encryptMsg(String msg, long timestamp, String nonce) throws CryptoException {
        // 加密
        String encrypt = encrypt(getRandomStr(), msg);
        // 加签
        String signature = SHA1.getSHA1(token, String.valueOf(timestamp), nonce, encrypt);
        return holderToJsonStr(new EncryptionHolder(signature, timestamp, nonce, encrypt));
    }


    /**
     * 将要发送的消息加密加前，打包为要发送的格式
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成 json 格式</li>
     * </ol>
     *
     * @param msg 要发送的未加密消息体
     * @return 加密后的可以直接回复用户的密文，包括 msgSignature, timestamp, nonce,
     * encrypt 的 json 格式的字符串
     * @throws CryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String encryptMsg(String msg) throws CryptoException {
        return encryptMsg(msg, System.currentTimeMillis(), getRandomStr());
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取 json 中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应 msgSignature
     * @param timestamp    时间戳，对应 timestamp
     * @param nonce        随机串，对应 nonce
     * @param encrypt      密文，对应 encrypt
     * @return 解密后的原文
     * @throws CryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(String msgSignature, long timestamp, String nonce, String encrypt) throws CryptoException {
        // 验签
        String signature = SHA1.getSHA1(token, String.valueOf(timestamp), nonce, encrypt);
        if (!signature.equals(msgSignature)) {
            LOGGER.error("签名校验失败！");
            throw new CryptoException(ErrorCode.INVALID_SIGNATURE);
        }
        // 解密
        return decrypt(encrypt);
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取 json 中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param jsonMsg 收到的加密加签的 json String
     * @return 解密后的原文
     * @throws CryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(String jsonMsg) throws CryptoException {
        EncryptionHolder holder = jsonToHolder(jsonMsg);
        return decryptMsg(holder.getMsgSignature(), holder.getTimestamp(), holder.getNonce(), holder.getEncrypt());
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取 json 中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param holder 收到的携带密文及加密信息的 {@link EncryptionHolder}
     * @return 解密后的原文
     * @throws CryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(EncryptionHolder holder) throws CryptoException {
        return decryptMsg(holder.getMsgSignature(), holder.getTimestamp(), holder.getNonce(), holder.getEncrypt());
    }

    public String holderToJsonStr(EncryptionHolder holder) throws CryptoException {
        try {
            return mapper.writeValueAsString(holder);
        } catch (JsonProcessingException e) {
            LOGGER.error("加密消息序列化失败");
            throw new CryptoException(ErrorCode.ENCRYPT_MESSAGE_SERIALIZE_FAILED, e);
        }
    }

    public EncryptionHolder jsonToHolder(String jsonStr) throws CryptoException {
        try {
            return mapper.readValue(jsonStr, EncryptionHolder.class);
        } catch (IOException e) {
            throw new CryptoException(ErrorCode.ENCRYPT_MESSAGE_DESERIALIZE_FAILED, e);
        }
    }

}
