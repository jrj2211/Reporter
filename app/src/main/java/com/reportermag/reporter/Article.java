package com.reportermag.reporter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.DownloadImageTask;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Article extends CustomActivity {

    private int nodeID;
    private int mainColor;
    private String author;
    private String title;
    private String section;
    private JSONArray body;
    private String imgLink;
    private LinearLayout container;
    private int date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

        nodeID = this.getIntent().getIntExtra("id", 0);

        container = (LinearLayout) this.findViewById(R.id.body);

        if (nodeID != 0) {
            Log.i("Article", "Loading article id " + Integer.toString(nodeID));
            PageContents article = new PageContents((AsyncResponse) this);
            article.execute("article", "http://reporter.rit.edu/api/article/" + Integer.toString(nodeID) + ".json");
        }
    }

    public void processFinish(List<String> result) {
        try {
            Log.i("REPORTER", "Downloaded page JSON contents");

            try {
                JSONObject json = new JSONObject(result.get(1).trim());

                mainColor = Color.parseColor(json.getString("sectionColor"));
                author = json.getString("author_fullname");
                title = json.getString("title");
                section = json.getString("section");
                date = Integer.parseInt(json.getString("date"));
                imgLink = json.getString("imgLink");
                body = (JSONArray) json.get("body");
            } catch (Exception e) {
                Log.e("REPORTER", e.getMessage());
            }

            // Add the Image
            try {
                if (!imgLink.isEmpty() && !imgLink.equals("[]")) {
                    ImageView articleThumbnail = (ImageView) this.findViewById(R.id.article).findViewById(R.id.article_image);
                    new DownloadImageTask(articleThumbnail).execute(imgLink);
                    articleThumbnail.setAdjustViewBounds(true);
                }
            } catch (Exception e) {
                Log.e("Article", "Could not get image for article id " + Integer.toString(nodeID));
            }

            titlebar.setBackgroundColor(mainColor);

            TextView titleView = (TextView) findViewById(R.id.article_title);
            titleView.setText(title);
            titleView.setTextColor(mainColor);

            TextView byline = (TextView) findViewById(R.id.article_byline);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
            byline.setText("By " + author + " on " + formatter.format(new Date(date * 1000L)));
            byline.setTextColor(Color.parseColor("#151515"));

            parse(body, null);
        } catch (Exception e) {
            Log.e("REPORTER", "Error downloading json contents.");
        }
    }


    private void parse(JSONObject json, TextView currentTextView) {
        try {
            if (json.get("tag").equals("p") || json.get("tag").equals("div") || currentTextView == null) {

                currentTextView = new TextView(this);
                currentTextView.setTextColor(Color.parseColor("#151515"));
                currentTextView.setPadding(0, 0, 0, 15);
                container.addView(currentTextView);

            } else if (json.get("tag").equals("b")) {

            }
            parse((JSONArray) json.get("contents"), currentTextView);
        } catch (Exception e) {
            Log.w("REPORTER", e.getMessage());
        }
    }

    private void parse(JSONArray json, TextView currentTextView) {
        for (int i = 0; i < json.length(); i++) {
            try {
                if (json.get(i) instanceof JSONObject) {
                    parse((JSONObject) json.get(i), currentTextView);
                } else if (json.get(i) instanceof String) {
                    currentTextView.setText(currentTextView.getText() + json.getString(i));
                }
            } catch (Exception e) {
                Log.w("REPORTER", "Invalid JSON format.");
            }
        }
    }
}
