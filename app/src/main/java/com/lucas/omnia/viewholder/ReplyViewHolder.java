package com.lucas.omnia.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        authorView = itemView.findViewById(R.id.authorTV);
        bodyView = itemView.findViewById(R.id.bodyTv);
        editedView = itemView.findViewById(R.id.editedTv);
        upVotesView = itemView.findViewById(R.id.upVotesTv);
        downVotesView = itemView.findViewById(R.id.downVotesTv);
        upVoteButton = itemView.findViewById(R.id.upVoteButton);
        downVoteButton = itemView.findViewById(R.id.downVoteButton);
        moreButton = itemView.findViewById(R.id.moreButton);
        replyButton = itemView.findViewById(R.id.replyButton);
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
