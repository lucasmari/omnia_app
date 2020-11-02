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
import com.lucas.omnia.adapters.LawAdapter;
import com.lucas.omnia.models.Entry;
import com.lucas.omnia.utils.LawsXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LawsActivity extends BaseActivity {

    private TextView noneTv;
    private RecyclerView recyclerView;
    private final String URL = "https://www.lexml.gov.br/busca/SRU?operation=searchRetrieve&query" +
            "=\"";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laws);

        setProgressBar(R.id.laws_pb);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.laws_rv);
        recyclerView.setLayoutManager(layoutManager);

        EditText searchEt = findViewById(R.id.laws_et);
        ImageButton searchBt = findViewById(R.id.laws_ib_search);
        searchBt.setOnClickListener(v -> {
            showProgressBar();
            String query = searchEt.getText().toString();
            query = query.replace(' ', '+');
            String url = URL + query + "\"";
            new DownloadXmlTask().execute(url);
        });
        noneTv = findViewById(R.id.laws_tv_none);
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, RecyclerView.Adapter> {
        @Override
        protected RecyclerView.Adapter doInBackground(String... urls) {
            return loadXmlFromNetwork(urls[0]);
        }

        @Override
        protected void onPostExecute(RecyclerView.Adapter result) {
            if (result.getItemCount() == 0) noneTv.setVisibility(View.VISIBLE);
            else recyclerView.setAdapter(result);
            hideProgressBar();
        }
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
// HTML markup. Returns HTML string.
    private RecyclerView.Adapter loadXmlFromNetwork(String urlString) {
        InputStream stream = null;
        // Instantiate the parser
        LawsXmlParser lawsXmlParser = new LawsXmlParser();
        List<Entry> entries = null;

        try {
            try {
                stream = downloadUrl(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                entries = lawsXmlParser.parse(stream);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

        entries.addAll(entries);

        return new LawAdapter(entries);
    }

    // Given a string representation of a URL, sets up a connection and gets
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
