package com.lucas.omnia.viewholders

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.models.Comment
import com.lucas.omnia.utils.TimestampBuilder
import java.util.*

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    var authorView: TextView = itemView.findViewById(R.id.comment_tv_author)
    private var timestampView: TextView = itemView.findViewById(R.id.comment_tv_timestamp)
    @JvmField
    var editedView: TextView = itemView.findViewById(R.id.comment_tv_edited)
    private var bodyView: TextView = itemView.findViewById(R.id.comment_tv_body)
    private var upVotesView: TextView = itemView.findViewById(R.id.comment_tv_upvote_count)
    private var downVotesView: TextView = itemView.findViewById(R.id.comment_tv_downvote_count)
    private var repliesView: TextView = itemView.findViewById(R.id.comment_tv_reply_count)

    @JvmField
    var upVoteButton: ImageButton = itemView.findViewById(R.id.comment_ib_upvote)
    @JvmField
    var downVoteButton: ImageButton = itemView.findViewById(R.id.comment_ib_downvote)
    @JvmField
    var moreButton: ImageButton = itemView.findViewById(R.id.comment_ib_more)
    @JvmField
    var replyButton: Button = itemView.findViewById(R.id.comment_bt_reply)
    @JvmField
    var repliesButton: Button = itemView.findViewById(R.id.comment_bt_replies)

    @JvmField
    var repliesLayout: LinearLayout = itemView.findViewById(R.id.comment_ll)

    fun bindToComment(comment: Comment, upVoteClickListener: View.OnClickListener?, downVoteClickListener: View.OnClickListener?) {
        authorView.text = comment.author
        val timeDiff: Long = TimestampBuilder.getCommentTime(comment)
        timestampView.text = TimestampBuilder.getTimestamp(timeDiff)
        bodyView.text = comment.body
        upVotesView.text = java.lang.String.valueOf(comment.upVoteCount)
        downVotesView.text = java.lang.String.valueOf(comment.downVoteCount)
        repliesView.text = java.lang.String.valueOf(comment.replyCount)
        upVoteButton.setOnClickListener(upVoteClickListener)
        downVoteButton.setOnClickListener(downVoteClickListener)
    }
}