package com.kylog.barbacaoaapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_at")
    @Expose
    private String expiresAt;

    /**
     * No args constructor for use in serialization
     *
     */
    public Auth() {
    }

    /**
     *
     * @param accessToken
     * @param tokenType
     * @param expiresAt
     */
    public Auth(String accessToken, String tokenType, String expiresAt) {
        super();
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresAt() { return expiresAt; }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}