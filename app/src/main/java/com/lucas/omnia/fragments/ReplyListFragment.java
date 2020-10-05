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
import com.lucas.omnia.utils.VerticalSpaceItemDecoration;
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

                // Determine if the Reply was edited
                if (reply.edited) {
                    viewHolder.editedView.setVisibility(View.VISIBLE);
                }

                // Determine if the current user has upvoted this item_reply and set UI accordingly
                if (reply.upVotes.containsKey(getUid())) {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }

                // Determine if the current user has downvoted this item_reply and set UI accordingly
                if (reply.downVotes.containsKey(getUid())) {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }

                if (!getUid().equals(reply.uid)) {
                    viewHolder.authorView.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), UserPageActivity.class);
                        intent.putExtra(UserPageActivity.EXTRA_USER_KEY, reply.uid);
                        startActivity(intent);
                    });
                }

                DatabaseReference globalReplyRef = databaseReference.child("comment-replies").child(commentKey).child(replyRef.getKey());
                viewHolder.bindToReply(reply, upVoteButton -> {
                    onUpVoteClicked(globalReplyRef);
                }, downVoteButton -> {
                    onDownVoteClicked(globalReplyRef);
                });

                viewHolder.moreButton.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    if (getUid().equals(reply.uid)) {
                        builder.setItems(getResources().getStringArray(R.array.options1), (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    editReply(replyRef);
                                    break;
                                case 1:
                                    deleteReply(v.getContext(), replyRef);
                                    decrementRepliesCount();
                                    break;
                            }
                        });
                        builder.show();
                    }
                    else {
                        builder.setItems(getResources().getStringArray(R.array.options2), (dialog, which) -> {
                            if (which == 0) {
                                //reportReply();
                            }
                        });
                        builder.show();
                    }
                });

                viewHolder.replyButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), NewReplyActivity.class);
                    intent.putExtra(NewReplyActivity.EXTRA_COMMENT_KEY, commentKey);
                    startActivity(intent);
                });
            }
        };

        recyclerView.setAdapter(recyclerAdapter);
        int VERTICAL_ITEM_SPACE = 48;
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
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
                }

                if (r.upVotes.containsKey(getUid())) {
                    // Unvote the item_Reply and remove self from votes
                    r.upVoteCount = r.upVoteCount - 1;
                    r.upVotes.remove(getUid());
                } else {
                    // Upvote the item_Reply and add self to votes
                    r.upVoteCount = r.upVoteCount + 1;
                    r.upVotes.put(getUid(), true);
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
                }

                if (r.downVotes.containsKey(getUid())) {
                    // Unvote the item_Reply and remove self from votes
                    r.downVoteCount = r.downVoteCount - 1;
                    r.downVotes.remove(getUid());
                } else {
                    // Downvote the item_Reply and add self to // run some codevotes
                    r.downVoteCount = r.downVoteCount + 1;
                    r.downVotes.put(getUid(), true);
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

    private void editReply(DatabaseReference replyRef) {
        String replyKey = replyRef.getKey();
        Intent intent = new Intent(getActivity(), EditReplyActivity.class);
        intent.putExtra(EditReplyActivity.EXTRA_REPLY_KEY, replyKey);
        startActivity(intent);
    }

    private void deleteReply(Context context, DatabaseReference replyRef) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Deseja realmente apagar essa resposta?")
                .setPositiveButton("Sim", (dialog1, which1) -> {
                    Toast.makeText(context, "Apagando...", Toast.LENGTH_SHORT).show();
                    databaseReference.child("comment-replies").child(commentKey).child(replyRef.getKey()).removeValue();
                })
                .setNegativeButton("Não", null)
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
                .child(commentKey).orderByChild("upVoteCount").limitToFirst(100);

        return topRepliesQuery;
    };
}
