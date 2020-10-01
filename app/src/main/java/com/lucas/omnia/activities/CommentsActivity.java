package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.lucas.omnia.R;
import com.lucas.omnia.fragments.CommentListFragment;

public class CommentsActivity extends BaseActivity {

    public static final String EXTRA_POST_KEY = "post_key";

    public static String postKey;
    public static DatabaseReference postReference;
    public static DatabaseReference commentsReference;
    public static FloatingActionButton addFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.comments_tb);
        setSupportActionBar(toolbar);

        // Get post key from intent
        postKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (postKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        postReference = getDatabaseReference().child("posts").child(postKey);
        commentsReference = getDatabaseReference().child("post-comments").child(postKey);

        Fragment fragment = null;
        fragment = new CommentListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.comments_fcv, fragment).commit();

        addFab = findViewById(R.id.comments_fab_add);
        addFab.setOnClickListener(v -> startActivity(new Intent(v.getContext(), NewCommentActivity.class)));
    }
}