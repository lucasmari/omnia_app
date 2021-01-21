package com.lucas.omnia.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityNewReplyBinding
import com.lucas.omnia.models.Comment
import com.lucas.omnia.models.Reply
import com.lucas.omnia.models.User
import java.util.*

class NewReplyActivity : BaseActivity() {
    private var commentKey: String? = null
    private var databaseRef: DatabaseReference? = null
    private var commentReference: DatabaseReference? = null
    private var author: String? = null
    private var binding: ActivityNewReplyBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReplyBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference

        // Get comment key from intent
        commentKey = intent.getStringExtra(EXTRA_COMMENT_KEY)
        requireNotNull(commentKey) { "Must pass EXTRA_COMMENT_KEY" }
        commentReference = databaseRef!!.child("post-comments").child(CommentsActivity.postKey!!).child(commentKey!!)
        if (intent.hasExtra(EXTRA_REPLY_KEY)) {
            // Get reply key from intent
            val replyKey = intent.getStringExtra(EXTRA_REPLY_KEY)
                    ?: throw IllegalArgumentException("Must pass EXTRA_REPLY_KEY")
            val replyReference = databaseRef!!.child("comment-replies").child(commentKey!!).child(replyKey)
            replyReference.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val reply = dataSnapshot.getValue(Reply::class.java)
                            author = reply!!.author
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "loadReply:onCancelled", databaseError.toException())
                            setEditingEnabled(true)
                        }
                    })
        } else {
            commentReference!!.addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val comment = dataSnapshot.getValue(Comment::class.java)
                            author = comment!!.author
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "loadReply:onCancelled", databaseError.toException())
                            setEditingEnabled(true)
                        }
                    })
        }
        binding!!.newReplyFabSubmit.setOnClickListener { submitReply() }
    }

    private fun submitReply() {
        val body = "@" + author + " " + binding!!.newReplyEtBody.text.toString()

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding!!.newReplyEtBody.error = REQUIRED
            return
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false)
        Toast.makeText(this, getString(R.string.new_reply_toast_replying), Toast.LENGTH_SHORT).show()
        val userId = uid
        databaseRef!!.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user == null) {
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(this@NewReplyActivity,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            writeNewReply(userId, user.username, body)
                            incrementRepliesCount()
                        }
                        setEditingEnabled(true)
                        finish()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                        setEditingEnabled(true)
                    }
                })
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding!!.newReplyEtBody.isEnabled = enabled
        if (enabled) {
            binding!!.newReplyFabSubmit.show()
        } else {
            binding!!.newReplyFabSubmit.hide()
        }
    }

    private fun writeNewReply(userId: String, username: String?, body: String) {
        // Create new item_reply at /comment-replies
        val key = databaseRef!!.child("comment-replies").child(commentKey!!).push().key
        val reply = Reply(userId, username, body)
        val replyValues = reply.toMap()
        val childUpdates: MutableMap<String, Any> = HashMap()
        childUpdates["/comment-replies/$commentKey/$key"] = replyValues
        databaseRef!!.updateChildren(childUpdates)

        // Set timestamp
        databaseRef!!.child("comment-replies").child(commentKey!!).child(key!!).child("timestamp").setValue(ServerValue.TIMESTAMP)
    }

    private fun incrementRepliesCount() {
        commentReference!!.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val c = mutableData.getValue(Comment::class.java)
                        ?: return Transaction.success(mutableData)

                // Increment commentsCount of item_post
                c.replyCount = c.replyCount + 1

                // Set value and report transaction success
                mutableData.value = c
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "ReplyTransaction:onComplete:$databaseError")
            }
        })
    }

    companion object {
        const val EXTRA_COMMENT_KEY = "comment_key"
        const val EXTRA_REPLY_KEY = "reply_key"
        private const val TAG = "NewReplyActivity"
        private const val REQUIRED = "Required"
    }
}