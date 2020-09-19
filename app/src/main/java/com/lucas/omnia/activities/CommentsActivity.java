package com.lucas.omnia.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lucas.omnia.CustomRequest;
import com.lucas.omnia.models.DataObject2;
import com.lucas.omnia.models.DataObject3;
import com.lucas.omnia.R;
import com.lucas.omnia.adapters.RecyclerViewAdapter2;
import com.lucas.omnia.VerticalSpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lucas.omnia.activities.MainActivity.userName;
import static com.lucas.omnia.utils.AppConfig.GET_ALL_COMMENTS_URL;
import static com.lucas.omnia.utils.AppConfig.CREATE_COMMENT_URL;
import static com.lucas.omnia.utils.AppController.getInstance;
import static com.lucas.omnia.activities.MainActivity.hideSoftKeyboard;
import static com.lucas.omnia.activities.MainActivity.showSoftKeyboard;
import static com.lucas.omnia.adapters.RecyclerViewAdapter2.getDownvotedComments;
import static com.lucas.omnia.adapters.RecyclerViewAdapter2.getUpvotedComments;
import static com.lucas.omnia.adapters.RecyclerViewAdapter2.mDataSet2;

public class CommentsActivity extends AppCompatActivity {

    static RecyclerView mRecyclerView;
    static RecyclerViewAdapter2 mAdapter;
    private TextView textView;
    private LinearLayoutManager mLayoutManager;
    private static final int VERTICAL_ITEM_SPACE = 48;
    static SwipeRefreshLayout mSwipeRefresh;
    public static EditText mEditText2;
    static ImageButton mSendButton2;
    private Animation mShrink;
    private Animation mExpand;
    private Animation mShow;
    private Animation mHide;
    private boolean visible;
    static int reply = 0;
    private int count2 = 0;
    static int position;
    ArrayList<DataObject2> comments2 = new ArrayList<>();
    ArrayList<DataObject3> comments3 = new ArrayList<>();
    static DataObject2 obj2;
    DataObject3 obj3;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COMMENTS = "activity_comments";
    private static final String TAG_PID = "pid";
    private static final String TAG_USER = "user";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_VOTES = "votes";

    static ArrayList idList = new ArrayList(999999);
    static ArrayList usersList = new ArrayList(999999);
    static ArrayList commentsList = new ArrayList(999999);
    static ArrayList votesList = new ArrayList(999999);

