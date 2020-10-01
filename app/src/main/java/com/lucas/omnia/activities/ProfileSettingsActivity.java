package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.lucas.omnia.R;

/**
 * Created by Lucas on 07/02/2018.
 */

public class ProfileSettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        TextView mLogOut = findViewById(R.id.profile_settings_tv_signout);
        mLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // Launching the login activity
            Intent intent = new Intent(ProfileSettingsActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
