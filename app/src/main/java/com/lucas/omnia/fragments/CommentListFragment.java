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
import com.lucas.omnia.activities.EditCommentActivity;
import com.lucas.omnia.activities.NewReplyActivity;
import com.lucas.omnia.activities.RepliesActivity;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.utils.VerticalSpaceItemDecoration;
import com.lucas.omnia.viewholder.CommentViewHolder;

import static com.lucas.omnia.activities.CommentsActivity.addFab;
import static com.lucas.omnia.activities.CommentsActivity.postKey;
import static com.lucas.omnia.activities.CommentsActivity.postReference;

public class CommentListFragment extends Fragment {

    private static final String TAG = "CommentListFragment";

    private DatabaseReference databaseReference;

    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> recyclerAdapter;
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
        Query CommentsQuery = getQuery(databaseReference);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(CommentsQuery, Comment.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {

            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new CommentViewHolder(inflater.inflate(R.layout.item_comment, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(CommentViewHolder viewHolder, int position, final Comment comment) {
                final DatabaseReference commentRef = getRef(position);
                final String commentKey = commentRef.getKey();

                // Determine if the Comment was edited
                if (comment.edited) {
                    viewHolder.editedView.setVisibility(View.VISIBLE);
                }

                // Determine if the current user has upvoted this item_comment and set UI accordingly
                if (comment.upVotes.containsKey(getUid())) {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }

                // Determine if the current user has downvoted this item_comment and set UI accordingly
                if (comment.downVotes.containsKey(getUid())) {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }

                // Bind Comment to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToComment(comment, upVoteButton -> {
                    // Need to write to both places the item_Comment is stored
                    DatabaseReference globalCommentRef = databaseReference.child("post-comments").child(postKey).child(commentRef.getKey());

                    // Run two transactions
                    onUpVoteClicked(globalCommentRef);
                }, downVoteButton -> {
                    // Need to write to both places the item_Comment is stored
                    DatabaseReference globalCommentRef = databaseReference.child("post-comments").child(postKey).child(commentRef.getKey());

                    // Run two transactions
                    onDownVoteClicked(globalCommentRef);
                });

                viewHolder.moreButton.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    if(getUid().equals(comment.uid)) {
                        builder.setItems(getResources().getStringArray(R.array.options1), (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    editComment(commentRef);
                                    break;
                                case 1:
                                    deleteComment(v.getContext(), commentRef);
                                    decrementCommentsCount();
                                    break;
                            }
                        });
                        builder.show();
                    }
                    else {
                        builder.setItems(getResources().getStringArray(R.array.options2), (dialog, which) -> {
                            if (which == 0) {
                                //reportComment();
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

                if (comment.replyCount > 0) {
                    viewHolder.repliesLayout.setVisibility(View.VISIBLE);
                    viewHolder.repliesButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), RepliesActivity.class);
                        intent.putExtra(RepliesActivity.EXTRA_COMMENT_KEY, commentKey);
                        startActivity(intent);
                    });
                }
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
        int VERTICAL_ITEM_SPACE = 48;
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addFab.getVisibility() == View.VISIBLE) {
                    addFab.hide();
                }
                else if (dy < 0 && addFab.getVisibility() != View.VISIBLE) {
                    addFab.show();
                }
            }
        });
    }

    private void onUpVoteClicked(DatabaseReference commentRef) {
        commentRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment c = mutableData.getValue(Comment.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                if (c.downVotes.containsKey(getUid())) {
                    // Remove downvote from item_Comment
                    c.downVoteCount = c.downVoteCount - 1;
                    c.downVotes.remove(getUid());
                }

                if (c.upVotes.containsKey(getUid())) {
                    // Unvote the item_Comment and remove self from votes
                    c.upVoteCount = c.upVoteCount - 1;
                    c.upVotes.remove(getUid());
                } else {
                    // Upvote the item_Comment and add self to votes
                    c.upVoteCount = c.upVoteCount + 1;
                    c.upVotes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "CommentTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onDownVoteClicked(DatabaseReference commentRef) {
        commentRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment c = mutableData.getValue(Comment.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                if (c.upVotes.containsKey(getUid())) {
                    // Remove upvote from item_Comment
                    c.upVoteCount = c.upVoteCount - 1;
                    c.upVotes.remove(getUid());
                }

                if (c.downVotes.containsKey(getUid())) {
                    // Unvote the item_Comment and remove self from votes
                    c.downVoteCount = c.downVoteCount - 1;
                    c.downVotes.remove(getUid());
                } else {
                    // Downvote the item_Comment and add self to // run some codevotes
                    c.downVoteCount = c.downVoteCount + 1;
                    c.downVotes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "CommentTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void editComment(DatabaseReference commentRef) {
        String CommentKey = commentRef.getKey();
        Intent intent = new Intent(getActivity(), EditCommentActivity.class);
        intent.putExtra(EditCommentActivity.EXTRA_COMMENT_KEY, CommentKey);
        startActivity(intent);
    }

    private void deleteComment(Context context, DatabaseReference commentRef) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Deseja realmente apagar esse comentário?")
                .setPositiveButton("Sim", (dialog1, which1) -> {
                    Toast.makeText(context, "Apagando...", Toast.LENGTH_SHORT).show();
                    databaseReference.child("post-comments").child(postKey).child(commentRef.getKey()).removeValue();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void decrementCommentsCount() {
        postReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                // Decrement commentsCount of item_post
                p.commentCount = p.commentCount - 1;

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "CommentTransaction:onComplete:" + databaseError);
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
        Query topCommentsQuery = databaseReference.child("post-comments")
                .child(postKey).orderByChild("upVoteCount").limitToFirst(100);

        return topCommentsQuery;
    };
}
