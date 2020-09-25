package com.lucas.omnia.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    //public TextView authorView;
    public TextView bodyView;
    public TextView editedView;
    public TextView upVotesView;
    public TextView downVotesView;
    public TextView commentsView;
    public ImageButton upVoteButton;
    public ImageButton downVoteButton;
    public ImageButton commentButton;
    public ImageButton shareButton;
    public ImageButton moreButton;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.titleTV);
        //authorView = itemView.findViewById(R.id.authorTv);
        bodyView = itemView.findViewById(R.id.bodyTv);
        editedView = itemView.findViewById(R.id.editedTv);
        upVotesView = itemView.findViewById(R.id.upVotesTv);
        downVotesView = itemView.findViewById(R.id.downVotesTv);
        commentsView = itemView.findViewById(R.id.commentsTv);
        upVoteButton = itemView.findViewById(R.id.upVoteButton);
        downVoteButton = itemView.findViewById(R.id.downVoteButton);
        commentButton = itemView.findViewById(R.id.commentButton);
        shareButton = itemView.findViewById(R.id.shareButton);
        moreButton = itemView.findViewById(R.id.moreButton);
    }

    public void bindToPost(Post post, View.OnClickListener upVoteClickListener, View.OnClickListener downVoteClickListener) {
        titleView.setText(post.title);
        //authorView.setText(post.author);
        bodyView.setText(post.body);
        upVotesView.setText(String.valueOf(post.upVoteCount));
        downVotesView.setText(String.valueOf(post.downVoteCount));
        commentsView.setText(String.valueOf(post.commentsCount));

        upVoteButton.setOnClickListener(upVoteClickListener);
        downVoteButton.setOnClickListener(downVoteClickListener);
    }
}