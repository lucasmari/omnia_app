package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.lucas.omnia.R;

/**
 * Created by Lucas on 07/02/2018.
 */

public class UserSettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        TextView mLogOut = findViewById(R.id.logOut);
        mLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // Launching the login activity
            Intent intent = new Intent(UserSettingsActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
