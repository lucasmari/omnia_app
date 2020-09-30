package com.lucas.omnia.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Comment;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bodyView;
    public TextView editedView;
    public TextView upVotesView;
    public TextView downVotesView;
    public TextView repliesView;
    public ImageButton upVoteButton;
    public ImageButton downVoteButton;
    public ImageButton moreButton;
    public Button replyButton;
    public Button repliesButton;
    public LinearLayout repliesLayout;

    public CommentViewHolder(View itemView) {
        super(itemView);

        authorView = itemView.findViewById(R.id.authorTV);
        bodyView = itemView.findViewById(R.id.bodyTv);
        editedView = itemView.findViewById(R.id.editedTv);
        upVotesView = itemView.findViewById(R.id.upVotesTv);
        downVotesView = itemView.findViewById(R.id.downVotesTv);
        repliesView = itemView.findViewById(R.id.repliesTv);
        upVoteButton = itemView.findViewById(R.id.upVoteButton);
        downVoteButton = itemView.findViewById(R.id.downVoteButton);
        moreButton = itemView.findViewById(R.id.moreButton);
        replyButton = itemView.findViewById(R.id.replyButton);
        repliesButton = itemView.findViewById(R.id.repliesButton);
        repliesLayout = itemView.findViewById(R.id.repliesLayout);
    }

    public void bindToComment(Comment comment, View.OnClickListener upVoteClickListener, View.OnClickListener downVoteClickListener) {
        authorView.setText(comment.author);
        bodyView.setText(comment.body);
        upVotesView.setText(String.valueOf(comment.upVoteCount));
        downVotesView.setText(String.valueOf(comment.downVoteCount));
        repliesView.setText(String.valueOf(comment.replyCount));
        upVoteButton.setOnClickListener(upVoteClickListener);
        downVoteButton.setOnClickListener(downVoteClickListener);
    }
}