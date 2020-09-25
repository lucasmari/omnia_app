package com.lucas.omnia.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.databinding.ActivityEditPostBinding;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends BaseActivity {

    private static final String TAG = "EditPostActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_POST_KEY = "post_key";

    private ActivityEditPostBinding binding;
    private String postKey;
    private DatabaseReference postReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        // Get item_post key from intent
        postKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (postKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        postReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postKey);

        postReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        binding.fieldTitle2.setText(post.title);
                        binding.fieldBody2.setText(post.body);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });

        binding.fabSubmitPost2.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        final String title = binding.fieldTitle2.getText().toString();
        final String body = binding.fieldBody2.getText().toString();
        final boolean edited = true;

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding.fieldTitle2.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.fieldBody2.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(EditPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            updatePost(userId, user.username, title, body, edited);
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
        binding.fieldTitle2.setEnabled(enabled);
        binding.fieldBody2.setEnabled(enabled);
        if (enabled) {
            binding.fabSubmitPost2.show();
        } else {
            binding.fabSubmitPost2.hide();
        }
    }

    private void updatePost(String userId, String username, String title, String body, boolean edited) {
        // Update item_post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Post post = new Post(userId, username, title, body, edited);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + postKey, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + postKey, postValues);

        databaseReference.updateChildren(childUpdates);
    }
}
