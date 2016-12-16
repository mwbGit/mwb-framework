package com.mwb.framework.security.crypto;

import com.mwb.framework.log.Log;
import com.mwb.framework.security.crypto.api.IDataCryptor;
import com.mwb.framework.security.crypto.api.IKeyAccessor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DefaultDataCryptor implements IDataCryptor {
    private Log LOG = Log.getLog(DefaultDataCryptor.class);

    private static final int DIGEST_LENGTH = 20;
    private static final int MAX_DATA_LENGTH = 128;
    private static final String DIGEST_ALGORITHM = "SHA1";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String TEXT_ENCODING = "UTF-8";
    private static final String PRNG_ALGORITHM = "SHA1PRNG";
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int INTT_VECTOR_LENGTH = 16;

    @Resource
    private IKeyAccessor keyAccessor;

    private SecureRandom prng;
    private Key key;

    public void init() throws NoSuchAlgorithmException {
        LOG.info("Initializing DefaultDataCryptor.");

        prng = SecureRandom.getInstance(PRNG_ALGORITHM);
        key = keyAccessor.getKey();

        LOG.info("Got the random number generator and key.");
    }

    @Override
    public String encrypt(String data) {
        try {
            if (StringUtils.isBlank(data)) {
                throw new IllegalArgumentException("Data can not be empty.");
            }

            ByteBuffer buffer = ByteBuffer.allocate(MAX_DATA_LENGTH + DIGEST_LENGTH);

            putString(buffer, data);

            byte[] digest = digest(buffer.array(), buffer.position());
            buffer.put(digest);

            byte[] ivBytes = getInitVector();
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] cipherText = cipher.doFinal(buffer.array(), 0, buffer.position());

            byte[] fullText = new byte[ivBytes.length + cipherText.length];

            System.arraycopy(ivBytes, 0, fullText, 0, ivBytes.length);
            System.arraycopy(cipherText, 0, fullText, ivBytes.length, cipherText.length);

            byte[] encoded = Base64.encodeBase64(fullText);
            return new String(encoded, TEXT_ENCODING);
        } catch (Exception e) {
            LOG.error("Unable to encrypt data", e);
            throw new RuntimeException("Unable to encrypt data", e);
        }

    }

    @Override
    public String decrypt(String data) {
        try {
            if (StringUtils.isBlank(data)) {
                throw new IllegalArgumentException("Data can not be empty.");
            }

            byte[] fullText = Base64.decodeBase64(data.getBytes(TEXT_ENCODING));
            byte[] ivBytes = new byte[INTT_VECTOR_LENGTH];
            byte[] cipherText = new byte[fullText.length - ivBytes.length];

            System.arraycopy(fullText, 0, ivBytes, 0, ivBytes.length);
            System.arraycopy(fullText, ivBytes.length, cipherText, 0, cipherText.length);

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] plainText = cipher.doFinal(cipherText);
            ByteBuffer buffer = ByteBuffer.wrap(plainText);

            int digestOffset = plainText.length - DIGEST_LENGTH;
            byte[] digest = digest(buffer.array(), digestOffset);
            for (int i = 0; i < digest.length; ++i) {
                if (plainText[digestOffset + i] != digest[i]) {
                    throw new RuntimeException(
                            "Failed to verify signature, likely malformed or corrupt encrypted data!");
                }
            }

            return getString(buffer);

        } catch (Exception e) {
            LOG.error("Unable to decrypt data", e);
            throw new RuntimeException("Unable to decrypt data", e);
        }
    }

    private byte[] getInitVector() {
        byte[] ivBytes = new byte[INTT_VECTOR_LENGTH];
        prng.nextBytes(ivBytes);
        return ivBytes;
    }

    private byte[] digest(byte[] buffer, int length) {
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            md.update(buffer, 0, length);
            byte[] digest = md.digest();
            return digest;
        } catch (Exception e) {
            String errorMsg = "Unable to compute digest for ticket.";
            LOG.error(errorMsg, e);

            throw new RuntimeException(errorMsg, e);
        }
    }

    private String getString(ByteBuffer buffer) {
        try {
            int length = buffer.getInt();
            if (length > 0) {
                byte[] bytes = new byte[length];
                buffer.get(bytes);
                return new String(bytes, TEXT_ENCODING);
            }

            return null;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Missing text encoding for " + TEXT_ENCODING, e);
        }
    }

    private void putString(ByteBuffer buffer, String s) {
        try {
            byte[] bytes = (s != null ? s.getBytes(TEXT_ENCODING) : EMPTY_BYTE_ARRAY);
            buffer.putInt(bytes.length);
            if (bytes.length > 0) {
                buffer.put(bytes);
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Missing text encoding for " + TEXT_ENCODING, e);
        }
    }
}
