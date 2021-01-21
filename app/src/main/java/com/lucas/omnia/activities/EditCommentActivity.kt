package com.lucas.omnia.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityEditCommentBinding
import com.lucas.omnia.models.Comment

class EditCommentActivity : BaseActivity() {
    private var binding: ActivityEditCommentBinding? = null
    private var commentKey: String? = null
    private var databaseRef: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCommentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference

        // Get item_Comment key from intent
        commentKey = intent.getStringExtra(EXTRA_COMMENT_KEY)
        requireNotNull(commentKey) { "Must pass EXTRA_COMMENT_KEY" }

        // Initialize Database
        val commentReference = databaseRef!!.child("post-comments")
                .child(CommentsActivity.postKey!!).child(commentKey!!)
        commentReference.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val comment = dataSnapshot.getValue(Comment::class.java)
                        binding!!.editCommentEtBody.setText(comment!!.body)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "loadComment:onCancelled", databaseError.toException())
                        setEditingEnabled(true)
                    }
                })
        binding!!.editCommentFabSubmit.setOnClickListener { submitComment() }
    }

    private fun submitComment() {
        val body = binding!!.editCommentEtBody.text.toString()

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding!!.editCommentEtBody.error = REQUIRED
            return
        }

        // Disable button so there are no multi-Comments
        setEditingEnabled(false)
        Toast.makeText(this, getString(R.string.new_comment_toast_commenting), Toast.LENGTH_SHORT).show()
        databaseRef!!.child("post-comments").child(CommentsActivity.postKey!!).child(commentKey!!).child("body").setValue(body)
        databaseRef!!.child("post-comments").child(CommentsActivity.postKey!!).child(commentKey!!).child("edited").setValue(true)
        setEditingEnabled(true)
        finish()
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding!!.editCommentEtBody.isEnabled = enabled
        if (enabled) {
            binding!!.editCommentFabSubmit.show()
        } else {
            binding!!.editCommentFabSubmit.hide()
        }
    }

    companion object {
        private const val TAG = "EditCommentActivity"
        private const val REQUIRED = "Required"
        const val EXTRA_COMMENT_KEY = "comment_key"
    }
}