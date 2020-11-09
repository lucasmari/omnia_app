package com.lucas.omnia.activities;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.lucas.omnia.R;

public class LawPageActivity extends BaseActivity {

    public static final String EXTRA_LAW_URN = "law_urn";
    private final String ITEM_URL = "https://www.lexml.gov.br/urn/";
    private String law_urn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law_page);

        law_urn = getIntent().getStringExtra(EXTRA_LAW_URN);
        if (law_urn == null) {
            throw new IllegalArgumentException("Must pass EXTRA_LAW_URN");
        }

        WebView webView = findViewById(R.id.law_page_wv);
        webView.loadUrl(ITEM_URL + law_urn);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
    }
}
