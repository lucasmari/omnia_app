package com.lucas.omnia.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lucas.omnia.CommentViewHolder;
import com.lucas.omnia.models.DataObject2;
import com.lucas.omnia.models.DataObject3;
import com.lucas.omnia.R;
import com.lucas.omnia.ReplyViewHolder;
import com.lucas.omnia.ViewType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.lucas.omnia.utils.AppConfig.GET_DOWNVOTED_COMMENTS_URL;
import static com.lucas.omnia.utils.AppConfig.GET_UPVOTED_COMMENTS_URL;
import static com.lucas.omnia.utils.AppConfig.DELETE_COMMENT_URL;
import static com.lucas.omnia.utils.AppConfig.DOWNVOTE_COMMENT_URL;
import static com.lucas.omnia.utils.AppConfig.EDIT_COMMENT_URL;
import static com.lucas.omnia.utils.AppConfig.REMOVE_DOWNVOTE_COMMENT_URL;
import static com.lucas.omnia.utils.AppConfig.REMOVE_UPVOTE_COMMENT_URL;
import static com.lucas.omnia.utils.AppConfig.UPVOTE_COMMENT_URL;
import static com.lucas.omnia.utils.AppController.getInstance;
import static com.lucas.omnia.activities.CommentsActivity.getAllComments;
import static com.lucas.omnia.activities.CommentsActivity.mEditText2;
import static com.lucas.omnia.activities.CommentsActivity.reply;
import static com.lucas.omnia.activities.MainActivity.mUserName;
import static com.lucas.omnia.fragments.NavFragment1.mEditText;

