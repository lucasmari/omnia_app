package com.lucas.omnia.fragments;

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
        // Top 100 posts
        Query topPostsQuery = databaseReference.child("posts")
                .orderByChild("upVoteCount").limitToFirst(100);

        return topPostsQuery;
    }
}
