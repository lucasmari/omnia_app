package com.lucas.omnia.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityNewPostBinding
import com.lucas.omnia.models.Post
import com.lucas.omnia.models.User
import java.util.*

class NewPostActivity : BaseActivity() {
    private var databaseRef: DatabaseReference? = null
    private var binding: ActivityNewPostBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference
        binding!!.newPostFabSubmit.setOnClickListener { submitPost() }
    }

    private fun submitPost() {
        val title = binding!!.newPostEtTitle.text.toString()
        val body = binding!!.newPostEtBody.text.toString()

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding!!.newPostEtTitle.error = REQUIRED
            return
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding!!.newPostEtBody.error = REQUIRED
            return
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false)
        Toast.makeText(this, getString(R.string.new_post_toast_posting), Toast.LENGTH_SHORT).show()
        val userId = uid
        databaseRef!!.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user == null) {
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(this@NewPostActivity,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            writeNewPost(userId, user.username, title, body)
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
        binding!!.newPostEtTitle.isEnabled = enabled
        binding!!.newPostEtBody.isEnabled = enabled
        if (enabled) {
            binding!!.newPostFabSubmit.show()
        } else {
            binding!!.newPostFabSubmit.hide()
        }
    }

    private fun writeNewPost(userId: String, username: String?, title: String, body: String) {
        // Create new item_post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = databaseRef!!.child("posts").push().key
        val post = Post(userId, username, title, body)
        val postValues = post.toMap()
        val childUpdates: MutableMap<String, Any> = HashMap()
        childUpdates["/posts/$key"] = postValues
        childUpdates["/user-posts/$userId/$key"] = postValues
        databaseRef!!.updateChildren(childUpdates)

        // Set timestamp
        databaseRef!!.child("posts").child(key!!).child("timestamp").setValue(ServerValue.TIMESTAMP)
        databaseRef!!.child("user-posts").child(userId).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP)
    }

    companion object {
        private const val TAG = "NewPostActivity"
        private const val REQUIRED = "Required"
    }
}