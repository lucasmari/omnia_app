package com.lucas.omnia.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lucas.omnia.models.DataObject1;
import com.lucas.omnia.R;
import com.lucas.omnia.activities.CommentsActivity;
import com.lucas.omnia.activities.UserPageActivity2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lucas.omnia.utils.AppConfig.GET_DOWNVOTED_POSTS_URL;
import static com.lucas.omnia.utils.AppConfig.GET_UPVOTED_POSTS_URL;
import static com.lucas.omnia.utils.AppConfig.DELETE_POST_URL;
import static com.lucas.omnia.utils.AppConfig.DOWNVOTE_POST_URL;
import static com.lucas.omnia.utils.AppConfig.EDIT_POST_URL;
import static com.lucas.omnia.utils.AppConfig.REMOVE_DOWNVOTE_POST_URL;
import static com.lucas.omnia.utils.AppConfig.REMOVE_UPVOTE_POST_URL;
import static com.lucas.omnia.utils.AppConfig.UPVOTE_POST_URL;
import static com.lucas.omnia.utils.AppController.getInstance;
import static com.lucas.omnia.activities.MainActivity.hideSoftKeyboard;
import static com.lucas.omnia.activities.MainActivity.mUserName;
import static com.lucas.omnia.activities.MainActivity.showSoftKeyboard;
import static com.lucas.omnia.fragments.NavFragment1.mAddFab;
import static com.lucas.omnia.fragments.NavFragment1.mAttachFab;
import static com.lucas.omnia.fragments.NavFragment1.mCameraFab;
import static com.lucas.omnia.fragments.NavFragment1.mEditText;
import static com.lucas.omnia.fragments.NavFragment1.mRelativeLayout;
import static com.lucas.omnia.fragments.NavFragment1.mSendButton;
import static com.lucas.omnia.fragments.TabFragment1.getAllPosts;

/**
 * Created by Lucas on 24/10/2017.
 */

