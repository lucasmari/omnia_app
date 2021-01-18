package com.lucas.omnia.utils;

import com.lucas.omnia.models.User;

public class SharedPreferenceEntry {
    // ID of the user.
    private final String uid;

    // Name of the user.
    private final String username;

    public SharedPreferenceEntry(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }
}
