package com.assessment.translate.utils;

import org.apache.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtils {
    private static final Logger log = Logger.getLogger(EncryptionUtils.class);

    /**
     * 阿里巴巴翻译引擎参数编码
     *
     * @param text
     * @return
     */
    public static String percentEncode(String text) {
        String t = utf8Encode(text);
        return (t == null ? null : t.replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~"));
    }

    /**
     * UTF-8编码
     *
     * @param text
     * @return
     */
    public static String utf8Encode(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.toString());
        }
        return null;
    }

    /**
     * UTF-8解码
     *
     * @param text
     * @return
     */
    public static String utf8Decode(String text) {
        try {
            return URLDecoder.decode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.toString());
        }
        return null;
    }

    /**
     * md5加密
     *
     * @param string
     * @return md5加密后的字符串
     */
    public static String toMD5String(String string) {
        byte[] secretBytes = toMD5Bytes(string);
        if (secretBytes == null) {
            return null;
        }
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }

    /**
     * 进行base64编码
     *
     * @param string
     * @return base64编码后的字符串
     */
    public static String toBase64String(String string) {
        Base64.Encoder b64Encoder = Base64.getEncoder();
        return b64Encoder.encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 进行base64编码
     * @param bytes
     * @return base64编码后的字符串
     */
    public static String toBase64String(byte[] bytes) {
        Base64.Encoder b64Encoder = Base64.getEncoder();
        return b64Encoder.encodeToString(bytes);
    }

    /**
     * 计算 HMAC-SHA1
     *
     * @param data
     * @param key
     * @return
     */
    public static String toHMACSHA1String(String data, String key) {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = toBase64String(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.toString());
            return null;
        }
        return result;
    }

    /**
     * SHA-256加密
     *
     * @param string
     * @return
     */
    public static String toSHA256String(String string) {
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("SHA-256").digest(
                    string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            log.error(e.toString());
            return null;
        }
        StringBuilder sha256code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - sha256code.length(); i++) {
            sha256code.insert(0, "0");
        }
        return sha256code.toString();
    }

    /**
     * md5加密
     *
     * @param string
     * @return md5加密后的字节数组
     */
    private static byte[] toMD5Bytes(String string) {
        try {
            return MessageDigest.getInstance("md5").digest(
                    string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            log.error(e.toString());
            return null;
        }
    }
}