    // posts JSONArray
    static JSONArray comments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mRecyclerView = findViewById(R.id.rv_comments);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getInstance());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        mAdapter = new RecyclerViewAdapter2(getDataSet2(),getDataSet3());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefresh = findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        int i = mAdapter.getItemCount();
                        while(i != 0) {
                            ((RecyclerViewAdapter2) mAdapter).deleteItem2(i-1);
                            i--;
                        }
                        if(isOnline()) {
                            textView = findViewById(R.id.noConnection2);
                            textView.setVisibility(View.GONE);
                            getAllComments(getInstance());
                            getUpvotedComments(getInstance());
                            getDownvotedComments(getInstance());
                        }
                        else {
                            mSwipeRefresh.setRefreshing(false);
                            textView = findViewById(R.id.noConnection2);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        if(isOnline()) {
            textView = findViewById(R.id.noConnection2);
            textView.setVisibility(View.GONE);
            getAllComments(getInstance());
            //getUpvotedComments(getInstance());
            //getDownvotedComments(getInstance());
        }
        else {
            mSwipeRefresh.setRefreshing(false);
            textView = findViewById(R.id.noConnection2);
            textView.setVisibility(View.VISIBLE);
        }

        mEditText2 = findViewById(R.id.editText2);
        mSendButton2 = findViewById(R.id.sendButton2);

        mEditText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                visible = !visible;
                if(visible) {
                    mEditText2.setHint("");
                    mShrink = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.shrink_et_1);
                    mShow = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.show_1);
                    mEditText2.startAnimation(mShrink);
                    mSendButton2.startAnimation(mShow);
                    mSendButton2.setVisibility(View.VISIBLE);
                    showSoftKeyboard(v.getContext(),mEditText2);
                }
                else {
                    mEditText2.setHint(getResources().getString(R.string.comment_hint));
                    mExpand = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.expand_et_1);
                    mHide = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.hide_1);
                    mEditText2.startAnimation(mExpand);
                    mSendButton2.startAnimation(mHide);
                    mSendButton2.setVisibility(View.GONE);
                    hideSoftKeyboard(v.getContext(),mEditText2);
                }
            }
        });

        mSendButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText2.getText().toString().isEmpty()) {
                    Toast.makeText(v.getContext(), "Escreva algo!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (reply == 0) {
                        createNewComment(v.getContext());
                        //obj2 = new DataObject2(count2, mEditText2.getText().toString(),0);
                        //setDataSet2(obj2);
                        //mAdapter.setData2(obj2, count2);
                        //mAdapter.addItem2(obj2, count2);
                        count2++;
                        if (mEditText2.length() > 0) {
                            mEditText2.getText().clear();
                        }
                        mEditText2.clearFocus();
                    } else {
                        obj3 = new DataObject3(mDataSet2.get(position).getChild2(), mEditText2.getText().toString());
                        setDataSet3(obj3);
                        mDataSet2.get(position).setChild2();
                        //mAdapter.setData3(obj3, count3);
                        //mAdapter.addItem3(obj3, count3);
                        reply = 0;
                        if (mEditText2.length() > 0) {
                            mEditText2.getText().clear();
                        }
                        mEditText2.clearFocus();
                    }
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void getAllComments(final Context context) {

        String url = GET_ALL_COMMENTS_URL;

        mSwipeRefresh.setRefreshing(true);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Checking for SUCCESS TAG
                            int success = response.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                int j = mAdapter.getItemCount();
                                while(j != 0) {
                                    ((RecyclerViewAdapter2) mAdapter).deleteItem2(j-1);
                                    j--;
                                }
                                // activity_comments found
                                mSwipeRefresh.setRefreshing(false);
                                // getting array of posts
                                comments = response.getJSONArray(TAG_COMMENTS);

                                // looping through all activity_comments
                                for (int i = 0; i < comments.length(); i++) {
                                    JSONObject c = comments.optJSONObject(i);

                                    // Storing each json item in variable
                                    int id = c.getInt(TAG_PID);
                                    String user = c.getString(TAG_USER);
                                    String post = c.getString(TAG_COMMENT);
                                    String votes = c.getString(TAG_VOTES);

                                    idList.add(i, id-1);
                                    usersList.add(i, user);
                                    commentsList.add(i, post);
                                    votesList.add(i, votes);

                                    obj2 = new DataObject2((int) idList.get(i),
                                            commentsList.get(i).toString(),0);
                                    (mAdapter).addItem2(obj2,(int) idList.get(i));
                                }
                            } else {
                                // no activity_comments found
                                mSwipeRefresh.setRefreshing(false);
                                Toast.makeText(context, "Não há comentários...", Toast.LENGTH_SHORT).show();
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

    public void createNewComment(final Context context) {

        String url = CREATE_COMMENT_URL;
        String comment = mEditText2.getText().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", userName);
            obj.put("comment", comment);
            obj.put("votes", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Criando comentário...");
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
                                getAllComments(context);
                                Toast.makeText(context, "Comentário criado!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if(mEditText2.isFocused()) {
            mExpand = AnimationUtils.loadAnimation(this, R.anim.expand_et_1);
            mHide = AnimationUtils.loadAnimation(this, R.anim.hide_1);
            if (mEditText2.length() > 0) {
                mEditText2.getText().clear();
            }
            mEditText2.clearFocus();
            mEditText2.startAnimation(mExpand);
            mSendButton2.startAnimation(mHide);
            mSendButton2.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public static void reply(int p) {
        position = p;
        reply = 1;
        mEditText2.requestFocus();
    }

    private void setDataSet2(DataObject2 object2) {
        comments2.add(object2);
    }

    private void setDataSet3(DataObject3 object3) {
        comments3.add(object3);
    }


    private ArrayList<DataObject2> getDataSet2() {
        return comments2;
    }

    private ArrayList<DataObject3> getDataSet3() {
        return comments3;
    }
}
