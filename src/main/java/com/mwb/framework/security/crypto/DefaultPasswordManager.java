package com.mwb.framework.security.crypto;

import com.mwb.framework.security.crypto.api.IPasswordManager;
import com.mwb.framework.util.MD5Utility;
import com.mwb.framework.util.RandomGenerator;
import org.apache.commons.lang.StringUtils;

public class DefaultPasswordManager implements IPasswordManager {

    @Override
    public String encrypt(String password, String salt) {
        String encryptedPwd = MD5Utility.digest(MD5Utility.digest(password) + salt);

        return encryptedPwd;
    }

    @Override
    public boolean isValid(String password, String salt, String encryptedPwd) {
        return StringUtils.equals(encrypt(password, salt), encryptedPwd);
    }

    @Override
    public String createSalt() {
        return RandomGenerator.getGlobalUniqueId();
    }

    public static void main(String argv[]) {
        IPasswordManager manager = new DefaultPasswordManager();

        String salt = manager.createSalt();
        String encrypedPwd = manager.encrypt("password", salt);

        System.out.println("Salt: " + salt);
        System.out.println("Encrypted password: " + encrypedPwd);
    }
}
