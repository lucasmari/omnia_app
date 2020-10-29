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
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.CommentsActivity;
import com.lucas.omnia.activities.EditPostActivity;
import com.lucas.omnia.activities.UserPageActivity;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.utils.ImageLoadAsyncTask;
import com.lucas.omnia.viewholder.PostViewHolder;

import java.net.MalformedURLException;
import java.net.URL;

import static com.lucas.omnia.fragments.FeedNavFragment.addFab;

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    private DatabaseReference databaseReference;
    private StorageReference storageRef;

    private URL postImgUrl;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> recyclerAdapter;
    private RecyclerView recyclerView;

    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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
                final String postKey = postRef.getKey();

                // Determine if the post has image/video
                if (post.hasImage) {
                    viewHolder.bodyView.setVisibility(View.GONE);
                    fetchProfileImage(post.uid, postKey, viewHolder.bodyImageView);
                } else {
                    viewHolder.bodyView.setVisibility(View.VISIBLE);
                    viewHolder.bodyImageView.setVisibility(View.GONE);
                }

                // Determine if the post was edited
                if (post.edited) {
                    viewHolder.editedView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.editedView.setVisibility(View.GONE);
                }

                // Determine if the current user has upvoted this item_post and set UI accordingly
                if (post.upVotes.containsKey(getUid())) {
                    viewHolder.upVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccentBlue), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.upVoteButton.setColorFilter(viewHolder.upVoteButton.getSolidColor());
                }

                // Determine if the current user has downvoted this item_post and set UI accordingly
                if (post.downVotes.containsKey(getUid())) {
                    viewHolder.downVoteButton.setColorFilter(getResources()
                            .getColor(R.color.colorAccentRed), PorterDuff.Mode.SRC_ATOP);
                } else {
                    viewHolder.downVoteButton.setColorFilter(viewHolder.downVoteButton.getSolidColor());
                }

                viewHolder.authorView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), UserPageActivity.class);
                    intent.putExtra(UserPageActivity.EXTRA_USER_KEY, post.uid);
                    startActivity(intent);
                });

                DatabaseReference globalPostRef = databaseReference.child("posts").child(postKey);
                DatabaseReference userPostRef =
                        databaseReference.child("user-posts").child(post.uid).child(postKey);
                viewHolder.bindToPost(post, upVoteButton -> {
                    // Run two transactions
                    onUpVoteClicked(globalPostRef);
                    onUpVoteClicked(userPostRef);
                }, downVoteButton -> {
                    // Run two transactions
                    onDownVoteClicked(globalPostRef);
                    onDownVoteClicked(userPostRef);
                });

                viewHolder.commentButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), CommentsActivity.class);
                    intent.putExtra(CommentsActivity.EXTRA_POST_KEY, postKey);
                    startActivity(intent);
                });

                viewHolder.shareButton.setOnClickListener(v -> {
                    sharePost(post);
                });

                viewHolder.moreButton.setOnClickListener(v -> {
                    moreOptions(post, postKey);
                });
            }
        };

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addFab.getVisibility() == View.VISIBLE) {
                    addFab.hide();
                } else if (dy < 0 && addFab.getVisibility() != View.VISIBLE) {
                    addFab.show();
                }
            }
            });
    }

    private void fetchProfileImage(String userId, String postKey, ImageView postImgView) {
        StorageReference postImgRef = storageRef.child(userId + "/posts/" + postKey);
        postImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                postImgUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask(postImgUrl,
                    postImgView, false);
            imageLoadAsyncTask.execute();
            postImgView.setVisibility(View.VISIBLE);
        }).addOnFailureListener(exception -> {
            Toast.makeText(getContext(), getString(R.string.profile_toast_fetch_error), Toast.LENGTH_SHORT).show();
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
                    p.votesBalance = p.votesBalance - 1;
                }

                if (p.upVotes.containsKey(getUid())) {
                    // Unvote the item_post and remove self from votes
                    p.upVoteCount = p.upVoteCount - 1;
                    p.upVotes.remove(getUid());
                    p.votesBalance = p.votesBalance - 1;
                } else {
                    // Upvote the item_post and add self to votes
                    p.upVoteCount = p.upVoteCount + 1;
                    p.upVotes.put(getUid(), true);
                    p.votesBalance = p.votesBalance + 1;
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
                    p.votesBalance = p.votesBalance - 1;
                }

                if (p.downVotes.containsKey(getUid())) {
                    // Unvote the item_post and remove self from votes
                    p.downVoteCount = p.downVoteCount - 1;
                    p.downVotes.remove(getUid());
                    p.votesBalance = p.votesBalance + 1;
                } else {
                    // Downvote the item_post and add self to votes
                    p.downVoteCount = p.downVoteCount + 1;
                    p.downVotes.put(getUid(), true);
                    p.votesBalance = p.votesBalance - 1;
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

    private void sharePost(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TITLE, post.title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.body);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    private void moreOptions(Post post, String postKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (!getUid().equals(post.uid)) {
            builder.setItems(getResources().getStringArray(R.array.options3), (dialog, which) -> {
                if (which == 0) {
                    //reportPost();
                }
            });
            builder.show();
        }
        else if (post.hasImage) {
            builder.setItems(getResources().getStringArray(R.array.options2), (dialog, which) -> {
                if (which == 0) deletePost(getContext(), postKey, post);
            });
            builder.show();
        } else {
            builder.setItems(getResources().getStringArray(R.array.options1), (dialog, which) -> {
                switch (which) {
                    case 0:
                        editPost(postKey);
                        break;
                    case 1:
                        deletePost(getContext(), postKey, post);
                        break;
                }
            });
            builder.show();
        }
    }

    private void editPost(String postKey) {
        Intent intent = new Intent(getActivity(), EditPostActivity.class);
        intent.putExtra(EditPostActivity.EXTRA_POST_KEY, postKey);
        startActivity(intent);
    }

    private void deletePost(Context context, String postKey, Post post) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.post_list_ad_delete))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive), (dialog1, which1) -> {
                    Toast.makeText(context, getString(R.string.post_list_toast_deleting), Toast.LENGTH_SHORT).show();

                    databaseReference.child("posts").child(postKey).removeValue();
                    databaseReference.child("user-posts").child(post.uid).child(postKey).removeValue();

                    StorageReference postImgRef = storageRef.child(post.uid + "/posts/" + postKey);

                    postImgRef.delete().addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, getString(R.string.post_list_toast_success),
                                Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(context, getString(R.string.post_list_toast_failure), Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
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
