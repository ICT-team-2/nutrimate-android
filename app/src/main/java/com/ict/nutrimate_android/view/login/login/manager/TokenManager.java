package com.ict.nutrimate_android.view.login.login.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class TokenManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String EXPIRATION_KEY = "expiration";

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isTokenExpired() {
        long expirationTime = sharedPreferences.getLong(EXPIRATION_KEY, 0);
        long currentTime = new Date().getTime();
        return currentTime > expirationTime;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(EXPIRATION_KEY);
        editor.apply();
        Log.d("TokenManager", "Logged out successfully");
    }
}
