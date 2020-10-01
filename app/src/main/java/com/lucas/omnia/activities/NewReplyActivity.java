package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityNewReplyBinding;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.Reply;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.lucas.omnia.activities.CommentsActivity.postKey;

public class NewReplyActivity extends BaseActivity {

    public static final String EXTRA_COMMENT_KEY = "comment_key";
    private static final String TAG = "NewReplyActivity";
    private static final String REQUIRED = "Required";

    private String commentKey;
    private DatabaseReference databaseReference;
    private DatabaseReference commentReference;
    private String author;

    private ActivityNewReplyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        // Get comment key from intent
        commentKey = getIntent().getStringExtra(EXTRA_COMMENT_KEY);
        if (commentKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_COMMENT_KEY");
        }

        commentReference = databaseReference.child("post-comments").child(postKey).child(commentKey);
        commentReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        author = comment.author;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadReply:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });

        binding.newReplyFabSubmit.setOnClickListener(v -> submitReply());
    }

    private void submitReply() {
        final String body = "@" + author + " " + binding.newReplyEtBody.getText().toString();
        final boolean edited = false;

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.newReplyEtBody.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Replying...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewReplyActivity.this,
                                    getString(R.string.user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewReply(userId, user.username, body, edited);
                            incrementRepliesCount();
                        }
                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        binding.newReplyEtBody.setEnabled(enabled);
        if (enabled) {
            binding.newReplyFabSubmit.show();
        } else {
            binding.newReplyFabSubmit.hide();
        }
    }

    private void writeNewReply(String userId, String username, String body, boolean edited) {
        // Create new item_reply at /comment-replies
        String key = databaseReference.child("comment-replies").child(commentKey).push().getKey();
        Reply reply = new Reply(userId, username, body, edited);
        Map<String, Object> replyValues = reply.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/comment-replies/" + commentKey + "/" + key, replyValues);
        databaseReference.updateChildren(childUpdates);
    }

    private void incrementRepliesCount() {
        commentReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment c = mutableData.getValue(Comment.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                // Increment commentsCount of item_post
                c.replyCount = c.replyCount + 1;

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
}
