package com.shinhan.spp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class CryptoUtil {
    private static String salt;

    public CryptoUtil(@Value("${secret.enc.salt}") String salt) {
        CryptoUtil.salt = salt;
    }

    public static String getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[20];

        //2. 난수 생성
        sr.nextBytes(salt);

        //3. byte To String (10진수 문자열로 변경)
        StringBuffer sb = new StringBuffer();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String encryptSha256(String password, String salt) throws NoSuchAlgorithmException {
        if(password != null && salt != null) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();

            md.update(salt.getBytes());
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

            return String.format("%064x", new BigInteger(1, digest));
        }
        else {
            return null;
        }
    }
}