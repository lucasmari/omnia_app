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
    private var titleView: TextView = itemView.findViewById(R.id.post_tv_title)

    @JvmField
    var authorView: TextView = itemView.findViewById(R.id.post_tv_author)
    private var timestampView: TextView = itemView.findViewById(R.id.post_tv_timestamp)

    @JvmField
    var editedView: TextView = itemView.findViewById(R.id.post_tv_edited)

    @JvmField
    var bodyView: TextView = itemView.findViewById(R.id.post_tv_body)

    @JvmField
    var bodyImageView: ImageView = itemView.findViewById(R.id.post_iv_body_image)
    private var upVotesView: TextView = itemView.findViewById(R.id.post_tv_upvote_count)
    private var downVotesView: TextView = itemView.findViewById(R.id.post_tv_downvote_count)
    private var commentsView: TextView = itemView.findViewById(R.id.post_tv_comment_count)

    @JvmField
    var upVoteButton: ImageButton = itemView.findViewById(R.id.post_ib_upvote)

    @JvmField
    var downVoteButton: ImageButton = itemView.findViewById(R.id.post_ib_downvote)

    @JvmField
    var commentButton: ImageButton = itemView.findViewById(R.id.post_ib_comment)

    @JvmField
    var shareButton: ImageButton = itemView.findViewById(R.id.post_ib_share)

    @JvmField
    var moreButton: ImageButton = itemView.findViewById(R.id.post_ib_more)
    private var date = Date()
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
        return when {
            diffSeconds < 1 -> {
                "less than a second"
            }
            diffMinutes < 1 -> {
                "$diffSeconds s"
            }
            diffHours < 1 -> {
                "$diffMinutes min"
            }
            diffDays < 1 -> {
                "$diffHours h"
            }
            diffWeeks < 1 -> {
                "$diffDays d"
            }
            diffMonths < 1 -> {
                "$diffWeeks w"
            }
            diffYears < 1 -> {
                "$diffMonths m"
            }
            else -> {
                "$diffYears y"
            }
        }
    }

}