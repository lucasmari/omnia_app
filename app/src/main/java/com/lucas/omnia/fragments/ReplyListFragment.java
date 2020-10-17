package com.lucas.omnia.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.EditReplyActivity;
import com.lucas.omnia.activities.NewReplyActivity;
import com.lucas.omnia.activities.UserPageActivity;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.Reply;
import com.lucas.omnia.viewholder.ReplyViewHolder;

import static com.lucas.omnia.activities.RepliesActivity.commentKey;
import static com.lucas.omnia.activities.RepliesActivity.commentReference;

public class ReplyListFragment extends Fragment {

    private static final String TAG = "ReplyListFragment";

    private DatabaseReference databaseReference;

    private FirebaseRecyclerAdapter<Reply, ReplyViewHolder> recyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = rootView.findViewById(R.id.posts_rv);
        recyclerView.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query RepliesQuery = getQuery(databaseReference);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Reply>()
                .setQuery(RepliesQuery, Reply.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Reply, ReplyViewHolder>(options) {

            @Override
            public ReplyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ReplyViewHolder(inflater.inflate(R.layout.item_reply, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(ReplyViewHolder viewHolder, int position, final Reply reply) {
                final DatabaseReference replyRef = getRef(position);
                final String replyKey = replyRef.getKey();

                // Determine if the Reply was edited
                if (reply.edited) {
                    viewHolder.editedView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.editedView.setVisibility(View.GONE);
                }

                // Determine if the current user has upvoted this item_reply and set UI accordingly
                if (reply.upVotes.containsKey(getUid())) {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccentBlue), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.upVoteButton.setColorFilter(viewHolder.upVoteButton.getSolidColor());
                }

                // Determine if the current user has downvoted this item_reply and set UI accordingly
                if (reply.downVotes.containsKey(getUid())) {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccentRed), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.downVoteButton.setColorFilter(viewHolder.downVoteButton.getSolidColor());
                }

                if (!getUid().equals(reply.uid)) {
                    viewHolder.authorView.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), UserPageActivity.class);
                        intent.putExtra(UserPageActivity.EXTRA_USER_KEY, reply.uid);
                        startActivity(intent);
                    });
                }

                DatabaseReference globalReplyRef =
                        databaseReference.child("comment-replies").child(commentKey).child(replyKey);
                viewHolder.bindToReply(reply, upVoteButton -> {
                    onUpVoteClicked(globalReplyRef);
                }, downVoteButton -> {
                    onDownVoteClicked(globalReplyRef);
                });

                viewHolder.moreButton.setOnClickListener(v -> {
                    moreOptions(reply, replyKey);
                });

                viewHolder.replyButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), NewReplyActivity.class);
                    intent.putExtra(NewReplyActivity.EXTRA_COMMENT_KEY, commentKey);
                    intent.putExtra(NewReplyActivity.EXTRA_REPLY_KEY, replyKey);
                    startActivity(intent);
                });
            }
        };

        recyclerView.setAdapter(recyclerAdapter);
    }

    private void onUpVoteClicked(DatabaseReference replyRef) {
        replyRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Reply r = mutableData.getValue(Reply.class);
                if (r == null) {
                    return Transaction.success(mutableData);
                }

                if (r.downVotes.containsKey(getUid())) {
                    // Remove downvote from item_Reply
                    r.downVoteCount = r.downVoteCount - 1;
                    r.downVotes.remove(getUid());
                    r.votesBalance = r.votesBalance - 1;
                }

                if (r.upVotes.containsKey(getUid())) {
                    // Unvote the item_Reply and remove self from votes
                    r.upVoteCount = r.upVoteCount - 1;
                    r.upVotes.remove(getUid());
                    r.votesBalance = r.votesBalance - 1;
                } else {
                    // Upvote the item_Reply and add self to votes
                    r.upVoteCount = r.upVoteCount + 1;
                    r.upVotes.put(getUid(), true);
                    r.votesBalance = r.votesBalance + 1;
                }

                // Set value and report transaction success
                mutableData.setValue(r);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "ReplyTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onDownVoteClicked(DatabaseReference replyRef) {
        replyRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Reply r = mutableData.getValue(Reply.class);
                if (r == null) {
                    return Transaction.success(mutableData);
                }

                if (r.upVotes.containsKey(getUid())) {
                    // Remove upvote from item_Reply
                    r.upVoteCount = r.upVoteCount - 1;
                    r.upVotes.remove(getUid());
                    r.votesBalance = r.votesBalance - 1;
                }

                if (r.downVotes.containsKey(getUid())) {
                    // Unvote the item_Reply and remove self from votes
                    r.downVoteCount = r.downVoteCount - 1;
                    r.downVotes.remove(getUid());
                    r.votesBalance = r.votesBalance + 1;
                } else {
                    // Downvote the item_Reply and add self to votes
                    r.downVoteCount = r.downVoteCount + 1;
                    r.downVotes.put(getUid(), true);
                    r.votesBalance = r.votesBalance - 1;
                }

                // Set value and report transaction success
                mutableData.setValue(r);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "ReplyTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void moreOptions(Reply reply, String replyKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (getUid().equals(reply.uid)) {
            builder.setItems(getResources().getStringArray(R.array.options1), (dialog, which) -> {
                switch (which) {
                    case 0:
                        editReply(replyKey);
                        break;
                    case 1:
                        deleteReply(getContext(), replyKey);
                        decrementRepliesCount();
                        break;
                }
            });
            builder.show();
        }
        else {
            builder.setItems(getResources().getStringArray(R.array.options3), (dialog, which) -> {
                if (which == 0) {
                    //reportReply();
                }
            });
            builder.show();
        }
    }

    private void editReply(String replyKey) {
        Intent intent = new Intent(getActivity(), EditReplyActivity.class);
        intent.putExtra(EditReplyActivity.EXTRA_REPLY_KEY, replyKey);
        startActivity(intent);
    }

    private void deleteReply(Context context, String replyKey) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.reply_list_ad_delete))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive), (dialog1, which1) -> {
                    Toast.makeText(context, getString(R.string.reply_list_toast_deleting), Toast.LENGTH_SHORT).show();
                    databaseReference.child("comment-replies").child(commentKey).child(replyKey).removeValue();
                })
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
                .show();
    }

    private void decrementRepliesCount() {
        commentReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment c = mutableData.getValue(Comment.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                // Decrement repliesCount of item_post
                c.replyCount = c.replyCount - 1;

                // Set value and report transaction success
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "ReplyTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (recyclerAdapter != null) {
            recyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Query topRepliesQuery = databaseReference.child("comment-replies")
                .child(commentKey).orderByChild("votesBalance").limitToFirst(100);

        return topRepliesQuery;
    };
}
