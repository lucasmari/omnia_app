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
import com.lucas.omnia.databinding.ActivityEditReplyBinding
import com.lucas.omnia.models.Reply

class EditReplyActivity : BaseActivity() {
    private var binding: ActivityEditReplyBinding? = null
    private var replyKey: String? = null
    private var databaseRef: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditReplyBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseRef = databaseReference

        // Get item_Reply key from intent
        replyKey = intent.getStringExtra(EXTRA_REPLY_KEY)
        requireNotNull(replyKey) { "Must pass EXTRA_REPLY_KEY" }

        // Initialize Database
        val replyReference = RepliesActivity.commentKey?.let {
            databaseRef!!.child("comment-replies")
                .child(it).child(replyKey!!)
        }
        replyReference?.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val reply = dataSnapshot.getValue(Reply::class.java)
                        binding!!.editReplyEtBody.setText(reply!!.body)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(TAG, "loadReply:onCancelled", databaseError.toException())
                        setEditingEnabled(true)
                    }
                })
        binding!!.editReplyFabSubmit.setOnClickListener { submitReply() }
    }

    private fun submitReply() {
        val body = binding!!.editReplyEtBody.text.toString()

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding!!.editReplyEtBody.error = REQUIRED
            return
        }

        // Disable button so there are no multi-Replies
        setEditingEnabled(false)
        Toast.makeText(this, getString(R.string.new_reply_toast_replying), Toast.LENGTH_SHORT).show()
        RepliesActivity.commentKey?.let { databaseRef!!.child("comment-replies").child(it).child(replyKey!!).child("body").setValue(body) }
        RepliesActivity.commentKey?.let { databaseRef!!.child("comment-replies").child(it).child(replyKey!!).child("edited").setValue(true) }
        setEditingEnabled(true)
        finish()
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding!!.editReplyEtBody.isEnabled = enabled
        if (enabled) {
            binding!!.editReplyFabSubmit.show()
        } else {
            binding!!.editReplyFabSubmit.hide()
        }
    }

    companion object {
        private const val TAG = "EditReplyActivity"
        private const val REQUIRED = "Required"
        const val EXTRA_REPLY_KEY = "reply_key"
    }
}