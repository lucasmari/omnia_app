package com.lucas.omnia.viewholders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Post
import java.util.*

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleView: TextView
    var authorView: TextView
    var timestampView: TextView
    var editedView: TextView
    var bodyView: TextView
    var bodyImageView: ImageView
    var upVotesView: TextView
    var downVotesView: TextView
    var commentsView: TextView
    var upVoteButton: ImageButton
    var downVoteButton: ImageButton
    var commentButton: ImageButton
    var shareButton: ImageButton
    var moreButton: ImageButton
    var date = Date()
    fun bindToPost(post: Post, upVoteClickListener: View.OnClickListener?, downVoteClickListener: View.OnClickListener?) {
        titleView.text = post.title
        authorView.text = post.author
        timestampView.text = getTimeDiff(post)
        bodyView.text = post.body
        upVotesView.text = java.lang.String.valueOf(post.upVoteCount)
        downVotesView.text = java.lang.String.valueOf(post.downVoteCount)
        commentsView.text = java.lang.String.valueOf(post.commentCount)
        upVoteButton.setOnClickListener(upVoteClickListener)
        downVoteButton.setOnClickListener(downVoteClickListener)
    }

    private fun getTimeDiff(post: Post): String {
        val timeDiff = date.time - post.timestamp
        val diffSeconds = timeDiff / 1000
        val diffMinutes = timeDiff / (60 * 1000)
        val diffHours = timeDiff / (60 * 60 * 1000)
        val diffDays = timeDiff / (60 * 60 * 1000 * 24)
        val diffWeeks = timeDiff / (60 * 60 * 1000 * 24 * 7)
        val diffMonths = (timeDiff / (60 * 60 * 1000 * 24 * 30.41666666)).toLong()
        val diffYears = timeDiff / (60.toLong() * 60 * 1000 * 24 * 365)
        return if (diffSeconds < 1) {
            "less than a second"
        } else if (diffMinutes < 1) {
            "$diffSeconds s"
        } else if (diffHours < 1) {
            "$diffMinutes min"
        } else if (diffDays < 1) {
            "$diffHours h"
        } else if (diffWeeks < 1) {
            "$diffDays d"
        } else if (diffMonths < 1) {
            "$diffWeeks w"
        } else if (diffYears < 1) {
            "$diffMonths m"
        } else {
            "$diffYears y"
        }
    }

    init {
        titleView = itemView.findViewById(R.id.post_tv_title)
        authorView = itemView.findViewById(R.id.post_tv_author)
        timestampView = itemView.findViewById(R.id.post_tv_timestamp)
        editedView = itemView.findViewById(R.id.post_tv_edited)
        bodyView = itemView.findViewById(R.id.post_tv_body)
        bodyImageView = itemView.findViewById(R.id.post_iv_body_image)
        upVotesView = itemView.findViewById(R.id.post_tv_upvote_count)
        downVotesView = itemView.findViewById(R.id.post_tv_downvote_count)
        commentsView = itemView.findViewById(R.id.post_tv_comment_count)
        upVoteButton = itemView.findViewById(R.id.post_ib_upvote)
        downVoteButton = itemView.findViewById(R.id.post_ib_downvote)
        commentButton = itemView.findViewById(R.id.post_ib_comment)
        shareButton = itemView.findViewById(R.id.post_ib_share)
        moreButton = itemView.findViewById(R.id.post_ib_more)
    }
}