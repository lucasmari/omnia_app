package com.lucas.omnia.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityNewCommentBinding
import com.lucas.omnia.models.Comment
import com.lucas.omnia.models.Post
import com.lucas.omnia.models.User
import java.util.*

class NewCommentActivity : BaseActivity() {
    private var databaseRef: DatabaseReference? = null
    private var binding: ActivityNewCommentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCommentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference
        binding!!.newCommentFabSubmit.setOnClickListener { submitComment() }
    }

    private fun submitComment() {
        val body = binding!!.newCommentEtBody.text.toString()

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding!!.newCommentEtBody.error = REQUIRED
            return
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false)
        Toast.makeText(this, getString(R.string.new_comment_toast_commenting), Toast.LENGTH_SHORT).show()
        val userId = uid
        databaseRef!!.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user == null) {
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(this@NewCommentActivity,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            writeNewComment(userId, user.username, body)
                            incrementCommentsCount()
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
        binding!!.newCommentEtBody.isEnabled = enabled
        if (enabled) {
            binding!!.newCommentFabSubmit.show()
        } else {
            binding!!.newCommentFabSubmit.hide()
        }
    }

    private fun writeNewComment(userId: String, username: String?, body: String) {
        // Create new item_comment at /post-comments
        val key = CommentsActivity.commentsReference!!.push().key
        val comment = Comment(userId, username, body)
        val commentValues = comment.toMap()
        val childUpdates: MutableMap<String, Any> = HashMap()
        childUpdates["/post-comments/" + CommentsActivity.postKey + "/" + key] = commentValues
        databaseRef!!.updateChildren(childUpdates)

        // Set timestamp
        databaseRef!!.child("post-comments").child(CommentsActivity.postKey!!).child(key!!).child("timestamp").setValue(ServerValue.TIMESTAMP)
    }

    private fun incrementCommentsCount() {
        CommentsActivity.postReference!!.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Post::class.java)
                        ?: return Transaction.success(mutableData)

                // Increment commentsCount of item_post
                p.commentCount = p.commentCount + 1

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "CommentTransaction:onComplete:$databaseError")
            }
        })
    }

    companion object {
        private const val TAG = "NewCommentActivity"
        private const val REQUIRED = "Required"
    }
}