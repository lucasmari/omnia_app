package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Lucas on 10/10/2017.
 */

public class MyPostsTabFragment extends PostListFragment {

    public MyPostsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // User last 100 posts
        return databaseReference.child("user-posts").child(getUid()).limitToFirst(100);
    }
}
