package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.lucas.omnia.R;

/**
 * Created by Lucas on 07/02/2018.
 */

public class ProfileSettingsActivity extends BaseActivity {

    EditText descriptionEt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Toolbar toolbar = findViewById(R.id.profile_settings_tb);
        setSupportActionBar(toolbar);

        descriptionEt = findViewById(R.id.profile_settings_et_description);

        Button saveBt = findViewById(R.id.profile_settings_bt_save);
        saveBt.setOnClickListener(v -> writeDescription());

        Button signOutBt = findViewById(R.id.profile_settings_bt_signout);
        signOutBt.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // Launching the sign in activity
            Intent intent = new Intent(ProfileSettingsActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void writeDescription() {
        DatabaseReference databaseReference = getDatabaseReference();
        databaseReference.child("users").child(getUid()).child("description").setValue(descriptionEt.getText().toString());
        hideSoftKeyboard(this, descriptionEt);
        Toast.makeText(this, getString(R.string.profile_settings_toast_description),
                Toast.LENGTH_SHORT).show();
    }
}
