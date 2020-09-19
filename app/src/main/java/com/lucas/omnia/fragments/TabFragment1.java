package com.lucas.omnia.fragments;

import android.app.ProgressDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lucas.omnia.CustomRequest;
import com.lucas.omnia.models.DataObject1;
import com.lucas.omnia.R;
import com.lucas.omnia.VerticalSpaceItemDecoration;
import com.lucas.omnia.adapters.RecyclerViewAdapter1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.Request.Method;
import static com.lucas.omnia.activities.MainActivity.userName;
import static com.lucas.omnia.fragments.NavFragment1.mAddFab;
import static com.lucas.omnia.fragments.NavFragment1.mAttachFab;
import static com.lucas.omnia.fragments.NavFragment1.mCameraFab;
import static com.lucas.omnia.fragments.NavFragment1.mEditText;
import static com.lucas.omnia.fragments.NavFragment1.mRelativeLayout;
import static com.lucas.omnia.fragments.NavFragment1.mSendButton;
import static com.lucas.omnia.utils.AppConfig.CREATE_POST_URL;
import static com.lucas.omnia.utils.AppConfig.GET_ALL_POSTS_URL;
import static com.lucas.omnia.utils.AppController.getInstance;
import static com.lucas.omnia.activities.MainActivity.hideSoftKeyboard;
import static com.lucas.omnia.activities.MainActivity.showSoftKeyboard;
import static com.lucas.omnia.adapters.RecyclerViewAdapter1.getDownvotedPosts;
import static com.lucas.omnia.adapters.RecyclerViewAdapter1.getUpvotedPosts;

/**
 * Created by Lucas on 10/10/2017.
 */

public class TabFragment1 extends Fragment {

    public static Adapter mAdapter;
    private static final int VERTICAL_ITEM_SPACE = 48;
    static SwipeRefreshLayout mSwipeRefresh;
    private TextView textView;
    private Animation mRotateForward;
    private Animation mRotateBackward;
    private Animation mFabShow1;
    private Animation mFabShow2;
    private Animation mFabHide1;
    private Animation mSlideIn;
    private Animation mHide;
    public static boolean visible;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_PID = "pid";
    private static final String TAG_USER = "user";
    private static final String TAG_POST = "post";
    private static final String TAG_VOTES = "votes";

    static ArrayList idList = new ArrayList(999999);
    public static ArrayList usersList = new ArrayList(999999);
    static ArrayList postsList = new ArrayList(999999);
    static ArrayList votesList = new ArrayList(999999);

    // posts JSONArray
    static JSONArray posts = null;

    public TabFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
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
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
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

        mRotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);
        mFabShow1 = AnimationUtils.loadAnimation(getContext(), R.anim.fab_show_1);
        mFabShow2 = AnimationUtils.loadAnimation(getContext(), R.anim.fab_show_2);
        mFabHide1 = AnimationUtils.loadAnimation(getContext(), R.anim.fab_hide_1);
        mSlideIn = AnimationUtils.loadAnimation(getContext(), R.anim.rl_slide_in);
        mHide = AnimationUtils.loadAnimation(getContext(), R.anim.rl_hide);

        mAddFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                visible = !visible;
                if(visible) {
                    mAddFab.startAnimation(mRotateForward);
                    mAttachFab.startAnimation(mFabShow1);
                    mCameraFab.startAnimation(mFabShow2);
                    mAttachFab.setVisibility(View.VISIBLE);
                    mCameraFab.setVisibility(View.VISIBLE);
                    mRelativeLayout.startAnimation(mSlideIn);
                    mRelativeLayout.setVisibility(View.VISIBLE);
                    showSoftKeyboard(getContext(),mEditText);
                }
                else {
                    hideSoftKeyboard(getContext(),mEditText);
                    mAddFab.startAnimation(mRotateBackward);
                    mAttachFab.startAnimation(mFabHide1);
                    mCameraFab.startAnimation(mFabHide1);
                    mRelativeLayout.startAnimation(mHide);
                    mAttachFab.setVisibility(View.GONE);
                    mCameraFab.setVisibility(View.GONE);
                    mEditText.getText().clear();
                    mRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Escreva algo!", Toast.LENGTH_SHORT).show();
                }
                else {
                    createNewPost(v.getContext());

                    hideSoftKeyboard(getContext(), mEditText);
                    mAddFab.startAnimation(mRotateBackward);
                    mAttachFab.startAnimation(mFabHide1);
                    mCameraFab.startAnimation(mFabHide1);
                    mRelativeLayout.startAnimation(mHide);
                    mAttachFab.setVisibility(View.GONE);
                    mCameraFab.setVisibility(View.GONE);
                    mEditText.getText().clear();
                    mRelativeLayout.setVisibility(View.GONE);
                    visible = !visible;
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mAddFab.getVisibility() == View.VISIBLE) {
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
                }
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

        String url = GET_ALL_POSTS_URL;

        mSwipeRefresh.setRefreshing(true);

        CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
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
                            String post = c.getString(TAG_POST);
                            String votes = c.getString(TAG_VOTES);

                            idList.add(i, id-1);
                            usersList.add(i, user);
                            postsList.add(i, post);
                            votesList.add(i, votes);

                            DataObject1 obj = new DataObject1((int) idList.get(i),
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

    public void createNewPost(final Context context) {

        String url = CREATE_POST_URL;
        String post = mEditText.getText().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", userName);
            obj.put("post", post);
            obj.put("votes", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Criando post...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Checking for SUCCESS TAG
                    int success = response.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        getAllPosts(context);
                        Toast.makeText(context, "Post criado!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Erro...", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Response", response.toString());
                    pDialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
                pDialog.hide();
                Toast.makeText(context, "Erro do servidor", Toast.LENGTH_SHORT).show();
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private ArrayList<DataObject1> getDataSet() {
        ArrayList results = new ArrayList<DataObject1>();
        return results;
    }
}
