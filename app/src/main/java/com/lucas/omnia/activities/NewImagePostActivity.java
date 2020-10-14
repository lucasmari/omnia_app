package com.lucas.omnia.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityNewImagePostBinding;
import com.lucas.omnia.models.Post;
import com.lucas.omnia.models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewImagePostActivity extends BaseActivity {

    private static final String TAG = "NewImagePostActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_DATA = "data";

    private Uri dataUri;

    private StorageReference storageRef;
    private DatabaseReference databaseReference;

    private ActivityNewImagePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewImagePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = getDatabaseReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Get post key from intent
        dataUri = Uri.parse(getIntent().getStringExtra(EXTRA_DATA));
        if (dataUri == null) {
            throw new IllegalArgumentException("Must pass EXTRA_DATA");
        }

        setImage();

        binding.newImagePostFabSubmit.setOnClickListener(v -> submitPost());
    }

    private void setImage() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dataUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.newImagePostIvBody.setImageBitmap(bitmap);
    }

    private void submitPost() {
        final String title = binding.newImagePostEtTitle.getText().toString();
        final String body = "";

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding.newImagePostEtTitle.setError(REQUIRED);
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
                            Toast.makeText(NewImagePostActivity.this,
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
        binding.newImagePostEtTitle.setEnabled(enabled);
        if (enabled) {
            binding.newImagePostFabSubmit.show();
        } else {
            binding.newImagePostFabSubmit.hide();
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

        // Save image/video in storage
        StorageReference profileImgRef = storageRef.child(userId + "/posts/" + key);
        profileImgRef.putFile(dataUri);

        // Set hasImage in database
        databaseReference.child("posts").child(key).child("hasImage").setValue(true);
        databaseReference.child("user-posts").child(userId).child(key).child("hasImage").setValue(true);
    }
}
