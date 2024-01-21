package com.husseinamro.exchange;

import java.util.prefs.Preferences;
public class Authentication {
    private static Authentication instance;
    private static final String TOKEN_KEY = "TOKEN";
    private String token;
    private Preferences pref;
    private Authentication() {
        pref = Preferences.userRoot().node(this.getClass().getName());
        token = pref.get(TOKEN_KEY, null);
    }
    static public Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }
    public String getToken() {
        return token;
    }
    public void saveToken(String token) {
        this.token = token;
        pref.put(TOKEN_KEY, token);
    }
    public void deleteToken() {
        this.token = null;
        pref.remove(TOKEN_KEY);
    }
}

