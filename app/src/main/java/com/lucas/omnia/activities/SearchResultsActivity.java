package com.lucas.omnia.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by Lucas on 21/01/2018.
 */

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        /*if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(), mDataSet.get(1).toString(), Toast.LENGTH_SHORT).show();
            for(int i = 0; i < mAdapter.getItemCount(); i++) {

                if (mDataSet.get(i).toString().contains(query)) {
                    String q = mDataSet.get(i).toString();
                    SpannableStringBuilder sb = new SpannableStringBuilder(q);
                    Pattern p = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(q);
                    while (m.find()) {
                        //String word = m.group();
                        //String word1 = notes.substring(m.start(), m.end());

                        sb.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        sb.setSpan(new BackgroundColorSpan(Color.YELLOW), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    mComment.setText(sb);
                }
            }
        }*/
    }
}
