package com.hw.books_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ApiKeyDataStore {

    private static final String PREFS_NAME = "api_key_prefs";
    private static final String API_KEY = "api_key";
    private final SharedPreferences sharedPreferences;

    public ApiKeyDataStore(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getApiKey() {
        return sharedPreferences.getString(API_KEY, "");
    }

    public void saveApiKey(String apiKey) {
        // REQUIREMENT: 10.2 SharedPreferences
        sharedPreferences.edit().putString(API_KEY, apiKey).apply();
    }

    public boolean hasApiKey() {
        return sharedPreferences.contains(API_KEY);
    }
}
