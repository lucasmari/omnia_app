package com.lucas.omnia.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lucas.omnia.R;
import com.lucas.omnia.databinding.ActivityProfileSettingsBinding;

/**
 * Created by Lucas on 07/02/2018.
 */

public class ProfileSettingsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityProfileSettingsBinding binding;
    private FirebaseUser user;

    private static final String TAG = "ProfileSettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.profileSettingsBtSignout.setOnClickListener(this);
        binding.profileSettingsBtDeleteAccount.setOnClickListener(this);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        // Launching the sign in activity
        Intent intent = new Intent(ProfileSettingsActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteAccount() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.profile_settings_ad_delete_account))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive), (dialog1, which1) -> {
                    Toast.makeText(this, getString(R.string.profile_settings_toast_delete_account), Toast.LENGTH_SHORT).show();
                    user.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                } /*else {
                        reauthenticate();
                    }*/
                            });
                })
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
                .show();

    }

    /*private void reauthenticate() {

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, pass);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User re-authenticated.");
                    }
                });
    }*/

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.profile_settings_bt_signout:
                signOut();
                break;
            case R.id.profile_settings_bt_delete_account:
                deleteAccount();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }
    }
}
