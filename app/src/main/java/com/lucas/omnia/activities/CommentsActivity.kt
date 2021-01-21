package com.lucas.omnia.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.lucas.omnia.R
import com.lucas.omnia.fragments.CommentListFragment

class CommentsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val toolbar = findViewById<Toolbar>(R.id.comments_tb)
        setSupportActionBar(toolbar)

        // Get post key from intent
        postKey = intent.getStringExtra(EXTRA_POST_KEY)
        requireNotNull(postKey) { "Must pass EXTRA_POST_KEY" }

        // Initialize Database
        postReference = databaseReference.child("posts").child(postKey!!)
        commentsReference = databaseReference.child("post-comments").child(postKey!!)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.comments_fcv, CommentListFragment())
                .commit()
        val addFab: FloatingActionButton? = findViewById(R.id.comments_fab_add)
        addFab?.setOnClickListener { v: View -> startActivity(Intent(v.context, NewCommentActivity::class.java)) }
    }

    companion object {
        const val EXTRA_POST_KEY = "post_key"
        @JvmField
        var postKey: String? = null
        @JvmField
        var postReference: DatabaseReference? = null
        @JvmField
        var commentsReference: DatabaseReference? = null
    }
}