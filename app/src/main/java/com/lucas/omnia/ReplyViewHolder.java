package com.lucas.omnia;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ReplyViewHolder extends RecyclerView.ViewHolder {

    TextView mUser3;
    public TextView mComment3;
    public Button mReplyButton3;
    public TextView mInfo3;
    public ImageButton mUpVoteButton3;
    public ImageButton mDownVoteButton3;
    public ImageButton mMoreButton3;


    public ReplyViewHolder(View itemView) {
        super(itemView);

        mUser3 = itemView.findViewById(R.id.userTv3);
        mComment3 = itemView.findViewById(R.id.commentTv3);
        mReplyButton3 = itemView.findViewById(R.id.replyButton3);
        mInfo3 = itemView.findViewById(R.id.infoTv3);
        mUpVoteButton3 = itemView.findViewById(R.id.upVoteButton3);
        mDownVoteButton3 = itemView.findViewById(R.id.downVoteButton3);
        mMoreButton3 = itemView.findViewById(R.id.moreButton3);
    }
}
