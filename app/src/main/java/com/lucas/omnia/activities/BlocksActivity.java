package com.lucas.omnia.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lucas.omnia.R;
import com.lucas.omnia.adapters.BlockAdapter;
import com.lucas.omnia.models.Block;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlocksActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static BlocksActivity instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private final String URL = "https://dadosabertos.camara.leg.br/api/v2/blocos";

    public BlocksActivity() {}

    private BlocksActivity(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized BlocksActivity getInstance(Context context) {
        if (instance == null) {
            instance = new BlocksActivity(context);
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocks);

        setProgressBar(R.id.blocks_pb);
        showProgressBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.blocks_rv);
        recyclerView.setLayoutManager(layoutManager);

        showProgressBar();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, response -> {
                    List<Block> blockList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("dados");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Block block = new Block(jsonObject.getString("nome"));
                            blockList.add(block);
                        }

                        final BlockAdapter blockAdapter = new BlockAdapter(blockList);
                        recyclerView.setAdapter(blockAdapter);
                        hideProgressBar();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                        error -> {
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                });
        hideProgressBar();

        // Access the RequestQueue through your singleton class.
        BlocksActivity.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
