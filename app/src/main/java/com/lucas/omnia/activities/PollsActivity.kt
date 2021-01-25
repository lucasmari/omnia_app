package com.lucas.omnia.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lucas.omnia.R
import com.lucas.omnia.adapters.PollAdapter
import com.lucas.omnia.models.Poll
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PollsActivity : BaseActivity {
    private var requestQueue: RequestQueue? = null

    constructor() {}
    private constructor(context: Context) {
        ctx = context
        requestQueue = getRequestQueue()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polls)
        setProgressBar(R.id.polls_pb)
        showProgressBar()
        val layoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView? = findViewById(R.id.polls_rv)
        recyclerView?.layoutManager = layoutManager
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, POLLS_URL, null, { response:
                                                                                    JSONObject ->
            val pollList: MutableList<Poll> = ArrayList()
            try {
                val jsonArray = response.getJSONArray("dados")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val poll = Poll(jsonObject.getString("data"),
                            jsonObject.getString("siglaOrgao"), jsonObject.getString(
                            "descricao"))
                    pollList.add(poll)
                }
                val pollAdapter = PollAdapter(pollList)
                recyclerView?.adapter = pollAdapter
                hideProgressBar()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        ) { error: VolleyError? ->
            Toast.makeText(this, "Server error: $error", Toast.LENGTH_SHORT).show()
            hideProgressBar()
        }

        // Access the RequestQueue through your singleton class.
        getInstance(this)!!.addToRequestQueue(jsonObjectRequest)
    }

    fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx!!.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        getRequestQueue()!!.add(req)
    }

    companion object {
        const val POLLS_URL = "https://dadosabertos.camara.leg.br/api/v2/votacoes"

        private var instance: PollsActivity? = null
        private var ctx: Context? = null
        @Synchronized
        fun getInstance(context: Context): PollsActivity? {
            if (instance == null) {
                instance = PollsActivity(context)
            }
            return instance
        }
    }
}