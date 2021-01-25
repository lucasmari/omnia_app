package com.lucas.omnia.fragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class MyPostsTabFragment : PostListFragment() {
    override fun getQuery(databaseReference: DatabaseReference?): Query {
        // User last 100 posts
        return databaseReference!!.child("user-posts").child(uid).limitToFirst(100)
    }
}