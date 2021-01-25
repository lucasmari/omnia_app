package com.lucas.omnia.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lucas.omnia.R
import com.lucas.omnia.activities.EditReplyActivity
import com.lucas.omnia.activities.NewReplyActivity
import com.lucas.omnia.activities.RepliesActivity
import com.lucas.omnia.activities.UserPageActivity
import com.lucas.omnia.models.Comment
import com.lucas.omnia.models.Reply
import com.lucas.omnia.viewholders.ReplyViewHolder

class ReplyListFragment : Fragment() {
    private var databaseReference: DatabaseReference? = null
    private var recyclerAdapter: FirebaseRecyclerAdapter<Reply, ReplyViewHolder>? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_posts, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        recyclerView = rootView.findViewById(R.id.posts_rv)
        recyclerView?.setHasFixedSize(true)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up Layout Manager, reverse layout
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = layoutManager

        // Set up FirebaseRecyclerAdapter with the Query
        val repliesQuery = getQuery(databaseReference)
        val options: FirebaseRecyclerOptions<Reply> = FirebaseRecyclerOptions.Builder<Reply>()
                .setQuery(repliesQuery, Reply::class.java)
                .build()
        recyclerAdapter = object : FirebaseRecyclerAdapter<Reply, ReplyViewHolder>(options) {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReplyViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return ReplyViewHolder(inflater.inflate(R.layout.item_reply, viewGroup, false))
            }

