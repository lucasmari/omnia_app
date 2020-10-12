package com.lucas.omnia.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    private static final String STORAGE_PATH = "/profile-picture/profile.jpg";

    private String userKey;
    private URL userImgUrl;
    private ImageView userImgView;
    private TextView usernameTv;
    private TextView subCountTv;
    private TextView aboutTv;
    private Button subButton;

    DatabaseReference userRef;
    DatabaseReference currentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        userImgView = findViewById(R.id.user_page_iv);
        usernameTv = findViewById(R.id.user_page_tv_name);
        subCountTv = findViewById(R.id.user_page_tv_sub_count);
        aboutTv = findViewById(R.id.user_page_tv_about_body);

        // Get user key from intent
        userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        if (userKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        subButton = findViewById(R.id.user_page_bt_sub);
        if (userKey.equals(getUid())) subButton.setVisibility(View.GONE);
        else subButton.setOnClickListener(v -> verifySub());

        userRef = getDatabaseReference().child("users").child(userKey);
        currentUserRef = getDatabaseReference().child("users").child(getUid());

        setupUserPage();
    }

    private void setupUserPage() {
        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        if (u == null) {
                            Log.e(TAG, "User " + userKey + " is unexpectedly null");
                            Toast.makeText(UserPageActivity.this,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            setUser(u);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        currentUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        if (u == null) {
                            Log.e(TAG, "User " + userKey + " is unexpectedly null");
                            Toast.makeText(UserPageActivity.this,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (u.subs.containsKey(userKey))
                                subButton.setText(getString(R.string.user_page_bt_unsub));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void setUser(User u) {
        usernameTv.setText(u.username);
        subCountTv.setText(String.valueOf(u.subCount));
        if (u.hasPhoto) fetchProfileImage();
        if (u.about != null) aboutTv.setText(u.about);
    }

    private void fetchProfileImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImgRef = storageRef.child(userKey + STORAGE_PATH);
        profileImgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                userImgUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ImageLoadAsyncTask imageLoadAsyncTask = new ImageLoadAsyncTask(userImgUrl, userImgView);
            imageLoadAsyncTask.execute();
        }).addOnFailureListener(exception -> {
            Toast.makeText(UserPageActivity.this, getString(R.string.profile_toast_fetch_error),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void verifySub() {
        currentUserRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        if (u == null) {
                            Log.e(TAG, "User " + userKey + " is unexpectedly null");
                            Toast.makeText(UserPageActivity.this,
                                    getString(R.string.new_post_toast_user_fetch_error),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (u.subs.containsKey(userKey)) {
                                decrementSubCount();
                                unsetSub(u);
                            } else {
                                incrementSubCount();
                                setSub(u);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void decrementSubCount() {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }

                u.subCount = u.subCount - 1;
                subCountTv.setText(String.valueOf(u.subCount));

                // Set value and report transaction success
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
        Toast.makeText(this, getString(R.string.user_page_unsub),
                Toast.LENGTH_SHORT).show();
    }

    private void unsetSub(User u) {
        u.subs.remove(userKey);
        currentUserRef.child("subs").setValue(u.subs);
        subButton.setText(getString(R.string.user_page_bt_sub));
    }

    private void incrementSubCount() {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }

                u.subCount = u.subCount + 1;
                subCountTv.setText(String.valueOf(u.subCount));

                // Set value and report transaction success
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
        Toast.makeText(this, getString(R.string.user_page_sub),
                Toast.LENGTH_SHORT).show();
    }

    private void setSub(User u) {
        u.subs.put(userKey, true);
        currentUserRef.child("subs").setValue(u.subs);
        subButton.setText(getString(R.string.user_page_bt_unsub));
    }
}
