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
import com.lucas.omnia.adapters.DeputyAdapter
import com.lucas.omnia.models.Deputy
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DeputiesActivity : BaseActivity {
    private var requestQueue: RequestQueue? = null

    private constructor(context: Context) {
        ctx = context
        requestQueue = getRequestQueue()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deputies)
        setProgressBar(R.id.deputies_pb)
        showProgressBar()
        val layoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView? = findViewById(R.id.deputies_rv)
        recyclerView?.layoutManager = layoutManager
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, DEPUTIES_URL, null, {
            response:
                                                                                    JSONObject ->
            val deputyList: MutableList<Deputy> = ArrayList()
            try {
                val jsonArray = response.getJSONArray("dados")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val deputy = Deputy(jsonObject.getString("nome"),
                            jsonObject.getString("siglaPartido"), jsonObject.getString(
                            "siglaUf"),
                            jsonObject.getString("email"))
                    deputyList.add(deputy)
                }
                val deputyAdapter = DeputyAdapter(deputyList)
                recyclerView?.adapter = deputyAdapter
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

    private fun getRequestQueue(): RequestQueue? {
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
        const val DEPUTIES_URL = "https://dadosabertos.camara.leg.br/api/v2/deputados"

        private var instance: DeputiesActivity? = null
        private var ctx: Context? = null
        @Synchronized
        fun getInstance(context: Context): DeputiesActivity? {
            if (instance == null) {
                instance = DeputiesActivity(context)
            }
            return instance
        }
    }
}