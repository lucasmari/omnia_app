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
import com.lucas.omnia.adapters.PropositionAdapter;
import com.lucas.omnia.models.Proposition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PropositionsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static PropositionsActivity instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private final String URL = "https://dadosabertos.camara.leg.br/api/v2/proposicoes";

    public PropositionsActivity() {}

    private PropositionsActivity(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized PropositionsActivity getInstance(Context context) {
        if (instance == null) {
            instance = new PropositionsActivity(context);
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propositions);

        setProgressBar(R.id.propositions_pb);
        showProgressBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.propositions_rv);
        recyclerView.setLayoutManager(layoutManager);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, response -> {
                    List<Proposition> propositionList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("dados");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Proposition proposition = new Proposition(jsonObject.getString(
                                    "siglaTipo"),
                                    jsonObject.getString("ano"), jsonObject.getString(
                                    "ementa"));
                            propositionList.add(proposition);
                        }

                        final PropositionAdapter propositionAdapter =
                                new PropositionAdapter(propositionList);
                        recyclerView.setAdapter(propositionAdapter);
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
        PropositionsActivity.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
