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
import com.lucas.omnia.adapters.PollAdapter;
import com.lucas.omnia.models.Poll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PollsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static PollsActivity instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private final String URL = "https://dadosabertos.camara.leg.br/api/v2/votacoes";

    public PollsActivity() {}

    private PollsActivity(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized PollsActivity getInstance(Context context) {
        if (instance == null) {
            instance = new PollsActivity(context);
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        setProgressBar(R.id.polls_pb);
        showProgressBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.polls_rv);
        recyclerView.setLayoutManager(layoutManager);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, response -> {
                    List<Poll> pollList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("dados");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Poll poll = new Poll(jsonObject.getString("data"),
                                    jsonObject.getString("siglaOrgao"), jsonObject.getString(
                                    "descricao"));
                            pollList.add(poll);
                        }

                        final PollAdapter pollAdapter = new PollAdapter(pollList);
                        recyclerView.setAdapter(pollAdapter);
                        hideProgressBar();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                        error -> {
                            Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        });

        // Access the RequestQueue through your singleton class.
        PollsActivity.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
