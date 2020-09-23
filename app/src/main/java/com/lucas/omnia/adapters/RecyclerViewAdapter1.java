package com.lucas.omnia.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lucas.omnia.R;
import com.lucas.omnia.models.DataObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lucas.omnia.activities.MainActivity.userName;
//import static com.lucas.omnia.fragments.TabFragment1.getAllPosts;
import static com.lucas.omnia.utils.AppController.getInstance;

/**
 * Created by Lucas on 24/10/2017.
 */

public class RecyclerViewAdapter1 extends RecyclerView
        .Adapter<RecyclerViewAdapter1
        .DataObjectHolder> {

    public static ArrayList<DataObject> mDataSet;
    private static MyClickListener myClickListener;
    public static int pos = 0;
    private static ArrayList upvotesArray = new ArrayList();
    private static ArrayList downvotesArray = new ArrayList();
    private CharSequence options1[] = new CharSequence[]{"Editar", "Excluir"};
    private CharSequence options2[] = new CharSequence[]{"Reportar"};

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_UPVOTES = "upvotes";
    private static final String TAG_DOWNVOTES = "downvotes";

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView mUser;
        public static TextView mComment;
        TextView mInfo;
        ImageButton mUpVoteButton;
        ImageButton mDownVoteButton;
        ImageButton mCommentButton;
        ImageButton mShareButton;
        ImageButton mMoreButton;

        private DataObjectHolder(View itemView) {
            super(itemView);

            //mUser = itemView.findViewById(R.id.userTv);
            mComment = itemView.findViewById(R.id.bodyTv);
            mInfo = itemView.findViewById(R.id.upVotesTv);
            mUpVoteButton = itemView.findViewById(R.id.upVoteButton);
            mDownVoteButton = itemView.findViewById(R.id.downVoteButton);
            mCommentButton = itemView.findViewById(R.id.commentButton);
            mShareButton = itemView.findViewById(R.id.shareButton);
            mMoreButton = itemView.findViewById(R.id.moreButton);
        }

        @Override
        public void onClick(View v) { myClickListener.onItemClick(getOldPosition(), v); }
    }

    public RecyclerViewAdapter1(ArrayList<DataObject> myDataSet) { mDataSet = myDataSet; }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

    }

    public static void getUpvotedPosts(final Context context) {

        String url = "";
        upvotesArray.clear();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Checking for SUCCESS TAG
                            int success = response.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                // getting array of upvoted posts
                                JSONArray upvotes = response.getJSONArray(TAG_UPVOTES);

                                JSONObject c = upvotes.optJSONObject(0);

                                // Storing each json item in variable
                                String upvote = c.getString(TAG_UPVOTES);
                                String[] up = upvote.split(";");

                                // looping through all upvotes
                                for (int i = 0; i < up.length; i++) {
                                    upvotesArray.add(i, Integer.parseInt(up[i])-1);
                                }
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
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    public static void getDownvotedPosts(final Context context) {

        String url = "";
        downvotesArray.clear();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Checking for SUCCESS TAG
                            int success = response.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                // getting array of downvoted posts
                                JSONArray downvotes = response.getJSONArray(TAG_DOWNVOTES);

                                JSONObject c = downvotes.optJSONObject(0);

                                // Storing each json item in variable
                                String downvote = c.getString(TAG_DOWNVOTES);
                                String[] down = downvote.split(";");

                                // looping through all downvotes
                                for (int i = 0; i < down.length; i++) {
                                    downvotesArray.add(i, Integer.parseInt(down[i])-1);
                                }
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
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private void upvotePost(int position) {

        String url = "";

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", userName);
            obj.put("upvote", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Checking for SUCCESS TAG
                    int success = response.getInt(TAG_SUCCESS);

                    Log.d("Response", response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private void downvotePost(int position) {

        String url = "";

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", userName);
            obj.put("downvote", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Checking for SUCCESS TAG
                    int success = response.getInt(TAG_SUCCESS);

                    Log.d("Response", response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private void removeUpvotePost(int position) {

        String url = "";

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", userName);
            obj.put("upvote", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Checking for SUCCESS TAG
                            int success = response.getInt(TAG_SUCCESS);

                            Log.d("Response", response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private void removeDownvotePost(int position) {

        String url = "";

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", userName);
            obj.put("downvote", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Checking for SUCCESS TAG
                            int success = response.getInt(TAG_SUCCESS);

                            Log.d("Response", response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Error", response.toString());
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private void editPost(final Context context, int position) {

        String url = "";
        //String item_post = mEditText.getText().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", userName);
            //obj.put("item_post", item_post);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Atualizando item_post...");
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
                        //getAllPosts(context);
                        Toast.makeText(context, "Post atualizado!", Toast.LENGTH_SHORT).show();
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
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    private void deletePost(final Context context, int position) {

        String url = "";

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Apagando item_post...");
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
                        //getAllPosts(context);
                        Toast.makeText(context, "Post apagado!", Toast.LENGTH_SHORT).show();
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
            }
        });
        getInstance().addToRequestQueue(jsObjRequest);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public void addItem(DataObject dataObj, int index) {
        mDataSet.add(index, dataObj);
        notifyItemInserted(index);
        notifyItemRangeChanged(0, mDataSet.size());
    }

    private void editItem(DataObject dataObj, int index) {
        mDataSet.set(index, dataObj);
        notifyDataSetChanged();
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mDataSet.size());
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }
}
