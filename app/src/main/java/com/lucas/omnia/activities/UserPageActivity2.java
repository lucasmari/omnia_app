package com.lucas.omnia.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.lucas.omnia.R;

import static com.lucas.omnia.adapters.RecyclerViewAdapter1.pos;
import static com.lucas.omnia.fragments.TabFragment1.usersList;

public class UserPageActivity2 extends AppCompatActivity {

    private TextView mUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page_2);

        mUserName = findViewById(R.id.userName2);
        mUserName.setText(usersList.get(pos).toString());
    }
}
