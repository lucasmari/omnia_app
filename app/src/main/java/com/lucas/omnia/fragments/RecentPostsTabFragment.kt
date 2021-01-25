package com.lucas.omnia.fragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class RecentPostsTabFragment : PostListFragment() {
    override fun getQuery(databaseReference: DatabaseReference?): Query {
        // Last 100 posts
        return databaseReference!!.child("posts").limitToFirst(100)
    }
}