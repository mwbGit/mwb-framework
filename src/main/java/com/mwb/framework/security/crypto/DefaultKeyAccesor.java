package com.mwb.framework.security.crypto;

import com.mwb.framework.log.Log;
import com.mwb.framework.security.crypto.api.IKeyAccessor;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;

public class DefaultKeyAccesor implements IKeyAccessor {
    private Log LOG = Log.getLog(DefaultKeyAccesor.class);

    private static final String KEY_ALIAS = "PlatformKey";
    private static final String KEY_STORE_TYPE = "JCEKS";
    private static final String KEY_STORE_PATH = "security/";

    private String keyStoreFile;
    private String keyStorePassword;
    private String keyPassword;

    private Key key;

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public void init() throws Exception {
        LOG.info("Initializing the KeyAccessorService.");
        LOG.info("Using the default JCE provider.");

        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        InputStream is = getClass().getClassLoader().getResourceAsStream(KEY_STORE_PATH + keyStoreFile);
        keyStore.load(is, keyStorePassword.toCharArray());

        if (keyStore.containsAlias(KEY_ALIAS)) {
            key = keyStore.getKey(KEY_ALIAS, keyPassword.toCharArray());
            LOG.info("Key loading success.");
        } else {
            throw new RuntimeException("Specified alias not found in KeyStore.");
        }
    }

    @Override
    public Key getKey() {
        return key;
    }

}
