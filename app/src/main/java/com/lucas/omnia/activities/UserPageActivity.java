package com.lucas.omnia.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucas.omnia.R;
import com.lucas.omnia.models.User;
import com.lucas.omnia.utils.ImageLoadAsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

public class UserPageActivity extends BaseActivity {

    public static final String EXTRA_USER_KEY = "user_key";
    private static final String TAG = "UserPageActivity";

    private String userKey;
    private URL userImgUrl;
    private ImageView userImgView;
    private TextView usernameTv;
    private TextView subCountTv;
    private TextView descriptionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        userImgView = findViewById(R.id.user_page_iv);
        usernameTv = findViewById(R.id.user_page_tv_name);
        subCountTv = findViewById(R.id.user_page_tv_sub_count);
        descriptionTv = findViewById(R.id.user_page_tv_description);

        // Get user key from intent
        userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        if (userKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        setupUserPage();
    }

    private void setupUserPage() {
        DatabaseReference databaseReference = getDatabaseReference();
        databaseReference.child("users").child(userKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        if (u == null) {
                            Log.e(TAG, "User " + userKey + " is unexpectedly null");
                            Toast.makeText(UserPageActivity.this,
                                    getString(R.string.user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            usernameTv.setText(u.username);
                            subCountTv.setText(String.valueOf(u.subCount));
                            if (u.photoUrl != null) fetchProfileImage();
                            if (u.description != null) descriptionTv.setText(u.description);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void fetchProfileImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImgRef = storageRef.child(userKey + "/profile-picture/profile.jpg");
        profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                userImgUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask(userImgUrl, userImgView);
            imageLoadAsyncTask.execute();
        }).addOnFailureListener(exception -> {
            Toast.makeText(UserPageActivity.this, "Error: could not fetch image",
                    Toast.LENGTH_SHORT).show();
        });
    }
}
