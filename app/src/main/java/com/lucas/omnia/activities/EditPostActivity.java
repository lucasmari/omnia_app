package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
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

                        binding.editPostEtTitle.setText(post.title);
                        binding.editPostEtBody.setText(post.body);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });

        binding.editPostFabSubmit.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        final String title = binding.editPostEtTitle.getText().toString();
        final String body = binding.editPostEtBody.getText().toString();
        final boolean edited = true;

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding.editPostEtTitle.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.editPostEtBody.setError(REQUIRED);
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
        binding.editPostEtTitle.setEnabled(enabled);
        binding.editPostEtBody.setEnabled(enabled);
        if (enabled) {
            binding.editPostFabSubmit.show();
        } else {
            binding.editPostFabSubmit.hide();
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