            override fun onBindViewHolder(viewHolder: ReplyViewHolder, position: Int, reply: Reply) {
                val replyRef = getRef(position)
                val replyKey = replyRef.key

                // Determine if the Reply was edited
                if (reply.edited) {
                    viewHolder.editedView.visibility = View.VISIBLE
                } else {
                    viewHolder.editedView.visibility = View.GONE
                }

                // Determine if the current user has upvoted this item_reply and set UI accordingly
                if (reply.upVotes.containsKey(uid)) {
                    viewHolder.upVoteButton.setColorFilter(ContextCompat
                            .getColor(context!!, R.color.colorAccentBlue), PorterDuff.Mode.SRC_ATOP)
                } else {
                    viewHolder.upVoteButton.setColorFilter(viewHolder.upVoteButton.solidColor)
                }

                // Determine if the current user has downvoted this item_reply and set UI accordingly
                if (reply.downVotes.containsKey(uid)) {
                    viewHolder.downVoteButton.setColorFilter(ContextCompat
                            .getColor(context!!, R.color.colorAccentRed), PorterDuff.Mode.SRC_ATOP)
                } else {
                    viewHolder.downVoteButton.setColorFilter(viewHolder.downVoteButton.solidColor)
                }
                viewHolder.authorView.setOnClickListener {
                    val intent = Intent(activity, UserPageActivity::class.java)
                    intent.putExtra(UserPageActivity.EXTRA_USER_KEY, reply.uid)
                    startActivity(intent)
                }
                val globalReplyRef = databaseReference!!.child("comment-replies").child(RepliesActivity.commentKey!!).child(replyKey!!)
                viewHolder.bindToReply(reply, { onUpVoteClicked(globalReplyRef) }) { onDownVoteClicked(globalReplyRef) }
                viewHolder.moreButton.setOnClickListener { moreOptions(reply, replyKey) }
                viewHolder.replyButton.setOnClickListener {
                    val intent = Intent(activity, NewReplyActivity::class.java)
                    intent.putExtra(NewReplyActivity.EXTRA_COMMENT_KEY, RepliesActivity.commentKey)
                    intent.putExtra(NewReplyActivity.EXTRA_REPLY_KEY, replyKey)
                    startActivity(intent)
                }
            }
        }
        recyclerView!!.adapter = recyclerAdapter
    }

    private fun onUpVoteClicked(replyRef: DatabaseReference) {
        replyRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val r = mutableData.getValue(Reply::class.java)
                        ?: return Transaction.success(mutableData)
                if (r.downVotes.containsKey(uid)) {
                    // Remove downvote from item_Reply
                    r.downVoteCount = r.downVoteCount - 1
                    r.downVotes.remove(uid)
                    r.votesBalance = r.votesBalance - 1
                }
                if (r.upVotes.containsKey(uid)) {
                    // Unvote the item_Reply and remove self from votes
                    r.upVoteCount = r.upVoteCount - 1
                    r.upVotes.remove(uid)
                    r.votesBalance = r.votesBalance - 1
                } else {
                    // Upvote the item_Reply and add self to votes
                    r.upVoteCount = r.upVoteCount + 1
                    r.upVotes[uid] = true
                    r.votesBalance = r.votesBalance + 1
                }

                // Set value and report transaction success
                mutableData.value = r
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "ReplyTransaction:onComplete:$databaseError")
            }
        })
    }

    private fun onDownVoteClicked(replyRef: DatabaseReference) {
        replyRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val r = mutableData.getValue(Reply::class.java)
                        ?: return Transaction.success(mutableData)
                if (r.upVotes.containsKey(uid)) {
                    // Remove upvote from item_Reply
                    r.upVoteCount = r.upVoteCount - 1
                    r.upVotes.remove(uid)
                    r.votesBalance = r.votesBalance - 1
                }
                if (r.downVotes.containsKey(uid)) {
                    // Unvote the item_Reply and remove self from votes
                    r.downVoteCount = r.downVoteCount - 1
                    r.downVotes.remove(uid)
                    r.votesBalance = r.votesBalance + 1
                } else {
                    // Downvote the item_Reply and add self to votes
                    r.downVoteCount = r.downVoteCount + 1
                    r.downVotes[uid] = true
                    r.votesBalance = r.votesBalance - 1
                }

                // Set value and report transaction success
                mutableData.value = r
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "ReplyTransaction:onComplete:$databaseError")
            }
        })
    }

    private fun moreOptions(reply: Reply, replyKey: String?) {
        val builder = AlertDialog.Builder(context)
        if (uid == reply.uid) {
            builder.setItems(resources.getStringArray(R.array.options1)) { _: DialogInterface?, which: Int ->
                when (which) {
                    0 -> editReply(replyKey)
                    1 -> {
                        deleteReply(context, replyKey)
                        decrementRepliesCount()
                    }
                }
            }
            builder.show()
        } else {
            builder.setItems(resources.getStringArray(R.array.options3)) { _: DialogInterface?, which: Int ->
                if (which == 0) {
                    //reportReply();
                }
            }
            builder.show()
        }
    }

    private fun editReply(replyKey: String?) {
        val intent = Intent(activity, EditReplyActivity::class.java)
        intent.putExtra(EditReplyActivity.EXTRA_REPLY_KEY, replyKey)
        startActivity(intent)
    }

    private fun deleteReply(context: Context?, replyKey: String?) {
        AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.reply_list_ad_delete))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive)) { _: DialogInterface?, _: Int ->
                    Toast.makeText(context, getString(R.string.reply_list_toast_deleting), Toast.LENGTH_SHORT).show()
                    databaseReference!!.child("comment-replies").child(RepliesActivity.commentKey!!).child(replyKey!!).removeValue()
                }
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
                .show()
    }

    private fun decrementRepliesCount() {
        RepliesActivity.commentReference!!.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val c = mutableData.getValue(Comment::class.java)
                        ?: return Transaction.success(mutableData)

                // Decrement repliesCount of item_post
                c.replyCount = c.replyCount - 1

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

    override fun onStart() {
        super.onStart()
        if (recyclerAdapter != null) {
            recyclerAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (recyclerAdapter != null) {
            recyclerAdapter!!.stopListening()
        }
    }

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    private fun getQuery(databaseReference: DatabaseReference?): Query {
        return databaseReference!!.child("comment-replies")
                .child(RepliesActivity.commentKey!!).orderByChild("votesBalance").limitToFirst(100)
    }

    companion object {
        private const val TAG = "ReplyListFragment"
    }
}