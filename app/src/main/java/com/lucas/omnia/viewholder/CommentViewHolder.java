package com.lucas.omnia.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.Post;

import java.util.Date;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView timestampView;
    public TextView editedView;
    public TextView bodyView;
    public TextView upVotesView;
    public TextView downVotesView;
    public TextView repliesView;
    public ImageButton upVoteButton;
    public ImageButton downVoteButton;
    public ImageButton moreButton;
    public Button replyButton;
    public Button repliesButton;
    public LinearLayout repliesLayout;
    public Date date = new Date();

    public CommentViewHolder(View itemView) {
        super(itemView);

        authorView = itemView.findViewById(R.id.comment_tv_author);
        timestampView = itemView.findViewById(R.id.comment_tv_timestamp);
        editedView = itemView.findViewById(R.id.comment_tv_edited);
        bodyView = itemView.findViewById(R.id.comment_tv_body);
        upVotesView = itemView.findViewById(R.id.comment_tv_upvote_count);
        downVotesView = itemView.findViewById(R.id.comment_tv_downvote_count);
        repliesView = itemView.findViewById(R.id.comment_tv_reply_count);
        upVoteButton = itemView.findViewById(R.id.comment_ib_upvote);
        downVoteButton = itemView.findViewById(R.id.comment_ib_downvote);
        moreButton = itemView.findViewById(R.id.comment_ib_more);
        replyButton = itemView.findViewById(R.id.comment_bt_reply);
        repliesButton = itemView.findViewById(R.id.comment_bt_replies);
        repliesLayout = itemView.findViewById(R.id.comment_ll);
    }

    public void bindToComment(Comment comment, View.OnClickListener upVoteClickListener, View.OnClickListener downVoteClickListener) {
        authorView.setText(comment.author);
        timestampView.setText(getTimeDiff(comment));
        bodyView.setText(comment.body);
        upVotesView.setText(String.valueOf(comment.upVoteCount));
        downVotesView.setText(String.valueOf(comment.downVoteCount));
        repliesView.setText(String.valueOf(comment.replyCount));
        upVoteButton.setOnClickListener(upVoteClickListener);
        downVoteButton.setOnClickListener(downVoteClickListener);
    }

    private String getTimeDiff(Comment comment) {
        long timeDiff = date.getTime() - comment.timestamp;

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