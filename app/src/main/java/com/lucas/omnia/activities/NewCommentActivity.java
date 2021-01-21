package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityNewCommentBinding;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.lucas.omnia.activities.CommentsActivity.commentsReference;
import static com.lucas.omnia.activities.CommentsActivity.postKey;
import static com.lucas.omnia.activities.CommentsActivity.postReference;

public class NewCommentActivity extends BaseActivity {

    private static final String TAG = "NewCommentActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference databaseReference;

    private ActivityNewCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        binding.newCommentFabSubmit.setOnClickListener(v -> submitComment());
    }

    private void submitComment() {
        final String body = binding.newCommentEtBody.getText().toString();

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.newCommentEtBody.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, getString(R.string.new_comment_toast_commenting), Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewCommentActivity.this,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewComment(userId, user.getUsername(), body);
                            incrementCommentsCount();
                        }
                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        binding.newCommentEtBody.setEnabled(enabled);
        if (enabled) {
            binding.newCommentFabSubmit.show();
        } else {
            binding.newCommentFabSubmit.hide();
        }
    }

    private void writeNewComment(String userId, String username, String body) {
        // Create new item_comment at /post-comments
        String key = commentsReference.push().getKey();
        Comment comment = new Comment(userId, username, body);
        Map<String, Object> commentValues = comment.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/post-comments/" + postKey + "/" + key, commentValues);
        databaseReference.updateChildren(childUpdates);

        // Set timestamp
        databaseReference.child("post-comments").child(postKey).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
    }

    private void incrementCommentsCount() {
        postReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                // Increment commentsCount of item_post
                p.commentCount = p.commentCount + 1;

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
}
