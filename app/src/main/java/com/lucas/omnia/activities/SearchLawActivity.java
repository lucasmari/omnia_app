package com.lucas.omnia.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.omnia.R;
import com.lucas.omnia.adapters.SearchLawAdapter;
import com.lucas.omnia.models.Law;
import com.lucas.omnia.utils.LawsXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SearchLawActivity extends BaseActivity {

    private TextView noneTv;
    private RecyclerView recyclerView;
    private final String SEARCH_URL = "https://www.lexml.gov.br/busca/SRU?query=urn=lei&query=description=\"";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_law);

        setProgressBar(R.id.search_law_pb);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.search_law_rv);
        recyclerView.setLayoutManager(layoutManager);

        EditText searchEt = findViewById(R.id.search_law_et);
        ImageButton searchBt = findViewById(R.id.search_law_ib_search);
        searchBt.setOnClickListener(v -> {
            showProgressBar();
            String query = searchEt.getText().toString();
            String url = composeQuery(query);
            new DownloadXmlTask().execute(url);
        });
        noneTv = findViewById(R.id.search_law_tv_none);
    }

    public String composeQuery(String query) {
        query = query.replace(' ', '+');

        return SEARCH_URL + query + "\"";
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, RecyclerView.Adapter> {
        @Override
        protected RecyclerView.Adapter doInBackground(String... urls) {
            return loadXmlFromNetwork(urls[0]);
        }

        @Override
        protected void onPostExecute(RecyclerView.Adapter result) {
            if (result.getItemCount() == 0) {
                noneTv.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(null);
            }
            else {
                noneTv.setVisibility(View.GONE);
                recyclerView.setAdapter(result);
            }
            hideProgressBar();
        }
    }

    // Uploads XML, parses it, and combines it with
// HTML markup. Returns HTML string.
    private RecyclerView.Adapter loadXmlFromNetwork(String urlString) {
        InputStream stream = null;
        // Instantiate the parser
        LawsXmlParser lawsXmlParser = new LawsXmlParser();
        List<Law> laws = null;

        try {
            try {
                stream = downloadUrl(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                laws = lawsXmlParser.parse(stream);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        laws.addAll(laws);

        return new SearchLawAdapter(this, laws);
    }

    // Given a string representation of a SEARCH_URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
