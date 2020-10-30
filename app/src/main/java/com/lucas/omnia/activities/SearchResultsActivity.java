package com.lucas.omnia.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lucas.omnia.R;
import com.lucas.omnia.adapters.SearchResultsAdapter;
import com.lucas.omnia.adapters.SubscriptionsAdapter;
import com.lucas.omnia.models.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lucas on 21/01/2018.
 */

public class SearchResultsActivity extends BaseActivity {

    private static final String TAG = "SearchResultsActivity";
    private final Context context = this;
    private DatabaseReference usersReference;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

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

                final SearchResultsAdapter recyclerAdapter = new SearchResultsAdapter(context,
                        userList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getUsersByUsername:onCancelled", error.toException());
            }
        });
    }
}
