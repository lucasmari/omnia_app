package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Lucas on 10/10/2017.
 */

public class TabFragment2 extends PostListFragment {

    public TabFragment2() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Last 100 posts
        Query recentPostsQuery = databaseReference.child("posts").limitToFirst(100);

        return recentPostsQuery;
    }
}
