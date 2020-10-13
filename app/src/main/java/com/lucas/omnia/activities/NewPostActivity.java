package com.lucas.omnia.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityNewPostBinding;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.models.User;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference databaseReference;

    private ActivityNewPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();

        binding.newPostFabSubmit.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        final String title = binding.newPostEtTitle.getText().toString();
        final String body = binding.newPostEtBody.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding.newPostEtTitle.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            binding.newPostEtBody.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, getString(R.string.new_post_toast_posting), Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(userId, user.username, title, body);
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
        binding.newPostEtTitle.setEnabled(enabled);
        binding.newPostEtBody.setEnabled(enabled);
        if (enabled) {
            binding.newPostFabSubmit.show();
        } else {
            binding.newPostFabSubmit.hide();
        }
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new item_post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = databaseReference.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        databaseReference.updateChildren(childUpdates);

        // Set timestamp
        databaseReference.child("posts").child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
        databaseReference.child("user-posts").child(userId).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
    }
}
