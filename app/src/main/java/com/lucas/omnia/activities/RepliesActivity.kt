package com.lucas.omnia.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.lucas.omnia.R
import com.lucas.omnia.fragments.ReplyListFragment

class RepliesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replies)
        val toolbar = findViewById<Toolbar>(R.id.comments_tb)
        setSupportActionBar(toolbar)

        // Get comment key from intent
        commentKey = intent.getStringExtra(EXTRA_COMMENT_KEY)
        requireNotNull(commentKey) { "Must pass EXTRA_COMMENT_KEY" }

        // Initialize Database
        commentReference = databaseReference.child("post-comments").child(CommentsActivity.postKey!!).child(commentKey!!)
        repliesReference = databaseReference.child("comment-replies").child(commentKey!!)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.replies_fcv, ReplyListFragment())
                .commit()
    }

    companion object {
        const val EXTRA_COMMENT_KEY = "comment_key"
        @JvmField
        var commentKey: String? = null
        @JvmField
        var commentReference: DatabaseReference? = null
        var repliesReference: DatabaseReference? = null
    }
}