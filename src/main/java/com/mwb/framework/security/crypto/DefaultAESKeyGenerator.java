package com.mwb.framework.security.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;

public class DefaultAESKeyGenerator {
    private static final String CIPHER_ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String PRNG_ALGORITHM = "SHA1PRNG";

    private String keyStoreFileName;
    private String alias;
    private char[] keyPassword;
    private char[] keyStorePassword;

    public DefaultAESKeyGenerator(String keyStoreFileName, String alias, String keyPassword, String keyStorePassword) {
        this.keyStoreFileName = keyStoreFileName;
        this.alias = alias;
        this.keyPassword = keyPassword.toCharArray();
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    public Key createKey() throws Exception {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null, null);

        KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        Key key = keyGenerator.generateKey();

        keyStore.setKeyEntry(alias, key, keyPassword, null);
        keyStore.store(new FileOutputStream(keyStoreFileName), keyStorePassword);

        return key;
    }

    private boolean testKey() {
        try {
            byte[] ivBytes = new byte[16];

            SecureRandom.getInstance(PRNG_ALGORITHM).nextBytes(ivBytes);

            System.out.println("The random initialization vector:");

            for (byte i : ivBytes) {
                System.out.println(i + " ");
            }

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            String sampleString = "There are TESTING chars: abcdEFGHI1234)(*&^";

            System.out.println("\nSample String:    " + sampleString);

            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(new FileInputStream(keyStoreFileName), keyStorePassword);
            Key key = keyStore.getKey(alias, keyPassword);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] cipherText = cipher.doFinal(sampleString.getBytes("UTF8"));

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] decryptedText = cipher.doFinal(cipherText);

            String decryptedString = new String(decryptedText, "UTF8");
            System.out.println("Decrypted String: " + decryptedString);

            if (sampleString.equals(decryptedString)) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Input args: KeyStoreFileName, Alias, KeyPassword, KeyStorePassword");
        } else {
            DefaultAESKeyGenerator keyGenerator = new DefaultAESKeyGenerator(args[0], args[1], args[2], args[3]);

            try {
                System.out.println("Generating the key ...");
                keyGenerator.createKey();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to generate the key: " + e);
            }

            System.out.println("Testing the key ...");
            boolean success = keyGenerator.testKey();

            if (success) {
                System.out.println("Key is working fine!");
            } else {
                System.out.println("Invalid key!");
            }
        }
    }
}
