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
import com.lucas.omnia.adapters.PartyAdapter;
import com.lucas.omnia.models.Party;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PartiesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private static PartiesActivity instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private final String URL = "https://dadosabertos.camara.leg.br/api/v2/partidos";

    public PartiesActivity() {}

    private PartiesActivity(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized PartiesActivity getInstance(Context context) {
        if (instance == null) {
            instance = new PartiesActivity(context);
        }
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parties);

        setProgressBar(R.id.parties_pb);
        showProgressBar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.parties_rv);
        recyclerView.setLayoutManager(layoutManager);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, response -> {
                    List<Party> partyList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("dados");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Party party = new Party(jsonObject.getString("sigla"),
                                    jsonObject.getString("nome"));
                            partyList.add(party);
                        }

                        final PartyAdapter partyAdapter = new PartyAdapter(partyList);
                        recyclerView.setAdapter(partyAdapter);
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
        PartiesActivity.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
