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
import com.lucas.omnia.activities.*
import com.lucas.omnia.fragments.FeedNavFragment.Companion.addFab
import com.lucas.omnia.models.Comment
import com.lucas.omnia.models.Post
import com.lucas.omnia.viewholders.CommentViewHolder

class CommentListFragment : Fragment() {
    private var databaseReference: DatabaseReference? = null
    private var recyclerAdapter: FirebaseRecyclerAdapter<Comment, CommentViewHolder>? = null
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
        val commentsQuery = getQuery(databaseReference)
        val options: FirebaseRecyclerOptions<Comment> = FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(commentsQuery, Comment::class.java)
                .build()
        recyclerAdapter = object : FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommentViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return CommentViewHolder(inflater.inflate(R.layout.item_comment, viewGroup, false))
            }

            override fun onBindViewHolder(viewHolder: CommentViewHolder, position: Int, comment: Comment) {
                val commentRef = getRef(position)
                val commentKey = commentRef.key

                // Determine if the Comment was edited
                if (comment.edited) {
                    viewHolder.editedView.visibility = View.VISIBLE
                } else {
                    viewHolder.editedView.visibility = View.GONE
                }

                // Determine if the current user has upvoted this item_comment and set UI accordingly
                if (comment.upVotes.containsKey(uid)) {
                    viewHolder.upVoteButton.setColorFilter(ContextCompat
                            .getColor(context!!, R.color.colorAccentBlue), PorterDuff.Mode.SRC_ATOP)
                } else {
                    viewHolder.upVoteButton.setColorFilter(viewHolder.upVoteButton.solidColor)
                }

                // Determine if the current user has downvoted this item_comment and set UI accordingly
                if (comment.downVotes.containsKey(uid)) {
                    viewHolder.downVoteButton.setColorFilter(ContextCompat
                            .getColor(context!!, R.color.colorAccentRed), PorterDuff.Mode.SRC_ATOP)
                } else {
                    viewHolder.downVoteButton.setColorFilter(viewHolder.downVoteButton.solidColor)
                }
                viewHolder.authorView.setOnClickListener {
                    val intent = Intent(activity, UserPageActivity::class.java)
                    intent.putExtra(UserPageActivity.EXTRA_USER_KEY, comment.uid)
                    startActivity(intent)
                }
                val globalCommentRef = databaseReference!!.child("post-comments").child(CommentsActivity.postKey!!).child(commentKey!!)
                viewHolder.bindToComment(comment, { onUpVoteClicked(globalCommentRef) }) { onDownVoteClicked(globalCommentRef) }
                viewHolder.moreButton.setOnClickListener { moreOptions(comment, commentKey) }
                viewHolder.replyButton.setOnClickListener {
                    val intent = Intent(activity, NewReplyActivity::class.java)
                    intent.putExtra(NewReplyActivity.EXTRA_COMMENT_KEY, commentKey)
                    startActivity(intent)
                }
                if (comment.replyCount > 0) {
                    viewHolder.repliesLayout.visibility = View.VISIBLE
                } else {
                    viewHolder.repliesLayout.visibility = View.GONE
                }
                viewHolder.repliesButton.setOnClickListener {
                    val intent = Intent(activity, RepliesActivity::class.java)
                    intent.putExtra(RepliesActivity.EXTRA_COMMENT_KEY, commentKey)
                    startActivity(intent)
                }
            }
        }
        recyclerView!!.adapter = recyclerAdapter
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && addFab!!.visibility == View.VISIBLE) {
                    addFab!!.hide()
                } else if (dy < 0 && addFab!!.visibility != View.VISIBLE) {
                    addFab!!.show()
                }
            }
        })
    }

    private fun onUpVoteClicked(commentRef: DatabaseReference) {
        commentRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val c = mutableData.getValue(Comment::class.java)
                        ?: return Transaction.success(mutableData)
                if (c.downVotes.containsKey(uid)) {
                    // Remove downvote from item_Comment
                    c.downVoteCount = c.downVoteCount - 1
                    c.downVotes.remove(uid)
                    c.votesBalance = c.votesBalance - 1
                }
                if (c.upVotes.containsKey(uid)) {
                    // Unvote the item_Comment and remove self from votes
                    c.upVoteCount = c.upVoteCount - 1
                    c.upVotes.remove(uid)
                    c.votesBalance = c.votesBalance - 1
                } else {
                    // Upvote the item_Comment and add self to votes
                    c.upVoteCount = c.upVoteCount + 1
                    c.upVotes[uid] = true
                    c.votesBalance = c.votesBalance + 1
                }

                // Set value and report transaction success
                mutableData.value = c
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "CommentTransaction:onComplete:$databaseError")
            }
        })
    }

    private fun onDownVoteClicked(commentRef: DatabaseReference) {
        commentRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val c = mutableData.getValue(Comment::class.java)
                        ?: return Transaction.success(mutableData)
                if (c.upVotes.containsKey(uid)) {
                    // Remove upvote from item_Comment
                    c.upVoteCount = c.upVoteCount - 1
                    c.upVotes.remove(uid)
                    c.votesBalance = c.votesBalance - 1
                }
                if (c.downVotes.containsKey(uid)) {
                    // Unvote the item_Comment and remove self from votes
                    c.downVoteCount = c.downVoteCount - 1
                    c.downVotes.remove(uid)
                    c.votesBalance = c.votesBalance + 1
                } else {
                    // Downvote the item_Comment and add self to votes
                    c.downVoteCount = c.downVoteCount + 1
                    c.downVotes[uid] = true
                    c.votesBalance = c.votesBalance - 1
                }

                // Set value and report transaction success
                mutableData.value = c
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "CommentTransaction:onComplete:$databaseError")
            }
        })
    }

    private fun moreOptions(comment: Comment, commentKey: String?) {
        val builder = AlertDialog.Builder(context)
        if (uid == comment.uid) {
            builder.setItems(resources.getStringArray(R.array.options1)) { _: DialogInterface?, which: Int ->
                when (which) {
                    0 -> editComment(commentKey)
                    1 -> {
                        deleteComment(context, commentKey)
                        decrementCommentsCount()
                    }
                }
            }
            builder.show()
        } else {
            builder.setItems(resources.getStringArray(R.array.options3)) { _: DialogInterface?, which: Int ->
                if (which == 0) {
                    //reportComment();
                }
            }
            builder.show()
        }
    }

    private fun editComment(commentKey: String?) {
        val intent = Intent(activity, EditCommentActivity::class.java)
        intent.putExtra(EditCommentActivity.EXTRA_COMMENT_KEY, commentKey)
        startActivity(intent)
    }

    private fun deleteComment(context: Context?, commentKey: String?) {
        AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.comment_list_ad_delete))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive)) { _: DialogInterface?, _: Int ->
                    Toast.makeText(context, getString(R.string.comment_list_toast_deleting), Toast.LENGTH_SHORT).show()
                    databaseReference!!.child("post-comments").child(CommentsActivity.postKey!!).child(commentKey!!).removeValue()
                }
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
                .show()
    }

    private fun decrementCommentsCount() {
        CommentsActivity.postReference!!.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Post::class.java)
                        ?: return Transaction.success(mutableData)

                // Decrement commentsCount of item_post
                p.commentCount = p.commentCount - 1

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
        return databaseReference!!.child("post-comments")
                .child(CommentsActivity.postKey!!).orderByChild("votesBalance").limitToFirst(100)
    }

    companion object {
        private const val TAG = "CommentListFragment"
    }
}