public class RecyclerViewAdapter1 extends RecyclerView
        .Adapter<RecyclerViewAdapter1
        .DataObjectHolder> {

    public static ArrayList<DataObject1> mDataSet;
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

            mUser = itemView.findViewById(R.id.userTv);
            mComment = itemView.findViewById(R.id.commentTv);
            mInfo = itemView.findViewById(R.id.infoTv);
            mUpVoteButton = itemView.findViewById(R.id.upVoteButton);
            mDownVoteButton = itemView.findViewById(R.id.downVoteButton);
            mCommentButton = itemView.findViewById(R.id.commentButton);
            mShareButton = itemView.findViewById(R.id.shareButton);
            mMoreButton = itemView.findViewById(R.id.moreButton);
        }

        @Override
        public void onClick(View v) { myClickListener.onItemClick(getOldPosition(), v); }
    }

    public RecyclerViewAdapter1(ArrayList<DataObject1> myDataSet) { mDataSet = myDataSet; }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_1, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        holder.mUser.setText(mDataSet.get(position).getUser());
        holder.mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = holder.getAdapterPosition();
                v.getContext().startActivity(new Intent(v.getContext(), UserPageActivity2.class));
            }
        });
        holder.mComment.setText(mDataSet.get(position).getComment());
        holder.mInfo.setText(String.valueOf(mDataSet.get(holder.getAdapterPosition()).getmVotes()));
        holder.mUpVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mUpVoteButton.isSelected()) {
                    removeUpvotePost(holder.getAdapterPosition());
                    holder.mUpVoteButton.setColorFilter(v.getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    mDataSet.get(holder.getAdapterPosition()).removeUpVote();
                    holder.mInfo.setText(String.valueOf(mDataSet.get(holder.getAdapterPosition()).getmVotes()));
                    holder.mUpVoteButton.setSelected(false);
                }
                else {
                    if(holder.mDownVoteButton.isSelected()) {
                        holder.mDownVoteButton.setColorFilter(v.getResources()
                                .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                        mDataSet.get(holder.getAdapterPosition()).removeDownVote();
                        holder.mDownVoteButton.setSelected(false);
                    }
                    upvotePost(holder.getAdapterPosition());
                    holder.mUpVoteButton.setColorFilter(v.getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    mDataSet.get(holder.getAdapterPosition()).upVote();
                    holder.mInfo.setText(String.valueOf(mDataSet.get(holder.getAdapterPosition()).getmVotes()));
                    holder.mUpVoteButton.setSelected(true);
                }
            }
        });
        if(upvotesArray.contains(holder.getAdapterPosition())) {
            holder.mUpVoteButton.setColorFilter(getInstance().getResources()
                    .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.mUpVoteButton.setSelected(true);
        }
        holder.mDownVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mDownVoteButton.isSelected()) {
                    removeDownvotePost(holder.getAdapterPosition());
                    holder.mDownVoteButton.setColorFilter(v.getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    mDataSet.get(holder.getAdapterPosition()).removeDownVote();
                    holder.mInfo.setText(String.valueOf(mDataSet.get(holder.getAdapterPosition()).getmVotes()));
                    holder.mDownVoteButton.setSelected(false);
                }
                else {
                    if(holder.mUpVoteButton.isSelected()) {
                        holder.mUpVoteButton.setColorFilter(v.getResources()
                                .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                        mDataSet.get(holder.getAdapterPosition()).removeUpVote();
                        holder.mUpVoteButton.setSelected(false);
                    }
                    downvotePost(holder.getAdapterPosition());
                    holder.mDownVoteButton.setColorFilter(v.getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    mDataSet.get(holder.getAdapterPosition()).downVote();
                    holder.mInfo.setText(String.valueOf(mDataSet.get(holder.getAdapterPosition()).getmVotes()));
                    holder.mDownVoteButton.setSelected(true);
                }
            }
        });
        if(downvotesArray.contains(holder.getAdapterPosition())) {
            holder.mDownVoteButton.setColorFilter(getInstance().getResources()
                    .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            holder.mDownVoteButton.setSelected(true);
        }
        holder.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), CommentsActivity.class));
            }
        });
        holder.mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mDataSet.get(holder.getAdapterPosition()).getComment());
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_title)));
            }
        });
        holder.mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Animation mRotateForward = AnimationUtils.loadAnimation(v.getContext(), R.anim.rotate_forward);
                final Animation mRotateBackward = AnimationUtils.loadAnimation(v.getContext(), R.anim.rotate_backward);
                final Animation mFabShow1 = AnimationUtils.loadAnimation(v.getContext(), R.anim.fab_show_1);
                final Animation mFabShow2 = AnimationUtils.loadAnimation(v.getContext(), R.anim.fab_show_2);
                final Animation mFabHide1 = AnimationUtils.loadAnimation(v.getContext(), R.anim.fab_hide_1);
                final Animation mSlideIn = AnimationUtils.loadAnimation(v.getContext(), R.anim.rl_slide_in);
                final Animation mHide = AnimationUtils.loadAnimation(v.getContext(), R.anim.rl_hide);

                if(mUserName.equals(mDataSet.get(holder.getAdapterPosition()).getUser())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setItems(options1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    mAddFab.startAnimation(mRotateForward);
                                    mAttachFab.startAnimation(mFabShow1);
                                    mCameraFab.startAnimation(mFabShow2);
                                    mAttachFab.setVisibility(View.VISIBLE);
                                    mCameraFab.setVisibility(View.VISIBLE);
                                    mRelativeLayout.startAnimation(mSlideIn);
                                    mRelativeLayout.setVisibility(View.VISIBLE);
                                    showSoftKeyboard(v.getContext(), mEditText);
                                    mEditText.setText(mDataSet.get(holder.getAdapterPosition()).getComment());
                                    mSendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            editPost(v.getContext(), holder.getAdapterPosition());

                                            mAddFab.startAnimation(mRotateBackward);
                                            mAttachFab.startAnimation(mFabHide1);
                                            mCameraFab.startAnimation(mFabHide1);
                                            mRelativeLayout.startAnimation(mHide);
                                            mAttachFab.setVisibility(View.GONE);
                                            mCameraFab.setVisibility(View.GONE);
                                            mEditText.getText().clear();
                                            mRelativeLayout.setVisibility(View.GONE);
                                            hideSoftKeyboard(v.getContext(), mEditText);
                                        }
                                    });
                                    break;
                                case 1:
                                    new AlertDialog.Builder(v.getContext())
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setMessage("Deseja realmente apagar esse post?")
                                            .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deletePost(v.getContext(), holder.getAdapterPosition());
                                                }
                                            })
                                            .setNegativeButton("Não", null)
                                            .show();
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setItems(options2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    public static void getUpvotedPosts(final Context context) {

        String url = GET_UPVOTED_POSTS_URL;
        upvotesArray.clear();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", mUserName);
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

        String url = GET_DOWNVOTED_POSTS_URL;
        downvotesArray.clear();

        JSONObject obj = new JSONObject();
        try {
            obj.put("user", mUserName);
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

        String url = UPVOTE_POST_URL;

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", mUserName);
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

        String url = DOWNVOTE_POST_URL;

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", mUserName);
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

        String url = REMOVE_UPVOTE_POST_URL;

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", mUserName);
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

        String url = REMOVE_DOWNVOTE_POST_URL;

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", mUserName);
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

        String url = EDIT_POST_URL;
        String post = mEditText.getText().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", mUserName);
            obj.put("post", post);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Atualizando post...");
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

        String url = DELETE_POST_URL;

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Apagando post...");
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

    public void addItem(DataObject1 dataObj, int index) {
        mDataSet.add(index, dataObj);
        notifyItemInserted(index);
        notifyItemRangeChanged(0, mDataSet.size());
    }

    private void editItem(DataObject1 dataObj, int index) {
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
