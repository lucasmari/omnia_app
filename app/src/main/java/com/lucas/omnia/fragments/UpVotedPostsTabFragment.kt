package com.lucas.omnia.fragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class UpVotedPostsTabFragment : PostListFragment() {
    override fun getQuery(databaseReference: DatabaseReference?): Query {
        // UpVoted last 100 posts
        return databaseReference!!.child("posts").orderByChild("upVotes/$uid").equalTo(true)
    }

    companion object {
        private const val TAG = "UpVotedPostsTabFragment"
    }
}