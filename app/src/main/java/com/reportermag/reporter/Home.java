package com.reportermag.reporter;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
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


public class Home extends CustomActivity implements AsyncResponse {

    private PageContents page = new PageContents();
    private static final String TAG = "Home";
    private JSONArray json;
    private LinearLayout articles;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        articles = (LinearLayout) findViewById(R.id.articles);
        inflater = LayoutInflater.from(this);

        page.delegate = this;
        page.execute(getString(R.string.URL_FRONT));

        Rect scrollBounds = new Rect();
        ScrollView scrollView = (ScrollView) this.findViewById(R.id.scroll_container);
        LinearLayout loadMoreView = (LinearLayout) this.findViewById(R.id.load_more_articles);
        scrollView.getHitRect(scrollBounds);
        if (loadMoreView.getLocalVisibleRect(scrollBounds)) {
            Log.i(TAG, "Loading more articles...");
        }
    }

    @Override
    public void processFinish(String result) {

        try {
            json = new JSONArray(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse JSON.");
        }

        // Loop through results
        for (int i = 0; i < json.length(); i++) {

            try {
                JSONObject article = json.getJSONObject(i);

                // Get Article Details
                String nodeTitle = article.getString("node_title");
                String nodeSection = article.getString("field_section");
                String nodeColor = article.getString("field_color");
                String nodeAuthor = "Author";
                String nodeDate = "12321321";
                String nodeImage = article.getString("imgLink");
                String nodeBody = Html.fromHtml(article.getString("body")).toString();

                // Transform any Article Details
                if (!nodeColor.startsWith("#")) {
                    nodeColor = "#" + nodeColor;
                }

                if (!nodeSection.isEmpty()) {
                    nodeSection = Character.toString(nodeSection.charAt(0));
                }

                // Create article container
                LinearLayout articleContainer = (LinearLayout) inflater.inflate(R.layout.article_abstract, articles, false);
                articles.addView(articleContainer);

                // Add the Title
                TextView articleTitle = (TextView) articleContainer.findViewById(R.id.abstract_title);
                articleTitle.setOnClickListener(new onArticleClickListener(article.getInt("nid"), this));
                articleTitle.setText(nodeTitle);

                // Add Section
                TextView articleSection = (TextView) articleContainer.findViewById(R.id.abstract_section);
                articleSection.setText(nodeSection);
                GradientDrawable bgShape = (GradientDrawable) articleSection.getBackground();
                bgShape.setColor(Color.parseColor(nodeColor));

                // Add Byline
                TextView articleByline = (TextView) articleContainer.findViewById(R.id.abstract_byline);
                articleByline.setText("By " + nodeAuthor + " on " + nodeDate);

                // Add the Image
                if (!nodeImage.isEmpty() && !nodeImage.equals("[]")) {
                    Log.e("REPORTER", "Image: " + nodeImage);
                    try {
                        ImageView articleThumbnail = (ImageView) articleContainer.findViewById(R.id.abstract_image);
                        articleThumbnail.setOnClickListener(new onArticleClickListener(article.getInt("nid"), this));
                        new DownloadImageTask(articleThumbnail).execute(nodeImage);
                        articleThumbnail.setAdjustViewBounds(true);
                        articleThumbnail.setVisibility(ImageView.VISIBLE);
                    } catch (Exception e) {
                        Log.e("REPORTER", "Could not get image from URL");
                    }
                }

                // Add the Summary
                TextView articleSummary = (TextView) articleContainer.findViewById(R.id.abstract_summary);
                articleSummary.setText(nodeBody);

            } catch (Exception e) {
                Log.w("REPORTER", "Error adding article");
            }

        }

    }
}
