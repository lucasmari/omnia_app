package com.lucas.omnia.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
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

        titleView = itemView.findViewById(R.id.post_tv_title);
        authorView = itemView.findViewById(R.id.post_tv_author);
        bodyView = itemView.findViewById(R.id.post_tv_body);
        editedView = itemView.findViewById(R.id.post_tv_edited);
        upVotesView = itemView.findViewById(R.id.post_tv_upvote_count);
        downVotesView = itemView.findViewById(R.id.post_tv_downvote_count);
        commentsView = itemView.findViewById(R.id.post_tv_comment_count);
        upVoteButton = itemView.findViewById(R.id.post_ib_upvote);
        downVoteButton = itemView.findViewById(R.id.post_ib_downvote);
        commentButton = itemView.findViewById(R.id.post_ib_comment);
        shareButton = itemView.findViewById(R.id.post_ib_share);
        moreButton = itemView.findViewById(R.id.post_ib_more);
    }

    public void bindToPost(Post post, View.OnClickListener upVoteClickListener, View.OnClickListener downVoteClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        bodyView.setText(post.body);
        upVotesView.setText(String.valueOf(post.upVoteCount));
        downVotesView.setText(String.valueOf(post.downVoteCount));
        commentsView.setText(String.valueOf(post.commentCount));
        upVoteButton.setOnClickListener(upVoteClickListener);
        downVoteButton.setOnClickListener(downVoteClickListener);
    }
}