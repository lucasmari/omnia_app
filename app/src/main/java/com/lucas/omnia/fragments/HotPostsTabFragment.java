package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class HotPostsTabFragment extends PostListFragment {

    public HotPostsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Top 100 posts
        return databaseReference.child("posts")
                .orderByChild("votesBalance").limitToFirst(100);
    }
}
