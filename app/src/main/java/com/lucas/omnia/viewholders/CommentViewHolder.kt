package com.lucas.omnia.viewholders

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Comment
import java.util.*

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var authorView: TextView
    var timestampView: TextView
    var editedView: TextView
    var bodyView: TextView
    var upVotesView: TextView
    var downVotesView: TextView
    var repliesView: TextView
    var upVoteButton: ImageButton
    var downVoteButton: ImageButton
    var moreButton: ImageButton
    var replyButton: Button
    var repliesButton: Button
    var repliesLayout: LinearLayout
    var date = Date()
    fun bindToComment(comment: Comment, upVoteClickListener: View.OnClickListener?, downVoteClickListener: View.OnClickListener?) {
        authorView.text = comment.author
        timestampView.text = getTimeDiff(comment)
        bodyView.text = comment.body
        upVotesView.text = java.lang.String.valueOf(comment.upVoteCount)
        downVotesView.text = java.lang.String.valueOf(comment.downVoteCount)
        repliesView.text = java.lang.String.valueOf(comment.replyCount)
        upVoteButton.setOnClickListener(upVoteClickListener)
        downVoteButton.setOnClickListener(downVoteClickListener)
    }

    private fun getTimeDiff(comment: Comment): String {
        val timeDiff = date.time - comment.timestamp
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
        authorView = itemView.findViewById(R.id.comment_tv_author)
        timestampView = itemView.findViewById(R.id.comment_tv_timestamp)
        editedView = itemView.findViewById(R.id.comment_tv_edited)
        bodyView = itemView.findViewById(R.id.comment_tv_body)
        upVotesView = itemView.findViewById(R.id.comment_tv_upvote_count)
        downVotesView = itemView.findViewById(R.id.comment_tv_downvote_count)
        repliesView = itemView.findViewById(R.id.comment_tv_reply_count)
        upVoteButton = itemView.findViewById(R.id.comment_ib_upvote)
        downVoteButton = itemView.findViewById(R.id.comment_ib_downvote)
        moreButton = itemView.findViewById(R.id.comment_ib_more)
        replyButton = itemView.findViewById(R.id.comment_bt_reply)
        repliesButton = itemView.findViewById(R.id.comment_bt_replies)
        repliesLayout = itemView.findViewById(R.id.comment_ll)
    }
}