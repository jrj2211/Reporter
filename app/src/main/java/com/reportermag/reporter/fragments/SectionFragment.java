package com.reportermag.reporter.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.DownloadImageTask;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.onArticleClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SectionFragment extends Fragment implements AsyncResponse {

    private final String TAG = "SectionFragment";
    private JSONArray json;
    private LayoutInflater inflater;
    private LinearLayout sectionContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String section = "home";

        // Get Arguments
        Bundle bundle = this.getArguments();
        if (!bundle.getString("section").isEmpty()) {
            section = bundle.getString("section");
        }

        // Load the page contents
        PageContents downloadPage = new PageContents(this);
        downloadPage.execute(getString(R.string.URL) + section + "-mobile.json");

        sectionContainer = (LinearLayout) inflater.inflate(R.layout.fragment_section, container, false);

        this.inflater = inflater;

        return sectionContainer;
    }

    @Override
    public void processFinish(String result) {

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
            LinearLayout articleContainer = (LinearLayout) inflater.inflate(R.layout.article_abstract, sectionContainer, false);
            sectionContainer.addView(articleContainer);

            // Add the Title
            try {
                TextView articleTitle = (TextView) articleContainer.findViewById(R.id.abstract_title);
                articleTitle.setOnClickListener(new onArticleClickListener(1, getActivity()));
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
                    articleThumbnail.setOnClickListener(new onArticleClickListener(1, getActivity()));
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
}
