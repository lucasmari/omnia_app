package com.lucas.omnia.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityEditPostBinding
import com.lucas.omnia.models.Post
import com.lucas.omnia.models.User

class EditPostActivity : BaseActivity() {
    private var binding: ActivityEditPostBinding? = null
    private var postKey: String? = null
    private var databaseRef: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference

        // Get item_post key from intent
        postKey = intent.getStringExtra(EXTRA_POST_KEY)
        requireNotNull(postKey) { "Must pass EXTRA_POST_KEY" }

        // Initialize Database
        val postReference = FirebaseDatabase.getInstance().reference
                .child("posts").child(postKey!!)
        postReference.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val post = dataSnapshot.getValue(Post::class.java)
                        binding!!.editPostEtTitle.setText(post!!.title)
                        binding!!.editPostEtBody.setText(post.body)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException())
                        setEditingEnabled(true)
                    }
                })
        binding!!.editPostFabSubmit.setOnClickListener { submitPost() }
    }

    private fun submitPost() {
        val title = binding!!.editPostEtTitle.text.toString()
        val body = binding!!.editPostEtBody.text.toString()

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding!!.editPostEtTitle.error = REQUIRED
            return
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding!!.editPostEtBody.error = REQUIRED
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
                            Toast.makeText(this@EditPostActivity,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            updatePost(userId, title, body)
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
        binding!!.editPostEtTitle.isEnabled = enabled
        binding!!.editPostEtBody.isEnabled = enabled
        if (enabled) {
            binding!!.editPostFabSubmit.show()
        } else {
            binding!!.editPostFabSubmit.hide()
        }
    }

    private fun updatePost(userId: String, title: String, body: String) {
        // Update item_post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        databaseRef!!.child("posts").child(postKey!!).child("title").setValue(title)
        databaseRef!!.child("posts").child(postKey!!).child("body").setValue(body)
        databaseRef!!.child("posts").child(postKey!!).child("edited").setValue(true)
        databaseRef!!.child("user-posts").child(userId).child(postKey!!).child("title").setValue(title)
        databaseRef!!.child("user-posts").child(userId).child(postKey!!).child("body").setValue(body)
        databaseRef!!.child("user-posts").child(userId).child(postKey!!).child("edited").setValue(true)
    }

    companion object {
        private const val TAG = "EditPostActivity"
        private const val REQUIRED = "Required"
        const val EXTRA_POST_KEY = "post_key"
    }
}