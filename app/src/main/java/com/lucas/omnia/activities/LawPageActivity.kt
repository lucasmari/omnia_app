package com.lucas.omnia.activities

import android.os.Bundle
import android.webkit.WebView
import com.lucas.omnia.R

class LawPageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_law_page)
        law_urn = intent.getStringExtra(EXTRA_LAW_URN)
        requireNotNull(law_urn) { "Must pass EXTRA_LAW_URN" }
        val webView = findViewById<WebView>(R.id.law_page_wv)
        webView.loadUrl(ITEM_URL + law_urn)
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
    }

    companion object {
        const val ITEM_URL = "https://www.lexml.gov.br/urn/"
        var law_urn: String? = null
        const val EXTRA_LAW_URN = "law_urn"
    }
}