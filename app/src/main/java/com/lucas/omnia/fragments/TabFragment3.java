package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Lucas on 10/10/2017.
 */

public class TabFragment3 extends PostListFragment {

    public TabFragment3() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // User last 100 posts
        Query myPostsQuery = databaseReference.child("user-posts").child(getUid()).limitToFirst(100);

        return myPostsQuery;
    }
}
