package com.example.space.ravenmessenger.encryption;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CipherHandler {

    private static final String SEED_KEY = "#R@VeN % Me$$@ge % UnBrE@K@Ble!#";
    private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(SEED_KEY.getBytes(), "AES");
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static Cipher cipherE, cipherD;


    public static String encrypt(final String message) {
        try {
            cipherE = Cipher.getInstance(ALGORITHM);
            cipherE.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC);

            byte[] encryptedBytes = cipherE.doFinal(message.getBytes());
            String base64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            return base64;
        } catch (Exception e) {
            e.getMessage();
        }
        return message;
    }

    public static String decrypt(final String message) {
        try {
            cipherD = Cipher.getInstance(ALGORITHM);
            cipherD.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC);
            byte[] raw = Base64.decode(message, Base64.DEFAULT);

            byte[] decryptedBytes = cipherD.doFinal(raw);
            String decryptedMessage = new String(decryptedBytes, "UTF8");
            return decryptedMessage;
        } catch (Exception e) {
            e.getMessage();
        }
        return message;
    }

}
