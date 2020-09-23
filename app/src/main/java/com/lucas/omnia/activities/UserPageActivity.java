package com.lucas.omnia.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lucas.omnia.R;

import static com.lucas.omnia.activities.MainActivity.userName;

public class UserPageActivity extends AppCompatActivity {

    private TextView mUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        mUserName = findViewById(R.id.userName2);
        mUserName.setText(userName);
    }
}
