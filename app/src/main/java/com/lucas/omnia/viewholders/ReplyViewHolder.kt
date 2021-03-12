package com.lucas.omnia.viewholders

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Reply
import com.lucas.omnia.utils.TimestampBuilder
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

    fun bindToReply(reply: Reply, upVoteClickListener: View.OnClickListener?, downVoteClickListener: View.OnClickListener?) {
        authorView.text = reply.author
        val timeDiff: Long = TimestampBuilder.getReplyTime(reply)
        timestampView.text = TimestampBuilder.getTimestamp(timeDiff)
        bodyView.text = reply.body
        upVotesView.text = java.lang.String.valueOf(reply.upVoteCount)
        downVotesView.text = java.lang.String.valueOf(reply.downVoteCount)
        upVoteButton.setOnClickListener(upVoteClickListener)
        downVoteButton.setOnClickListener(downVoteClickListener)
    }
}