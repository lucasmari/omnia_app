package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentPostsTabFragment extends PostListFragment {

    public RecentPostsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Last 100 posts
        return databaseReference.child("posts").limitToFirst(100);
    }
}
