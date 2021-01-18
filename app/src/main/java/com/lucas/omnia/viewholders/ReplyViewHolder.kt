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
    @JvmField
    var authorView: TextView = itemView.findViewById(R.id.reply_tv_author)
    private var timestampView: TextView = itemView.findViewById(R.id.reply_tv_timestamp)

    @JvmField
    var editedView: TextView = itemView.findViewById(R.id.reply_tv_edited)
    private var bodyView: TextView = itemView.findViewById(R.id.reply_tv_body)
    private var upVotesView: TextView = itemView.findViewById(R.id.reply_tv_upvote_count)
    private var downVotesView: TextView = itemView.findViewById(R.id.reply_tv_downvote_count)

    @JvmField
    var upVoteButton: ImageButton = itemView.findViewById(R.id.reply_ib_upvote)

    @JvmField
    var downVoteButton: ImageButton = itemView.findViewById(R.id.reply_ib_downvote)

    @JvmField
    var moreButton: ImageButton = itemView.findViewById(R.id.reply_ib_more)

    @JvmField
    var replyButton: Button = itemView.findViewById(R.id.reply_bt_reply)
    private var date = Date()
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