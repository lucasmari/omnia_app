package com.lucas.omnia.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DatabaseReference;
import com.lucas.omnia.R;
import com.lucas.omnia.fragments.ReplyListFragment;

import static com.lucas.omnia.activities.CommentsActivity.postKey;

public class RepliesActivity extends BaseActivity {

    public static final String EXTRA_COMMENT_KEY = "comment_key";

    public static String commentKey;
    public static DatabaseReference commentReference;
    public static DatabaseReference repliesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);

        Toolbar toolbar = findViewById(R.id.comments_tb);
        setSupportActionBar(toolbar);

        // Get comment key from intent
        commentKey = getIntent().getStringExtra(EXTRA_COMMENT_KEY);
        if (commentKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_COMMENT_KEY");
        }

        // Initialize Database
        commentReference = getDatabaseReference().child("post-comments").child(postKey).child(commentKey);
        repliesReference = getDatabaseReference().child("comment-replies").child(commentKey);

        Fragment fragment = null;
        fragment = new ReplyListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.replies_fcv, fragment).commit();
    }
}
