package com.lucas.omnia.utils

import android.content.SharedPreferences

/**
 * Helper class to manage access to [SharedPreferences].
 *
 * @param sharedPreferences The injected [SharedPreferences] that will be used in this DAO.
 */
class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {

    /**
     * Retrieves the [SharedPreferenceEntry] containing the user's personal information from
     * [SharedPreferences].
     *
     * @return the Retrieved [SharedPreferenceEntry].
     */
    // Get data from the SharedPreferences.
    // Create and fill a SharedPreferenceEntry model object.
    fun getPersonalInfo(): SharedPreferenceEntry {
        val uid = sharedPreferences.getString(KEY_ID, "")
        val username = sharedPreferences.getString(KEY_USERNAME, "")
        return SharedPreferenceEntry(uid, username)
    }

    /**
     * Saves the given [SharedPreferenceEntry] that contains the user's settings to
     * [SharedPreferences].
     *
     * @param sharedPreferenceEntry contains data to save to [SharedPreferences].
     * @return `true` if writing to [SharedPreferences] succeeded, `false` otherwise.
     */
    fun savePersonalInfo(sharedPreferenceEntry: SharedPreferenceEntry): Boolean {
        // Start a SharedPreferences transaction.
        val editor = sharedPreferences.edit().apply() {
            putString(KEY_ID, sharedPreferenceEntry.uid)
            putString(KEY_USERNAME, sharedPreferenceEntry.username)
        }

        // Commit changes to SharedPreferences.
        return editor.commit()
    }

    companion object {
        // Keys for saving values in SharedPreferences.
        const val KEY_ID = "key_id"
        const val KEY_USERNAME = "key_username"
    }
}