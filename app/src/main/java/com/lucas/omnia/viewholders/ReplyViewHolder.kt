package com.lucas.omnia.viewholders

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Reply
import java.util.*

class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var authorView: TextView
    var timestampView: TextView
    var editedView: TextView
    var bodyView: TextView
    var upVotesView: TextView
    var downVotesView: TextView
    var upVoteButton: ImageButton
    var downVoteButton: ImageButton
    var moreButton: ImageButton
    var replyButton: Button
    var date = Date()
    fun bindToReply(reply: Reply, upVoteClickListener: View.OnClickListener?, downVoteClickListener: View.OnClickListener?) {
        authorView.text = reply.author
        timestampView.text = getTimeDiff(reply)
        bodyView.text = reply.body
        upVotesView.text = java.lang.String.valueOf(reply.upVoteCount)
        downVotesView.text = java.lang.String.valueOf(reply.downVoteCount)
        upVoteButton.setOnClickListener(upVoteClickListener)
        downVoteButton.setOnClickListener(downVoteClickListener)
    }

    private fun getTimeDiff(reply: Reply): String {
        val timeDiff = date.time - reply.timestamp
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
        authorView = itemView.findViewById(R.id.reply_tv_author)
        timestampView = itemView.findViewById(R.id.reply_tv_timestamp)
        editedView = itemView.findViewById(R.id.reply_tv_edited)
        bodyView = itemView.findViewById(R.id.reply_tv_body)
        upVotesView = itemView.findViewById(R.id.reply_tv_upvote_count)
        downVotesView = itemView.findViewById(R.id.reply_tv_downvote_count)
        upVoteButton = itemView.findViewById(R.id.reply_ib_upvote)
        downVoteButton = itemView.findViewById(R.id.reply_ib_downvote)
        moreButton = itemView.findViewById(R.id.reply_ib_more)
        replyButton = itemView.findViewById(R.id.reply_bt_reply)
    }
}