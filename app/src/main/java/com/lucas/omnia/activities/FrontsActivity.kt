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
import com.lucas.omnia.adapters.FrontAdapter
import com.lucas.omnia.models.Front
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FrontsActivity : BaseActivity {
    private var requestQueue: RequestQueue? = null

    private constructor(context: Context) {
        ctx = context
        requestQueue = getRequestQueue()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fronts)
        setProgressBar(R.id.fronts_pb)
        showProgressBar()
        val layoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView? = findViewById(R.id.fronts_rv)
        recyclerView?.layoutManager = layoutManager
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, FRONTS_URL, null, { response:
                                                                                    JSONObject ->
            val frontList: MutableList<Front> = ArrayList()
            try {
                val jsonArray = response.getJSONArray("dados")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val front = Front(jsonObject.getString("titulo"))
                    frontList.add(front)
                }
                val frontAdapter = FrontAdapter(frontList)
                recyclerView?.adapter = frontAdapter
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
        const val FRONTS_URL = "https://dadosabertos.camara.leg.br/api/v2/frentes"

        private var instance: FrontsActivity? = null
        private var ctx: Context? = null
        @Synchronized
        fun getInstance(context: Context): FrontsActivity? {
            if (instance == null) {
                instance = FrontsActivity(context)
            }
            return instance
        }
    }
}