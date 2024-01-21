package com.husseinamro.exchange.api.model;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("token")
    String token;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
