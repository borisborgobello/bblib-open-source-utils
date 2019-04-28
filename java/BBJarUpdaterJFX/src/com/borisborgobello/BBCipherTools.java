package com.borisborgobello;

/*  */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * 
 * @author boris
 * Toolbox for encodings and ciphering (MD5, AES128, ...)
 */

public class BBCipherTools {
    private static final String TAG = BBCipherTools.class.getSimpleName();

    public static byte[] getFullkeyAES128FromPartialKey(String partialKey) {
            if (partialKey.length() <= 0) return null;
            String currentString = partialKey;
            while (currentString.length()*8 < 128) {
                    currentString = currentString.concat(partialKey);
            }
            byte[] intermediateKey = currentString.getBytes();
            byte[] fullkey = new byte[128/8];
            for (int i = 0; i < fullkey.length; i ++) {
                    fullkey[i] = intermediateKey[i];
            }
            return fullkey;
    }

    public static boolean isCorrectFullKey(byte[] clearFullKey, byte[] encipheredFullKeyToMatch) {
            // Enter clear key into model sKeySpec
            SecretKeySpec skeySpec = new SecretKeySpec(clearFullKey, "AES");

            // Cipher clear key with clear key
            Cipher cipher;
            try {
                    cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

                    // Encrypt clearKey
                    byte[] encipheredFullkey = cipher.doFinal(clearFullKey);
                    if (encipheredFullkey.length != encipheredFullKeyToMatch.length) return false;

                    for (int i = 0; i < encipheredFullKeyToMatch.length; i++) {
                            if (encipheredFullKeyToMatch[i] != encipheredFullkey[i]) {
                                    return false;
                            }
                    }
                    return true;
            } catch (Exception e) {}
            return false;
    }

    public static boolean isCorrectHalfKey(String clearHalfKey, byte[] encipheredFullKeyToMatch) {
            return isCorrectFullKey(getFullkeyAES128FromPartialKey(clearHalfKey), encipheredFullKeyToMatch);
    }

    public static byte[] cryptAES128(byte[] clearFullkey, byte[] data) {
            if (data == null || data.length == 0) return null;
            try {
                    SecretKeySpec skeySpec = new SecretKeySpec(clearFullkey, "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

                    return cipher.doFinal(data);
            } catch (Exception e) {
                    return null;
            }
    }

    public static byte[] decryptAES128(byte[] clearFullkey, byte[] data) {
            if (data == null || data.length == 0) return null;
            try {
                    SecretKeySpec skeySpec = new SecretKeySpec(clearFullkey, "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, skeySpec);

                    return cipher.doFinal(data);
            } catch (Exception e) {
                    return null;
            }
    }

    public static String asHex(byte[] data) {
            StringBuffer strbuf = new StringBuffer(data.length * 2);
            int i;

            for (i = 0; i < data.length; i++) {
                    if (((int) data[i] & 0xff) < 0x10)
                            strbuf.append("0");

                    strbuf.append(Long.toString((int) data[i] & 0xff, 16));
            }

            return strbuf.toString();
    }
    public static String asHex2(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }

    public static String md5(String string) {
        try {
                    return md5(string.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
            }
    }

    public static String md5(byte[] data) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }

    public static String sha256(String password) {
        MessageDigest digest=null;
        String hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes());

            hash = asHex(digest.digest());

            //BBLog.i(TAG, "result is " + hash);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return hash;
    }
}
