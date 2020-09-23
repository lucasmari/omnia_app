package com.lucas.omnia.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lucas.omnia.CustomRequest;
import com.lucas.omnia.models.DataObject;
import com.lucas.omnia.R;
import com.lucas.omnia.VerticalSpaceItemDecoration;
import com.lucas.omnia.adapters.RecyclerViewAdapter1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.Request.Method;
import static com.lucas.omnia.utils.AppController.getInstance;
import static com.lucas.omnia.activities.MainActivity.hideSoftKeyboard;
import static com.lucas.omnia.adapters.RecyclerViewAdapter1.getDownvotedPosts;
import static com.lucas.omnia.adapters.RecyclerViewAdapter1.getUpvotedPosts;

/**
 * Created by Lucas on 10/10/2017.
 */

public class TabFragment1 extends PostListFragment {

    public TabFragment1() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("posts")
                .limitToFirst(100);

        return recentPostsQuery;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tab_1, container, false);

        RecyclerView mRecyclerView = view.findViewById(R.id.rv_main);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter1(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        mSwipeRefresh = view.findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(
                () -> {
                    int i = mAdapter.getItemCount();
                    while(i != 0) {
                        ((RecyclerViewAdapter1) mAdapter).deleteItem(i-1);
                        i--;
                    }
                    if(isOnline()) {
                        textView = view.findViewById(R.id.noConnection);
                        textView.setVisibility(View.GONE);
                        getAllPosts(getContext());
                        getUpvotedPosts(getContext());
                        getDownvotedPosts(getContext());
                    }
                    else {
                        mSwipeRefresh.setRefreshing(false);
                        textView = view.findViewById(R.id.noConnection);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
        );

        if(isOnline()) {
            textView = view.findViewById(R.id.noConnection);
            textView.setVisibility(View.GONE);
            getAllPosts(getContext());
            getUpvotedPosts(getContext());
            getDownvotedPosts(getContext());
        }
        else {
            mSwipeRefresh.setRefreshing(false);
            textView = view.findViewById(R.id.noConnection);
            textView.setVisibility(View.VISIBLE);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                *//*if (dy > 0 && mAddFab.getVisibility() == View.VISIBLE) {
                    mAddFab.hide();
                    if(mRelativeLayout.getVisibility() == View.VISIBLE) {
                        hideSoftKeyboard(getContext(), mEditText);
                        mAddFab.startAnimation(mRotateBackward);
                        mAttachFab.startAnimation(mFabHide1);
                        mCameraFab.startAnimation(mFabHide1);
                        mAttachFab.setVisibility(View.GONE);
                        mCameraFab.setVisibility(View.GONE);
                        mEditText.getText().clear();
                        mRelativeLayout.startAnimation(mHide);
                        mRelativeLayout.setVisibility(View.GONE);
                        visible = !visible;
                    }
                }
                else if (dy < 0 && mAddFab.getVisibility() != View.VISIBLE) {
                    mAddFab.show();
                }*//*
            }
        });
        return view;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void getAllPosts(final Context context) {

        String url = "";

        mSwipeRefresh.setRefreshing(true);

        CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, null,
                response -> {
                    try {
                        // Checking for SUCCESS TAG
                        int success = response.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            int j = mAdapter.getItemCount();
                            while(j != 0) {
                                ((RecyclerViewAdapter1) mAdapter).deleteItem(j-1);
                                j--;
                            }
                            // posts found
                            mSwipeRefresh.setRefreshing(false);
                            // getting array of posts
                            posts = response.getJSONArray(TAG_POSTS);

                            // looping through all posts
                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject c = posts.optJSONObject(i);

                                // Storing each json item in variable
                                int id = c.getInt(TAG_PID);
                                String user = c.getString(TAG_USER);
                                String item_post = c.getString(TAG_POST);
                                String votes = c.getString(TAG_VOTES);

                                idList.add(i, id-1);
                                usersList.add(i, user);
                                postsList.add(i, item_post);
                                votesList.add(i, votes);

                                DataObject obj = new DataObject((int) idList.get(i),
                                        usersList.get(i).toString(),
                                        postsList.get(i).toString(),
                                        Integer.parseInt(votesList.get(i).toString()));
                                ((RecyclerViewAdapter1) mAdapter).addItem(obj,(int) idList.get(i));
                            }
                        } else {
                            // no posts found
                            mSwipeRefresh.setRefreshing(false);
                            Toast.makeText(context, "Não há posts...", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Response", response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
                mSwipeRefresh.setRefreshing(false);
                Toast.makeText(context, "Erro do servidor", Toast.LENGTH_SHORT).show();
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        return results;
    }*/
}
