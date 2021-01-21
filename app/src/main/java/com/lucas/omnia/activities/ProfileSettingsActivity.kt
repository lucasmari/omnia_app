package com.lucas.omnia.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lucas.omnia.R
import com.lucas.omnia.databinding.ActivityProfileSettingsBinding

class ProfileSettingsActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityProfileSettingsBinding? = null
    private var user: FirebaseUser? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val toolbar = findViewById<Toolbar>(R.id.profile_settings_tb)
        setSupportActionBar(toolbar)
        user = FirebaseAuth.getInstance().currentUser
        binding!!.profileSettingsBtSignout.setOnClickListener(this)
        binding!!.profileSettingsBtDeleteAccount.setOnClickListener(this)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        // Launching the sign in activity
        val intent = Intent(this@ProfileSettingsActivity, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun deleteAccount() {
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.profile_settings_ad_delete_account))
                .setPositiveButton(getString(R.string.alert_dialog_bt_positive)) { _: DialogInterface?, _: Int ->
                    Toast.makeText(this, getString(R.string.profile_settings_toast_delete_account), Toast.LENGTH_SHORT).show()
                    user!!.delete()
                            .addOnCompleteListener { task: Task<Void?> ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User account deleted.")
                                } /*else {
                        reauthenticate();
                    }*/
                            }
                }
                .setNegativeButton(getString(R.string.alert_dialog_bt_negative), null)
                .show()
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
    override fun onClick(v: View) {
        when (val i = v.id) {
            R.id.profile_settings_bt_signout -> signOut()
            R.id.profile_settings_bt_delete_account -> deleteAccount()
            else -> throw IllegalStateException("Unexpected value: $i")
        }
    }

    companion object {
        private const val TAG = "ProfileSettingsActivity"
    }
}