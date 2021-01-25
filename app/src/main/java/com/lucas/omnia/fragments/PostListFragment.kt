package com.lucas.omnia.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lucas.omnia.R
import com.lucas.omnia.activities.CommentsActivity
import com.lucas.omnia.activities.EditPostActivity
import com.lucas.omnia.activities.UserPageActivity
import com.lucas.omnia.models.Post
import com.lucas.omnia.utils.ImageLoadAsyncTask
import com.lucas.omnia.viewholders.PostViewHolder
import java.net.MalformedURLException
import java.net.URL

abstract class PostListFragment : Fragment() {
    private var databaseReference: DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var postImgUrl: URL? = null
    private var recyclerAdapter: FirebaseRecyclerAdapter<Post, PostViewHolder>? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_posts, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
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
        val postsQuery = getQuery(databaseReference)
        val options: FirebaseRecyclerOptions<Post> = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post::class.java)
                .build()
        recyclerAdapter = object : FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PostViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false))
            }

            override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int, post: Post) {
                val postRef = getRef(position)
                val postKey = postRef.key

                // Determine if the post has image/video
                if (post.hasImage) {
                    viewHolder.bodyView.visibility = View.GONE
                    fetchProfileImage(post.uid, postKey, viewHolder.bodyImageView)
                } else {
                    viewHolder.bodyView.visibility = View.VISIBLE
                    viewHolder.bodyImageView.visibility = View.GONE
                }

                // Determine if the post was edited
                if (post.edited) {
                    viewHolder.editedView.visibility = View.VISIBLE
                } else {
                    viewHolder.editedView.visibility = View.GONE
                }

                // Determine if the current user has upvoted this item_post and set UI accordingly
                if (post.upVotes.containsKey(uid)) {
                    viewHolder.upVoteButton.setColorFilter(ContextCompat
                            .getColor(context!!, R.color.colorAccentBlue), PorterDuff.Mode.SRC_ATOP)
                } else {
                    viewHolder.upVoteButton.setColorFilter(viewHolder.upVoteButton.solidColor)
                }

                // Determine if the current user has downvoted this item_post and set UI accordingly
                if (post.downVotes.containsKey(uid)) {
                    viewHolder.downVoteButton.setColorFilter(ContextCompat
                            .getColor(context!!, R.color.colorAccentRed), PorterDuff.Mode.SRC_ATOP)
                } else {
                    viewHolder.downVoteButton.setColorFilter(viewHolder.downVoteButton.solidColor)
                }
                viewHolder.authorView.setOnClickListener {
                    val intent = Intent(activity, UserPageActivity::class.java)
                    intent.putExtra(UserPageActivity.EXTRA_USER_KEY, post.uid)
                    startActivity(intent)
                }
                val globalPostRef = databaseReference!!.child("posts").child(postKey!!)
                val userPostRef = databaseReference!!.child("user-posts").child(post.uid!!).child(postKey)
                viewHolder.bindToPost(post, {
                    // Run two transactions
                    onUpVoteClicked(globalPostRef)
                    onUpVoteClicked(userPostRef)
                }) {
                    // Run two transactions
                    onDownVoteClicked(globalPostRef)
                    onDownVoteClicked(userPostRef)
                }
                viewHolder.commentButton.setOnClickListener {
                    val intent = Intent(activity, CommentsActivity::class.java)
                    intent.putExtra(CommentsActivity.EXTRA_POST_KEY, postKey)
                    startActivity(intent)
                }
                viewHolder.shareButton.setOnClickListener { sharePost(post) }
                viewHolder.moreButton.setOnClickListener { moreOptions(post, postKey) }
            }
        }
        recyclerView!!.adapter = recyclerAdapter
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && FeedNavFragment.addFab?.visibility == View.VISIBLE) {
                    FeedNavFragment.addFab?.hide()
                } else if (dy < 0 && FeedNavFragment.addFab?.visibility != View.VISIBLE) {
                    FeedNavFragment.addFab?.show()
                }
            }
        })
    }

    private fun fetchProfileImage(userId: String?, postKey: String?, postImgView: ImageView) {
        val postImgRef = storageRef!!.child("$userId/posts/$postKey")
        postImgRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            try {
                postImgUrl = URL(uri.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            val imageLoadAsyncTask = ImageLoadAsyncTask(postImgUrl,
                    postImgView, false)
            imageLoadAsyncTask.execute()
            postImgView.visibility = View.VISIBLE
        }.addOnFailureListener { Toast.makeText(context, getString(R.string.profile_toast_fetch_error), Toast.LENGTH_SHORT).show() }
    }

    private fun onUpVoteClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Post::class.java)
                        ?: return Transaction.success(mutableData)
                if (p.downVotes.containsKey(uid)) {
                    // Remove downvote from item_post
                    p.downVoteCount = p.downVoteCount - 1
                    p.downVotes.remove(uid)
                    p.votesBalance = p.votesBalance - 1
                }
                if (p.upVotes.containsKey(uid)) {
                    // Unvote the item_post and remove self from votes
                    p.upVoteCount = p.upVoteCount - 1
                    p.upVotes.remove(uid)
                    p.votesBalance = p.votesBalance - 1
                } else {
                    // Upvote the item_post and add self to votes
                    p.upVoteCount = p.upVoteCount + 1
                    p.upVotes[uid] = true
                    p.votesBalance = p.votesBalance + 1
                }

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:$databaseError")
            }
        })
    }

    private fun onDownVoteClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Post::class.java)
                        ?: return Transaction.success(mutableData)
                if (p.upVotes.containsKey(uid)) {
                    // Remove upvote from item_post
                    p.upVoteCount = p.upVoteCount - 1
                    p.upVotes.remove(uid)
                    p.votesBalance = p.votesBalance - 1
                }
                if (p.downVotes.containsKey(uid)) {
                    // Unvote the item_post and remove self from votes
                    p.downVoteCount = p.downVoteCount - 1
                    p.downVotes.remove(uid)
                    p.votesBalance = p.votesBalance + 1
                } else {
                    // Downvote the item_post and add self to votes
                    p.downVoteCount = p.downVoteCount + 1
                    p.downVotes[uid] = true
                    p.votesBalance = p.votesBalance - 1
                }

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean,
                                    currentData: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:$databaseError")
            }
        })
    }

    private fun sharePost(post: Post) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TITLE, post.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.body)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
    }

    private fun moreOptions(post: Post, postKey: String?) {
        val builder = AlertDialog.Builder(context)
        if (uid != post.uid) {
            builder.setItems(resources.getStringArray(R.array.options3)) { _: DialogInterface?, which: Int ->
                if (which == 0) {
                    //reportPost();
                }
            }
            builder.show()
        } else if (post.hasImage) {
            builder.setItems(resources.getStringArray(R.array.options2)) { _: DialogInterface?, which: Int -> if (which == 0) deletePost(context, postKey, post) }
            builder.show()
        } else {
            builder.setItems(resources.getStringArray(R.array.options1)) { _: DialogInterface?, which: Int ->
                when (which) {
                    0 -> editPost(postKey)
                    1 -> deletePost(context, postKey, post)
                }
            }
            builder.show()
        }
    }

    private fun editPost(postKey: String?) {
        val intent = Intent(activity, EditPostActivity::class.java)
        intent.putExtra(EditPostActivity.EXTRA_POST_KEY, postKey)
        startActivity(intent)
    }

    private fun deletePost(context: Context?, postKey: String?, post: Post) {
        AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.post_list_ad_delete))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive)) { _: DialogInterface?, _: Int ->
                    Toast.makeText(context, getString(R.string.post_list_toast_deleting), Toast.LENGTH_SHORT).show()
                    databaseReference!!.child("posts").child(postKey!!).removeValue()
                    databaseReference!!.child("user-posts").child(post.uid!!).child(postKey).removeValue()
                    val postImgRef = storageRef!!.child(post.uid + "/posts/" + postKey)
                    postImgRef.delete().addOnSuccessListener {
                        Toast.makeText(context, getString(R.string.post_list_toast_success),
                                Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { Toast.makeText(context, getString(R.string.post_list_toast_failure), Toast.LENGTH_SHORT).show() }
                }
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
                .show()
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

    abstract fun getQuery(databaseReference: DatabaseReference?): Query

    companion object {
        private const val TAG = "PostListFragment"
    }
}