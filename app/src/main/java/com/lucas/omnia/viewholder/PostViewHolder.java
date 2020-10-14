package com.lucas.omnia.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Post;

import java.sql.Timestamp;
import java.util.Date;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView timestampView;
    public TextView editedView;
    public TextView bodyView;
    public ImageView bodyImageView;
    public TextView upVotesView;
    public TextView downVotesView;
    public TextView commentsView;
    public ImageButton upVoteButton;
    public ImageButton downVoteButton;
    public ImageButton commentButton;
    public ImageButton shareButton;
    public ImageButton moreButton;
    public Date date = new Date();

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_tv_title);
        authorView = itemView.findViewById(R.id.post_tv_author);
        timestampView = itemView.findViewById(R.id.post_tv_timestamp);
        editedView = itemView.findViewById(R.id.post_tv_edited);
        bodyView = itemView.findViewById(R.id.post_tv_body);
        bodyImageView = itemView.findViewById(R.id.post_iv_body_image);
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
        timestampView.setText(getTimeDiff(post));
        bodyView.setText(post.body);
        upVotesView.setText(String.valueOf(post.upVoteCount));
        downVotesView.setText(String.valueOf(post.downVoteCount));
        commentsView.setText(String.valueOf(post.commentCount));
        upVoteButton.setOnClickListener(upVoteClickListener);
        downVoteButton.setOnClickListener(downVoteClickListener);
    }

    private String getTimeDiff(Post post) {
        long timeDiff = date.getTime() - post.timestamp;

        long diffSeconds = timeDiff / 1000;
        long diffMinutes = timeDiff / (60 * 1000);
        long diffHours = timeDiff / (60 * 60 * 1000);
        long diffDays = timeDiff / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDiff / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (timeDiff / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = timeDiff / ((long)60 * 60 * 1000 * 24 * 365);

        if (diffSeconds < 1) {
            return "less than a second";
        } else if (diffMinutes < 1) {
            return diffSeconds + " s";
        } else if (diffHours < 1) {
            return diffMinutes + " min";
        } else if (diffDays < 1) {
            return diffHours + " h";
        } else if (diffWeeks < 1) {
            return diffDays + " d";
        } else if (diffMonths < 1) {
            return diffWeeks + " w";
        } else if (diffYears < 1) {
            return diffMonths + " m";
        } else {
            return diffYears + " y";
        }
    }
}