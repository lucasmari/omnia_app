package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lucas.omnia.R;
import com.lucas.omnia.authenticator.AuthActivity;
import com.lucas.omnia.authenticator.SessionManager;
import com.lucas.omnia.data.SQLiteHandler;

/**
 * Created by Lucas on 07/02/2018.
 */

public class UserSettingsActivity extends AppCompatActivity {

    private TextView mLogOut;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());

        mLogOut = findViewById(R.id.logOut);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logOut() {
        session.setLogin(false);
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(UserSettingsActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
