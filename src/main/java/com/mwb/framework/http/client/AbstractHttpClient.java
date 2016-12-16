package com.mwb.framework.http.client;

import java.util.Map;

public abstract class AbstractHttpClient {
    private SimpleHttpClient httpClient;

    private boolean mock;
    
    private int maxConnection;
    private int connectionTimeout;
    private int socketTimeout;
    
    public void init() throws Exception {
        httpClient = new SimpleHttpClient(maxConnection, connectionTimeout, socketTimeout);
    }

    public String get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return httpClient.get(url, params, headers);
    }
    
    public String post(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return httpClient.post(url, params, headers);
    } 
    
    public String post2XX(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return httpClient.post2XX(url, params, headers);
    }
    
    public String postMutipart(String url, Map<String, Object> params) throws Exception {
        return httpClient.postMutipart(url, params);
    } 
    
    public String post(String url, String jsonStr) throws Exception {
        return httpClient.post(url, jsonStr);
    }
    
    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

}