/**
 * Created by Lucas on 21/11/2017.
 */

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        CommentViewHolder.HeaderViewHolderCallback {

    public static ArrayList<DataObject2> mDataSet2;
    static ArrayList<DataObject3> mDataSet3;
    private static ArrayList upvotesArray = new ArrayList();
    private static ArrayList downvotesArray = new ArrayList();
    private CharSequence options1[] = new CharSequence[]{"Compartilhar", "Editar", "Excluir"};
    private CharSequence options2[] = new CharSequence[]{"Compartilhar", "Reportar"};
    private static final int COMMENT_TYPE = 0;
    private static final int REPLY_TYPE = 1;
    static int commentPosition;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_UPVOTES = "upvotes";
    private static final String TAG_DOWNVOTES = "downvotes";


    private SparseArray<ViewType> viewTypes;
    private SparseIntArray headerExpandTracker;

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("onCreateViewHolder", String.valueOf(viewType));
        View view;
        switch (viewType) {
            case COMMENT_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_item_2, parent, false);
                return new CommentViewHolder(view, this);
            case REPLY_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_item_3, parent, false);
                return new ReplyViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_item_2, parent, false);
                return new CommentViewHolder(view, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("onBindViewHolder", String.valueOf(position));
        int itemViewType = getItemViewType(position);
        ViewType viewType = viewTypes.get(position);
        if (itemViewType == REPLY_TYPE) {
            bindReplyViewHolder(holder, position, viewType);
        } else {
            bindCommentViewHolder(holder, position, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("getItemViewType", String.valueOf(position));
        Log.d("getItemViewType2", String.valueOf(viewTypes.get(position).getType()));
        if (viewTypes.get(position).getType() == COMMENT_TYPE) {
            return COMMENT_TYPE;
        } else {
            return REPLY_TYPE;
        }
    }

    public void bindCommentViewHolder(RecyclerView.ViewHolder holder, int position, ViewType viewType) {
        Log.d("bindCommentViewHolder", String.valueOf(position));
        final CommentViewHolder headerViewHolder = (CommentViewHolder) holder;
        Log.d("bindCommentViewHolder2", String.valueOf(headerViewHolder.getAdapterPosition()));
        commentPosition = headerViewHolder.getAdapterPosition();
        headerViewHolder.mComment2.setText(mDataSet2.get(commentPosition).getText2());
        headerViewHolder.mReplyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply(headerViewHolder.getAdapterPosition());
            }
        });
        headerViewHolder.mUpVoteButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(headerViewHolder.mUpVoteButton2.isSelected()) {
                    headerViewHolder.mUpVoteButton2.setColorFilter(v.getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    mDataSet2.get(commentPosition).removeUpVote2();
                    headerViewHolder.mInfo2.setText(String.valueOf(mDataSet2.get(commentPosition)
                            .getmVotes2()));
                    headerViewHolder.mUpVoteButton2.setSelected(false);
                } else {
                    if(headerViewHolder.mDownVoteButton2.isSelected()) {
                        headerViewHolder.mDownVoteButton2.setColorFilter(v.getResources()
                                .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                        mDataSet2.get(commentPosition).removeDownVote2();
                        headerViewHolder.mDownVoteButton2.setSelected(false);
                    }
                    headerViewHolder.mUpVoteButton2.setColorFilter(v.getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    mDataSet2.get(commentPosition).upVote2();
                    headerViewHolder.mInfo2.setText(String.valueOf(mDataSet2.get(commentPosition)
                            .getmVotes2()));
                    headerViewHolder.mUpVoteButton2.setSelected(true);
                }
            }
        });
        headerViewHolder.mDownVoteButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(headerViewHolder.mDownVoteButton2.isSelected()) {
                    headerViewHolder.mDownVoteButton2.setColorFilter(v.getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    mDataSet2.get(headerViewHolder.getAdapterPosition()).removeDownVote2();
                    headerViewHolder.mInfo2.setText(String.valueOf(mDataSet2.get(headerViewHolder
                            .getAdapterPosition()).getmVotes2()));
                    headerViewHolder.mDownVoteButton2.setSelected(false);
                } else {
                    if(headerViewHolder.mUpVoteButton2.isSelected()) {
                        headerViewHolder.mUpVoteButton2.setColorFilter(v.getResources()
                                .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                        mDataSet2.get(headerViewHolder.getAdapterPosition()).removeUpVote2();
                        headerViewHolder.mUpVoteButton2.setSelected(false);
                    }
                    headerViewHolder.mDownVoteButton2.setColorFilter(v.getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    mDataSet2.get(headerViewHolder.getAdapterPosition()).downVote2();
                    headerViewHolder.mInfo2.setText(String.valueOf(mDataSet2.get(headerViewHolder
                            .getAdapterPosition()).getmVotes2()));
                    headerViewHolder.mDownVoteButton2.setSelected(true);
                }
            }
        });
        headerViewHolder.mMoreButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(options1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Context context = v.getContext();
                                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, mDataSet2.
                                        get(headerViewHolder.getAdapterPosition()).getText2());
                                context.startActivity(Intent.createChooser(shareIntent,
                                        context.getString(R.string.share_title)));
                                break;
                            case 1:
                                mEditText2.requestFocus();
                                DataObject2 obj = new DataObject2(headerViewHolder.getAdapterPosition(),
                                        mEditText2.getText().toString(),mDataSet2.get(headerViewHolder.getAdapterPosition()).getChild2());
                                editItem2(obj, headerViewHolder.getAdapterPosition());
                                break;
                            case 2:
                                new AlertDialog.Builder(v.getContext())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setMessage("Deseja realmente apagar esse comentário?")
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteItem2(headerViewHolder.getAdapterPosition());
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
        });
    }

    public void bindReplyViewHolder(RecyclerView.ViewHolder holder, int position, ViewType viewType) {
        Log.d("bindReplyViewHolder", String.valueOf(position));
        final ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
        Log.d("bindReplyViewHolder2", String.valueOf(replyViewHolder.getAdapterPosition()));
        replyViewHolder.mComment3.setText(mDataSet3.get(viewType.getDataIndex()).getText3());
        replyViewHolder.mReplyButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply(replyViewHolder.getAdapterPosition());
            }
        });
        replyViewHolder.mUpVoteButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replyViewHolder.mUpVoteButton3.isSelected()) {
                    replyViewHolder.mUpVoteButton3.setColorFilter(v.getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    mDataSet3.get(replyViewHolder.getAdapterPosition()).removeUpVote3();
                    replyViewHolder.mInfo3.setText(String.valueOf(mDataSet3.get(replyViewHolder
                            .getAdapterPosition()).getmVotes3()));
                    replyViewHolder.mUpVoteButton3.setSelected(false);
                } else {
                    if(replyViewHolder.mDownVoteButton3.isSelected()) {
                        replyViewHolder.mDownVoteButton3.setColorFilter(v.getResources()
                                .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                        mDataSet3.get(replyViewHolder.getAdapterPosition()).removeDownVote3();
                        replyViewHolder.mDownVoteButton3.setSelected(false);
                    }
                    replyViewHolder.mUpVoteButton3.setColorFilter(v.getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    mDataSet3.get(replyViewHolder.getAdapterPosition()).upVote3();
                    replyViewHolder.mInfo3.setText(String.valueOf(mDataSet3.get(replyViewHolder
                            .getAdapterPosition()).getmVotes3()));
                    replyViewHolder.mUpVoteButton3.setSelected(true);
                }
            }
        });
        replyViewHolder.mDownVoteButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replyViewHolder.mDownVoteButton3.isSelected()) {
                    replyViewHolder.mDownVoteButton3.setColorFilter(v.getResources()
                            .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    mDataSet3.get(replyViewHolder.getAdapterPosition()).removeDownVote3();
                    replyViewHolder.mInfo3.setText(String.valueOf(mDataSet3.get(replyViewHolder
                            .getAdapterPosition()).getmVotes3()));
                    replyViewHolder.mDownVoteButton3.setSelected(false);
                } else {
                    if(replyViewHolder.mUpVoteButton3.isSelected()) {
                        replyViewHolder.mUpVoteButton3.setColorFilter(v.getResources()
                                .getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                        mDataSet3.get(replyViewHolder.getAdapterPosition()).removeUpVote3();
                        replyViewHolder.mUpVoteButton3.setSelected(false);
                    }
                    replyViewHolder.mDownVoteButton3.setColorFilter(v.getResources()
                            .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    mDataSet3.get(replyViewHolder.getAdapterPosition()).downVote3();
                    replyViewHolder.mInfo3.setText(String.valueOf(mDataSet3.get(replyViewHolder
                            .getAdapterPosition()).getmVotes3()));
                    replyViewHolder.mDownVoteButton3.setSelected(true);
                }
            }
        });
        replyViewHolder.mMoreButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(options2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Context context = v.getContext();
                                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, mDataSet3
                                        .get(replyViewHolder.getAdapterPosition()).getText3());
                                context.startActivity(Intent.createChooser(shareIntent,
                                        context.getString(R.string.share_title)));
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public static void getUpvotedComments(final Context context) {

        String url = GET_UPVOTED_COMMENTS_URL;
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
                                // getting array of upvoted comments
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

    public static void getDownvotedComments(final Context context) {

        String url = GET_DOWNVOTED_COMMENTS_URL;
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
                                // getting array of downvoted comments
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

    public void upvoteComments(int position) {

        String url = UPVOTE_COMMENT_URL;

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

    public void downvoteComment(int position) {

        String url = DOWNVOTE_COMMENT_URL;

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

    public void removeUpvoteComment(int position) {

        String url = REMOVE_UPVOTE_COMMENT_URL;

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

    public void removeDownvoteComment(int position) {

        String url = REMOVE_DOWNVOTE_COMMENT_URL;

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

    public void editComment(final Context context, int position) {

        String url = EDIT_COMMENT_URL;
        String comment = mEditText.getText().toString();

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
            obj.put("user", mUserName);
            obj.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Atualizando comentário...");
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
                                Toast.makeText(context, "Comentário atualizado!", Toast.LENGTH_SHORT).show();
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

    public void deleteComment(final Context context, int position) {

        String url = DELETE_COMMENT_URL;

        JSONObject obj = new JSONObject();
        try {
            obj.put("pid", String.valueOf(position+1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Apagando comentário...");
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
                                Toast.makeText(context, "Comentário apagado!", Toast.LENGTH_SHORT).show();
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

    public RecyclerViewAdapter2(ArrayList<DataObject2> myDataSet2, ArrayList<DataObject3> myDataSet3) {
        mDataSet2 = myDataSet2;
        mDataSet3 = myDataSet3;
        viewTypes = new SparseArray<>(mDataSet3.size() + mDataSet2.size());
        headerExpandTracker = new SparseIntArray(mDataSet2.size());
        notifyDataSetChanged();
    }

    public void setData2(DataObject2 myDataSet2, int index) {
        mDataSet2.add(myDataSet2);
        //notifyDataSetChanged();
        notifyItemInserted(index);
        notifyItemRangeChanged(index, mDataSet2.size());
    }

    public void setData3(DataObject3 myDataSet3, int index) {
        mDataSet3.add(myDataSet3);
        //notifyDataSetChanged();
        notifyItemInserted(index);
        notifyItemRangeChanged(index, mDataSet3.size());
    }

    public final void addItem2(DataObject2 dataObj, int index) {
        mDataSet2.add(dataObj);
        notifyItemInserted(index);
        notifyItemRangeChanged(index, mDataSet2.size());
    }

    public final void addItem3(DataObject3 dataObj, int index) {
        mDataSet3.add(dataObj);
        notifyItemInserted(index);
        notifyItemRangeChanged(index, mDataSet3.size());
    }

    public final void editItem2(DataObject2 dataObj, int index) {
        mDataSet2.set(index, dataObj);
        notifyDataSetChanged();
    }

    public final void editItem3(DataObject3 dataObj, int index) {
        mDataSet3.set(index, dataObj);
        notifyDataSetChanged();
    }

    public void deleteItem2(int index) {
        mDataSet2.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mDataSet2.size());
    }

    public void deleteItem3(int index) {
        mDataSet3.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mDataSet3.size());
    }

    @Override
    public int getItemCount() {
        int count = 0;
        viewTypes.clear();
        int collapsedCount = 0;
        for (int i = 0; i < mDataSet2.size(); i++) {
            viewTypes.put(count, new ViewType(i, COMMENT_TYPE));
            count++;
            int childCount = getChildCount(i);
            if (mDataSet3.size() != 0) {
                if (headerExpandTracker.get(i) != 0) {
                    // Expanded State
                    for (int j = 0; j < childCount; j++) {
                        viewTypes.put(count, new ViewType(count - (i + 1) + collapsedCount, REPLY_TYPE));
                        count++;
                    }
                } else {
                    // Collapsed
                    collapsedCount += childCount;
                }
            }
        }
        return count;
    }

    public int getChildCount(int position) {
        Log.d("getChildCount", String.valueOf(mDataSet2.get(position).getChild2()));
        return mDataSet2.get(position).getChild2();
    }

    public void onHeaderClick(int position) {
        ViewType viewType = viewTypes.get(position);
        int dataIndex = viewType.getDataIndex();
        int childCount = getChildCount(position);
        if (headerExpandTracker.get(dataIndex) == 0) {
            // Collapsed. Now expand it
            headerExpandTracker.put(dataIndex, 1);
            notifyItemRangeInserted(position + 1, childCount);
        } else {
            // Expanded. Now collapse it
            headerExpandTracker.put(dataIndex, 0);
            notifyItemRangeRemoved(position + 1, childCount);
        }
    }

    public boolean isExpanded(int position) {
        int dataIndex = viewTypes.get(position).getDataIndex();
        return headerExpandTracker.get(dataIndex) == 1;
    }
}