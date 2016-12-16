package com.mwb.framework.security.crypto.api;

public interface IPasswordManager {
    public String encrypt(String password, String salt);

    public boolean isValid(String password, String salt, String encryptedPwd);

    public String createSalt();
}
