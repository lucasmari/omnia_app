package com.lucas.omnia;

import android.graphics.PorterDuff;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private final HeaderViewHolderCallback callback;

    TextView mUser2;
    public TextView mComment2;
    public Button mReplyButton2;
    public TextView mInfo2;
    public ImageButton mUpVoteButton2;
    public ImageButton mDownVoteButton2;
    public ImageButton mMoreButton2;
    ImageButton mMoreButton4;

    public CommentViewHolder(View itemView, HeaderViewHolderCallback callback) {
        super(itemView);

        mUser2 = itemView.findViewById(R.id.userTv2);
        mComment2 = itemView.findViewById(R.id.commentTv2);
        mReplyButton2 = itemView.findViewById(R.id.replyButton2);
        mInfo2 = itemView.findViewById(R.id.infoTv2);
        mUpVoteButton2 = itemView.findViewById(R.id.upVoteButton2);
        mDownVoteButton2 = itemView.findViewById(R.id.downVoteButton2);
        mMoreButton2 = itemView.findViewById(R.id.moreButton2);
        mMoreButton4 = itemView.findViewById(R.id.moreButton4);

        mMoreButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick2(v);
            }
        });

        this.callback = callback;
    }

    public void onClick2(View v) {
        int position = getAdapterPosition();
        callback.onHeaderClick(position);
        if (callback.isExpanded(position)) {
            mMoreButton4.setColorFilter(v.getResources()
                    .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        } else {
            mMoreButton4.setColorFilter(v.getResources()
                    .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public interface HeaderViewHolderCallback {
        void onHeaderClick(int position);

        boolean isExpanded(int position);
    }
}