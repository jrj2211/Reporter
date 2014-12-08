package com.reportermag.reporter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.onArticleClickListener;

import org.json.JSONArray;
import org.json.JSONObject;


public class Home extends CustomActivity implements AsyncResponse {

    private PageContents page = new PageContents();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        page.delegate = this;
        page.execute("http://reporter.rit.edu/api/frontpage-mobile.json");
    }

    @Override
    public void processFinish(String result) {
        LinearLayout container = (LinearLayout) this.findViewById(R.id.body);

        try {
            JSONArray json = new JSONArray(result.trim());
            for(int i = 0; i < json.length(); i++) {
                JSONObject article = json.getJSONObject(i);

                TextView articleButton = new TextView(this);
                articleButton.setText(article.getString("node_title"));
                container.addView(articleButton);

                articleButton.setOnClickListener(new onArticleClickListener(article.getInt("nid"), this));

            }
        } catch(Exception e) {
            Log.w("REPORTER", "Error parsing JSON");
        }
    }
}
