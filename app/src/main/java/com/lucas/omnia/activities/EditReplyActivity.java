package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.databinding.ActivityEditReplyBinding;
import com.lucas.omnia.models.Reply;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.lucas.omnia.activities.RepliesActivity.commentKey;

public class EditReplyActivity extends BaseActivity {

    private static final String TAG = "EditReplyActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_REPLY_KEY = "reply_key";

    private ActivityEditReplyBinding binding;
    private String replyKey;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        // Get item_Reply key from intent
        replyKey = getIntent().getStringExtra(EXTRA_REPLY_KEY);
        if (replyKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_REPLY_KEY");
        }

        // Initialize Database
        DatabaseReference replyReference = databaseReference.child("comment-replies")
                .child(commentKey).child(replyKey);

        replyReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Reply reply = dataSnapshot.getValue(Reply.class);

                        binding.editReplyEtBody.setText(reply.body);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadReply:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });

        binding.editReplyFabSubmit.setOnClickListener(v -> submitReply());
    }

    private void submitReply() {
        final String body = binding.editReplyEtBody.getText().toString();
        final boolean edited = true;

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.editReplyEtBody.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-Replies
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
                            Toast.makeText(EditReplyActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            updateReply(userId, user.username, body, edited);
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
        binding.editReplyEtBody.setEnabled(enabled);
        if (enabled) {
            binding.editReplyFabSubmit.show();
        } else {
            binding.editReplyFabSubmit.hide();
        }
    }

    private void updateReply(String userId, String username, String body, boolean edited) {
        // Update item_Reply at /comment-replies/commentId/replyId
        Reply reply = new Reply(userId, username, body, edited);
        Map<String, Object> replyValues = reply.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/comment-replies/" + commentKey + "/" + replyKey, replyValues);

        databaseReference.updateChildren(childUpdates);
    }
}
