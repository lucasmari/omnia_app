package com.lucas.omnia.utils;

import android.content.SharedPreferences;

/**
 *  Helper class to manage access to {@link SharedPreferences}.
 */
public class SharedPreferencesHelper {

    // Keys for saving values in SharedPreferences.
    public static final String KEY_ID = "key_id";
    public static final String KEY_USERNAME = "key_username";

    // The injected SharedPreferences implementation to use for persistence.
    private final SharedPreferences mSharedPreferences;

    /**
     * Constructor with dependency injection.
     *
     * @param sharedPreferences The {@link SharedPreferences} that will be used in this DAO.
     */
    public SharedPreferencesHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    /**
     * Saves the given {@link SharedPreferenceEntry} that contains the user's settings to
     * {@link SharedPreferences}.
     *
     * @param sharedPreferenceEntry contains data to save to {@link SharedPreferences}.
     * @return {@code true} if writing to {@link SharedPreferences} succeeded. {@code false}
     *         otherwise.
     */
    public boolean saveUserInfo(SharedPreferenceEntry sharedPreferenceEntry){
        // Start a SharedPreferences transaction.
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_ID, sharedPreferenceEntry.getUid());
        editor.putString(KEY_USERNAME, sharedPreferenceEntry.getUsername());

        // Commit changes to SharedPreferences.
        return editor.commit();
    }

    /**
     * Retrieves the {@link SharedPreferenceEntry} containing the user's personal information from
     * {@link SharedPreferences}.
     *
     * @return the Retrieved {@link SharedPreferenceEntry}.
     */
    public SharedPreferenceEntry getUserInfo() {
        // Get data from the SharedPreferences.
        String uid = mSharedPreferences.getString(KEY_ID, "");
        String username = mSharedPreferences.getString(KEY_USERNAME, "");

        // Create and fill a SharedPreferenceEntry model object.
        return new SharedPreferenceEntry(uid, username);
    }
}
