package com.lucas.omnia.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucas.omnia.R
import com.lucas.omnia.adapters.SearchLawAdapter
import com.lucas.omnia.models.Law
import com.lucas.omnia.utils.LawsXmlParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections.addAll

class SearchLawActivity : BaseActivity() {
    private var noneTv: TextView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_law)
        setProgressBar(R.id.search_law_pb)
        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.search_law_rv)
        recyclerView?.layoutManager = layoutManager
        val searchEt = findViewById<EditText>(R.id.search_law_et)
        val searchBt = findViewById<ImageButton>(R.id.search_law_ib_search)
        searchBt.setOnClickListener {
            showProgressBar()
            val query = searchEt.text.toString()
            val url = composeQuery(query)
            CoroutineScope(Dispatchers.Main).launch {
                makeNetworkCall(url)
            }
        }
        noneTv = findViewById(R.id.search_law_tv_none)
    }

    fun composeQuery(query: String): String {
        val composedQuery = query.replace(' ', '+')
        return SEARCH_URL + composedQuery + "\""
    }

    private suspend fun makeNetworkCall(url: String){
        // the getResults() function should be a suspend function too, more on that later
        val result = loadXmlFromNetwork(url)
        if (result.itemCount == 0) {
            noneTv!!.visibility = View.VISIBLE
            recyclerView!!.adapter = null
        } else {
            noneTv!!.visibility = View.GONE
            recyclerView!!.adapter = result
        }
        hideProgressBar()
    }

    // Uploads XML, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private suspend fun loadXmlFromNetwork(urlString: String): RecyclerView.Adapter<*> {
        var stream: InputStream? = null
        // Instantiate the parser
        val lawsXmlParser = LawsXmlParser()
        var laws: MutableList<Law>? = null
        withContext(Dispatchers.IO) {
            try {
                try {
                    stream = downloadUrl(urlString)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                withContext(Dispatchers.Default) {
                    try {
                        laws = lawsXmlParser.parse(stream!!)
                    } catch (e: XmlPullParserException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            } finally {
                if (stream != null) {
                    try {
                        stream!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        addAll(laws)

        return SearchLawAdapter(this, laws)
    }

    // Given a string representation of a SEARCH_URL, sets up a connection and gets
    // an input stream.
    @Throws(IOException::class)
    fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.readTimeout = 10000
        conn.connectTimeout = 15000
        conn.requestMethod = "GET"
        conn.doInput = true
        // Starts the query
        conn.connect()
        return conn.inputStream
    }

    companion object {
        const val SEARCH_URL = "https://www.lexml.gov.br/busca/SRU?query=urn=lei&query=description=\""
    }
}