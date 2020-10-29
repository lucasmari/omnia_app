package com.lucas.omnia.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String uid;
    public String username;
    public String email;
    public boolean hasPhoto;
    public String city;
    public String about;
    public int subCount = 0;
    public Map<String, String> subs = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public User(String username, String email, int subCount) {
        this.username = username;
        this.email = email;
        this.subCount = subCount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setSubCount(int subCount) { this.subCount = subCount; }
}
