package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class UserPostsTabFragment extends PostListFragment {

    public String userKey;

    public UserPostsTabFragment(String userKey) {
        this.userKey = userKey;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // User last 100 posts
        return databaseReference.child("user-posts").child(userKey).limitToFirst(100);
    }
}

