package com.lucas.omnia.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Reply;

public class ReplyViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bodyView;
    public TextView editedView;
    public TextView upVotesView;
    public TextView downVotesView;
    public ImageButton upVoteButton;
    public ImageButton downVoteButton;
    public ImageButton moreButton;
    public Button replyButton;

    public ReplyViewHolder(View itemView) {
        super(itemView);

        authorView = itemView.findViewById(R.id.reply_tv_author);
        bodyView = itemView.findViewById(R.id.reply_tv_body);
        editedView = itemView.findViewById(R.id.reply_tv_edited);
        upVotesView = itemView.findViewById(R.id.reply_tv_upvote_count);
        downVotesView = itemView.findViewById(R.id.reply_tv_downvote_count);
        upVoteButton = itemView.findViewById(R.id.reply_ib_upvote);
        downVoteButton = itemView.findViewById(R.id.reply_ib_downvote);
        moreButton = itemView.findViewById(R.id.reply_ib_more);
        replyButton = itemView.findViewById(R.id.reply_bt_reply);
    }

    public void bindToReply(Reply reply, View.OnClickListener upVoteClickListener, View.OnClickListener downVoteClickListener) {
        authorView.setText(reply.author);
        bodyView.setText(reply.body);
        upVotesView.setText(String.valueOf(reply.upVoteCount));
        downVotesView.setText(String.valueOf(reply.downVoteCount));
        upVoteButton.setOnClickListener(upVoteClickListener);
        downVoteButton.setOnClickListener(downVoteClickListener);
    }
}
