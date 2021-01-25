package com.lucas.omnia.fragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class HotPostsTabFragment : PostListFragment() {
    override fun getQuery(databaseReference: DatabaseReference?): Query {
        // Top 100 posts
        return databaseReference!!.child("posts")
                .orderByChild("votesBalance").limitToFirst(100)
    }
}