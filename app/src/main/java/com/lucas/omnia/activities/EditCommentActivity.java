package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.databinding.ActivityEditCommentBinding;
import com.lucas.omnia.models.Comment;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.lucas.omnia.activities.CommentsActivity.postKey;

public class EditCommentActivity extends BaseActivity {

    private static final String TAG = "EditCommentActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_COMMENT_KEY = "comment_key";

    private ActivityEditCommentBinding binding;
    private String commentKey;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        // Get item_Comment key from intent
        commentKey = getIntent().getStringExtra(EXTRA_COMMENT_KEY);
        if (commentKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_COMMENT_KEY");
        }

        // Initialize Database
        DatabaseReference commentReference = databaseReference.child("post-comments")
                .child(postKey).child(commentKey);

        commentReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        binding.editCommentEtBody.setText(comment.body);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadComment:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });

        binding.editCommentFabSubmit.setOnClickListener(v -> submitComment());
    }

    private void submitComment() {
        final String body = binding.editCommentEtBody.getText().toString();
        final boolean edited = true;

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.editCommentEtBody.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-Comments
        setEditingEnabled(false);
        Toast.makeText(this, "Commenting...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(EditCommentActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            updateComment(userId, user.username, body, edited);
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
        binding.editCommentEtBody.setEnabled(enabled);
        if (enabled) {
            binding.editCommentFabSubmit.show();
        } else {
            binding.editCommentFabSubmit.hide();
        }
    }

    private void updateComment(String userId, String username, String body, boolean edited) {
        // Update item_Comment at /post-comments/postId/commentId
        Comment comment = new Comment(userId, username, body, edited);
        Map<String, Object> commentValues = comment.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/post-comments/" + postKey + "/" + commentKey, commentValues);

        databaseReference.updateChildren(childUpdates);
    }
}
