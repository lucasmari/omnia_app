package com.lucas.omnia.fragments;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Lucas on 10/10/2017.
 */

public class TabFragment1 extends PostListFragment {

    public TabFragment1() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("posts")
                .limitToFirst(100);

        return recentPostsQuery;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
