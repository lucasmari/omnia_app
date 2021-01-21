package com.lucas.omnia.fragments;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lucas.omnia.R;
import com.lucas.omnia.models.User;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class UpVotedPostsTabFragment extends PostListFragment {

    private static final String TAG = "UpVotedPostsTabFragment";

    public UpVotedPostsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // UpVoted last 100 posts
        return databaseReference.child("posts").orderByChild("upVotes/" + getUid()).equalTo(true);
    }
}
