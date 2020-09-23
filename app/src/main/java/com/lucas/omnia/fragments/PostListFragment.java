package com.lucas.omnia.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.lucas.omnia.VerticalSpaceItemDecoration;
import com.lucas.omnia.activities.CommentsActivity;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.viewholder.PostViewHolder;

import static com.lucas.omnia.fragments.NavFragment1.addFab;

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    private DatabaseReference databaseReference;

    private FirebaseRecyclerAdapter<Post, PostViewHolder> recyclerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView textView;

    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_tab_1, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = rootView.findViewById(R.id.rv_main);
        recyclerView.setHasFixedSize(true);
        swipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        textView = rootView.findViewById(R.id.noConnection);

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
        Query postsQuery = getQuery(databaseReference);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(PostViewHolder viewHolder, int position, final Post post) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole item_post view
                /*final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });*/

                // Determine if the current user has upvoted this item_post and set UI accordingly
                if (post.upVotes.containsKey(getUid())) {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }

                // Determine if the current user has downvoted this item_post and set UI accordingly
                if (post.downVotes.containsKey(getUid())) {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(post, upVoteButton -> {
                    // Need to write to both places the item_post is stored
                    DatabaseReference globalPostRef = databaseReference.child("posts").child(postRef.getKey());
                    DatabaseReference userPostRef = databaseReference.child("user-posts").child(post.uid).child(postRef.getKey());

                    // Run two transactions
                    onUpVoteClicked(globalPostRef);
                    onUpVoteClicked(userPostRef);
                }, downVoteButton -> {
                    // Need to write to both places the item_post is stored
                    DatabaseReference globalPostRef = databaseReference.child("posts").child(postRef.getKey());
                    DatabaseReference userPostRef = databaseReference.child("user-posts").child(post.uid).child(postRef.getKey());

                    // Run two transactions
                    onDownVoteClicked(globalPostRef);
                    onDownVoteClicked(userPostRef);
                });

                viewHolder.commentButton.setOnClickListener(v -> v.getContext().startActivity(new Intent(v.getContext(), CommentsActivity.class)));
                viewHolder.shareButton.setOnClickListener(v -> {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TITLE, post.title);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, post.body);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
                });
                viewHolder.moreButton.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    if(getUid().equals(post.uid)) {
                        builder.setItems(getResources().getStringArray(R.array.options1), (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    //editPost(v.getContext(), postRef, post);
                                    break;
                                case 1:
                                    deletePost(v.getContext(), postRef, post);
                                    break;
                            }
                        });
                        builder.show();
                    }
                    else {
                        builder.setItems(getResources().getStringArray(R.array.options2), (dialog, which) -> {
                            if (which == 0) {
                                //reportPost();
                            }
                        });
                        builder.show();
                    }
                });
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

    private void onUpVoteClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.downVotes.containsKey(getUid())) {
                    // Remove downvote from item_post
                    p.downVoteCount = p.downVoteCount - 1;
                    p.downVotes.remove(getUid());
                }

                if (p.upVotes.containsKey(getUid())) {
                    // Unvote the item_post and remove self from votes
                    p.upVoteCount = p.upVoteCount - 1;
                    p.upVotes.remove(getUid());
                } else {
                    // Upvote the item_post and add self to votes
                    p.upVoteCount = p.upVoteCount + 1;
                    p.upVotes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onDownVoteClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.upVotes.containsKey(getUid())) {
                    // Remove upvote from item_post
                    p.upVoteCount = p.upVoteCount - 1;
                    p.upVotes.remove(getUid());
                }

                if (p.downVotes.containsKey(getUid())) {
                    // Unvote the item_post and remove self from votes
                    p.downVoteCount = p.downVoteCount - 1;
                    p.downVotes.remove(getUid());
                } else {
                    // Downvote the item_post and add self to votes
                    p.downVoteCount = p.downVoteCount + 1;
                    p.downVotes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void editPost(Context context, DatabaseReference postRef, Post post) {

    }

    private void deletePost(Context context, DatabaseReference postRef, Post post) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Deseja realmente apagar esse post?")
                .setPositiveButton("Sim", (dialog1, which1) -> {
                    Toast.makeText(context, "Apagando...", Toast.LENGTH_SHORT).show();
                    databaseReference.child("posts").child(postRef.getKey()).removeValue();
                    databaseReference.child("user-posts").child(post.uid).child(postRef.getKey()).removeValue();
                })
                .setNegativeButton("Não", null)
                .show();
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

    public abstract Query getQuery(DatabaseReference databaseReference);
}
