package com.lucas.omnia.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Lucas on 27/10/2017.
 */

public class UpvotedPostsTabFragment extends PostListFragment {

    public UpvotedPostsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Voted last 100 posts
        Query votedPostsQuery = databaseReference.child("posts").child("upVotes")
                .orderByChild(getUid()).equalTo(true).limitToFirst(100);

        return votedPostsQuery;
    }
}
