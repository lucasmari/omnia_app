package com.lucas.omnia.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.adapters.SearchUserAdapter;
import com.lucas.omnia.models.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lucas on 21/01/2018.
 */

public class SearchUserActivity extends BaseActivity {

    private static final String TAG = "SearchUserActivity";
    private final Context context = this;
    private DatabaseReference usersReference;
    private TextView noneTv;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        noneTv = findViewById(R.id.search_user_tv_none);
        recyclerView = findViewById(R.id.search_results_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        usersReference = getDatabaseReference().child("users");

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String queryString = intent.getStringExtra(SearchManager.QUERY);
            search(queryString);
        }
    }

    private void search(String queryString) {
        // Set up FirebaseRecyclerAdapter with the Query
        Query usersQuery =
                usersReference.orderByChild("username").startAt(queryString).endAt(queryString +
                        "\uf8ff");

        List<User> userList = new ArrayList<>();
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = new User(userSnapshot.getKey(),
                            userSnapshot.child("username").getValue().toString());
                    userList.add(user);
                }

                final SearchUserAdapter recyclerAdapter = new SearchUserAdapter(context, userList);

                if (recyclerAdapter.getItemCount() == 0) noneTv.setVisibility(View.VISIBLE);
                else recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getUsersByUsername:onCancelled", error.toException());
            }
        });
    }
}
