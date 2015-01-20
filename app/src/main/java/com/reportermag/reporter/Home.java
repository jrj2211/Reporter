package com.reportermag.reporter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.DownloadImageTask;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.onArticleClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Home extends CustomActivity implements AsyncResponse {

    private static final String TAG = "Home";
    private JSONArray json;
    private LinearLayout articles;
    private LayoutInflater inflater;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.activity_home);

        articles = (LinearLayout) findViewById(R.id.articles);
        inflater = LayoutInflater.from(this);

        PageContents articles = new PageContents(this);
        articles.execute("articles", getString(R.string.URL_FRONT));

        PageContents sections = new PageContents(this);
        sections.execute("sections", getString(R.string.URL_SECTIONS));

        Rect scrollBounds = new Rect();
        ScrollView scrollView = (ScrollView) this.findViewById(R.id.scroll_container);
        LinearLayout loadMoreView = (LinearLayout) this.findViewById(R.id.load_more_articles);
        scrollView.getHitRect(scrollBounds);
        if (loadMoreView.getLocalVisibleRect(scrollBounds)) {
            Log.i(TAG, "Loading more articles...");
        }
    }

    private void addSections(String result) {
        try {
            json = new JSONArray(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse sections JSON.");
        }

        // Loop through results
        for (int i = 0; i < json.length(); i++) {

            try {
                LinearLayout drawer = (LinearLayout) findViewById(R.id.drawer_container);

                JSONObject sectionInfo = json.getJSONObject(i);
                Button sectionButton = (Button) inflater.inflate(R.layout.drawer_button, drawer, false);
                sectionButton.setText(sectionInfo.getString("name"));
                String sectionColor = sectionInfo.getString("color");
                if (!sectionColor.startsWith("#")) {
                    sectionColor = "#" + sectionColor;
                }

                sectionButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        articles.removeAllViews();

                        Button b = (Button) v;

                        String text = b.getText().toString().toLowerCase();

                        TextView loading = new TextView(activity);
                        loading.setText("Loading articles...");
                        articles.addView(loading);

                        PageContents articles = new PageContents((AsyncResponse) activity);
                        articles.execute("articles", getString(R.string.URL) + text + "-mobile.json");

                        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                        View drawer = findViewById(R.id.drawer);
                        drawerLayout.closeDrawer(drawer);
                    }

                });

                sectionButton.setBackgroundColor(Color.parseColor(sectionColor));

                drawer.addView(sectionButton);

            } catch (Exception e) {
                Log.e(TAG, "Couldn't add section button to drawer.");
            }

        }
    }

    private void addArticles(String result) {

        // Remove all previous
        articles.removeAllViews();

        try {
            json = new JSONArray(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse articles JSON.");
        }

        // Loop through results
        for (int i = 0; i < json.length(); i++) {

            JSONObject article;
            Integer nodeID;

            // Get the article object
            try {
                article = json.getJSONObject(i);
                nodeID = article.getInt("nid");
            } catch (Exception e) {
                Log.e(TAG, "Couldn't get article object.");
                article = new JSONObject();
                continue;
            }

            // Create article container
            LinearLayout articleContainer = (LinearLayout) inflater.inflate(R.layout.article_abstract, articles, false);
            articles.addView(articleContainer);

            // Add the Title
            try {
                TextView articleTitle = (TextView) articleContainer.findViewById(R.id.abstract_title);
                articleTitle.setOnClickListener(new onArticleClickListener(nodeID, this));
                articleTitle.setText(article.getString("node_title"));
            } catch (Exception e) {
                Log.e(TAG, "Could not get title for article id " + Integer.toString(nodeID));
            }

            // Add the byline
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM. d, yyyy");
                String nodeDate = formatter.format(new Date(Integer.valueOf(article.getString("node_date")) * 1000L));

                TextView articleByline = (TextView) articleContainer.findViewById(R.id.abstract_byline);
                articleByline.setText("By " + article.getString("author") + " on " + nodeDate);
            } catch (Exception e) {
                Log.e(TAG, "Could not get byline for article id " + Integer.toString(nodeID));
            }

            // Add the summary
            try {
                String nodeBody = Html.fromHtml(article.getString("body")).toString();
                TextView articleSummary = (TextView) articleContainer.findViewById(R.id.abstract_summary);
                articleSummary.setText(nodeBody);
            } catch (Exception e) {
                Log.e(TAG, "Could not get summary/trimmed for article id " + Integer.toString(nodeID));
            }

            // Add the Image
            try {
                String nodeImage = article.getString("imgLink");
                if (!nodeImage.isEmpty() && !nodeImage.equals("[]")) {
                    ImageView articleThumbnail = (ImageView) articleContainer.findViewById(R.id.abstract_image);
                    articleThumbnail.setOnClickListener(new onArticleClickListener(article.getInt("nid"), this));
                    new DownloadImageTask(articleThumbnail).execute(nodeImage);
                    articleThumbnail.setAdjustViewBounds(true);
                    articleThumbnail.setVisibility(ImageView.VISIBLE);
                }
            } catch (Exception e) {
                Log.e(TAG, "Could not get image for article id " + Integer.toString(nodeID));
            }

            // Add the section circle
            try {
                String nodeSection = Character.toString(article.getString("field_section").charAt(0));
                String nodeColor = article.getString("field_color");

                // Fix the nodeColor if its invalid hex
                if (!nodeColor.startsWith("#")) {
                    nodeColor = "#" + nodeColor;
                }

                TextView articleSection = (TextView) articleContainer.findViewById(R.id.abstract_section);

                if (!nodeSection.isEmpty()) {
                    articleSection.setText(nodeSection);
                    GradientDrawable bgShape = (GradientDrawable) articleSection.getBackground();
                    bgShape.setColor(Color.parseColor(nodeColor));
                } else {
                    articleSection.setVisibility(TextView.GONE);
                }

            } catch (Exception e) {
                Log.e(TAG, "Could not get section for article id " + Integer.toString(nodeID));
            }
        }
    }

    public void processFinish(List<String> result) {
        try {
            if (result.get(0).equals("articles")) {
                addArticles(result.get(1));
            } else {
                addSections(result.get(1));
            }
        } catch (Exception e) {
            Log.i("REPORTER", "Unknown page contents received.");
        }
    }
}
