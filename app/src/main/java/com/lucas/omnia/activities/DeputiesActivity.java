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
import com.lucas.omnia.adapters.DeputyAdapter;
import com.lucas.omnia.models.Deputy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeputiesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static DeputiesActivity instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private final String URL = "https://dadosabertos.camara.leg.br/api/v2/deputados";

    public DeputiesActivity() {}

    private DeputiesActivity(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized DeputiesActivity getInstance(Context context) {
        if (instance == null) {
            instance = new DeputiesActivity(context);
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deputies);

        setProgressBar(R.id.deputies_pb);
        showProgressBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.deputies_rv);
        recyclerView.setLayoutManager(layoutManager);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, response -> {
                    List<Deputy> deputyList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("dados");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Deputy deputy = new Deputy(jsonObject.getString("nome"),
                                    jsonObject.getString("siglaPartido"), jsonObject.getString(
                                            "siglaUf"),
                                    jsonObject.getString("email"));
                            deputyList.add(deputy);
                        }

                        final DeputyAdapter deputyAdapter = new DeputyAdapter(deputyList);
                        recyclerView.setAdapter(deputyAdapter);
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
        DeputiesActivity.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
