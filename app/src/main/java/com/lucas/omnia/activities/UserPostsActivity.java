package com.lucas.omnia.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.lucas.omnia.R;
import com.lucas.omnia.fragments.UserPostsTabFragment;

public class UserPostsActivity extends BaseActivity {

    public static final String EXTRA_USER_KEY = "user_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        Toolbar toolbar = findViewById(R.id.user_posts_tb);
        setSupportActionBar(toolbar);

        // Get user key from intent
        String userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        if (userKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.user_posts_fcv, new UserPostsTabFragment(userKey))
                .commit();
    }
}
