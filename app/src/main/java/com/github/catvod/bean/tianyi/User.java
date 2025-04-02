package com.github.catvod.bean.tianyi;

import com.google.gson.annotations.SerializedName;

public class User {
    public User(String cookie) {
        this.cookie = cookie;
    }

    public User(String sessionKey, String sessionSecret, String refreshToken, String accessToken) {
        this.sessionKey = sessionKey;
        this.sessionSecret = sessionSecret;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    @SerializedName("cookie")
    private String cookie;

    @SerializedName("sessionKey")
    private String sessionKey;
    @SerializedName("sessionSecret")
    private String sessionSecret;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("accessToken")
    private String accessToken;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionSecret() {
        return sessionSecret;
    }

    public void setSessionSecret(String sessionSecret) {
        this.sessionSecret = sessionSecret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public static User objectFrom(String cookie) {
        return new User(cookie);
    }


    public void clean() {
        this.cookie = "";

    }
}
