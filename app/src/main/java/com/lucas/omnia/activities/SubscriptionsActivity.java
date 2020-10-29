package com.lucas.omnia.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.adapters.InspectionAdapter;
import com.lucas.omnia.adapters.SubscriptionsAdapter;
import com.lucas.omnia.models.Inspection;
import com.lucas.omnia.models.User;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsActivity extends BaseActivity {

    private static final String TAG = "SubscriptionsActivity";
    private final Context context = this;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subs);

        Toolbar toolbar = findViewById(R.id.subs_tb);
        setSupportActionBar(toolbar);

        DatabaseReference subsQuery = getDatabaseReference().child("users").child(getUid()).child("subs");

        recyclerView = findViewById(R.id.subs_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<User> userList = new ArrayList<>();
        subsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    User user = new User(userSnapshot.getKey(), userSnapshot.getValue().toString());
                    userList.add(user);
                }

                final SubscriptionsAdapter recyclerAdapter = new SubscriptionsAdapter(context,
                        userList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getUsersInSubs:onCancelled", error.toException());
            }
        });
    }
}
