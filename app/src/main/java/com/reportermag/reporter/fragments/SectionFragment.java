package com.reportermag.reporter.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.reportermag.reporter.util.ObservableScrollView;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.ScrollViewListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SectionFragment extends Fragment implements AsyncResponse, ScrollViewListener {

    private final String TAG = "SectionFragment";
    private JSONArray json;
    private LayoutInflater inflater;
    private LinearLayout sectionContainer;
    private Integer sectionID;
    private LinearLayout titlebar;
    private Boolean loading = false;
    private Integer lastNode = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get Arguments
        Bundle bundle = this.getArguments();
        sectionID = bundle.getInt("section");

        // Load the page contents
        PageContents downloadPage = new PageContents(this);
        downloadPage.execute(getString(R.string.URL_SECTION) + "?s=" + sectionID.toString());

        // Set the titlebar
        titlebar = (LinearLayout) getActivity().findViewById(R.id.header);

        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor("#151515"));
        colorFade.setDuration(300);
        colorFade.start();

        // Get the container
        sectionContainer = (LinearLayout) inflater.inflate(R.layout.fragment_section, container, false);

        ((ObservableScrollView) getActivity().findViewById(R.id.scroll_container)).setScrollViewListener(this);

        this.inflater = inflater;

        return sectionContainer;
    }

    @Override
    public void processFinish(String result) {

        getActivity().findViewById(R.id.loading).setVisibility(View.GONE);

        // Unset loading
        loading = false;

        try {
            json = new JSONArray(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse articles JSON.");
            return;
        }

        // Loop through results
        for (int i = 0; i < json.length(); i++) {

            JSONObject article;
            Integer nodeID;

            // Get the article object
            try {
                article = json.getJSONObject(i);
                nodeID = article.getInt("nid");
                lastNode = nodeID;
            } catch (Exception e) {
                Log.e(TAG, "Couldn't get article object.");
                continue;
            }

            String nodeColor = "#151515";

            // Create article container
            LinearLayout articleContainer = (LinearLayout) inflater.inflate(R.layout.article_abstract, sectionContainer, false);
            sectionContainer.addView(articleContainer);

            // Add the section circle
            try {
                String nodeSection = Character.toString(article.getString("section_name").charAt(0));
                nodeColor = article.getString("section_color");

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

            Map<String, Object> articleInfo = new HashMap<>();
            articleInfo.put("id", nodeID);
            articleInfo.put("color", nodeColor);

            // Add the Title
            try {
                TextView articleTitle = (TextView) articleContainer.findViewById(R.id.abstract_title);
                articleTitle.setTag(articleInfo);
                articleTitle.setText(article.getString("title"));

                // Add the onclick listener
                articleTitle.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Map<String, Object> articleInfo = (HashMap<String, Object>) v.getTag();
                        loadArticleFragment((Integer) articleInfo.get("id"), (String) articleInfo.get("color"));
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Could not get title for article id " + Integer.toString(nodeID));
            }

            // Add the byline
            try {
                JSONArray authors = article.getJSONArray("authors");
                TextView articleByline = (TextView) articleContainer.findViewById(R.id.abstract_byline);
                String author = authors.getJSONObject(0).getString("fullname");
                articleByline.setText("By " + author + " on " + article.getString("date_format"));
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
                    articleThumbnail.setTag(articleInfo);
                    articleThumbnail.setAdjustViewBounds(true);
                    articleThumbnail.setVisibility(ImageView.VISIBLE);

                    // Download the image
                    new DownloadImageTask(articleThumbnail).execute(nodeImage);

                    // Add the onclick listener
                    articleThumbnail.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Map<String, Object> articleInfo = (HashMap<String, Object>) v.getTag();
                            loadArticleFragment((Integer) articleInfo.get("id"), (String) articleInfo.get("color"));
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Could not get image for article id " + Integer.toString(nodeID));
            }
        }
    }

    public void loadArticleFragment(int nodeID, String color) {

        getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Set the arguments
        Bundle bundle = new Bundle();
        bundle.putInt("id", nodeID);

        // Add the article fragment
        Fragment articleFrag = new ArticleFragment();
        articleFrag.setArguments(bundle);

        transaction.replace(R.id.fragment_container, articleFrag);
        transaction.addToBackStack(null);

        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor(color));
        colorFade.setDuration(300);
        colorFade.start();

        // Commit the new fragment
        transaction.commit();
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        if (!loading) {
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);

            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
            // if diff is zero, then the bottom has been reached
            if (diff <= 500) {
                loading = true;
                loadMoreResults();
            }
        }
    }

    public void loadMoreResults() {
        Log.w(TAG, "Loading more results after node " + lastNode);

        PageContents downloadPage = new PageContents(this);
        downloadPage.execute(getString(R.string.URL_SECTION) + "?s=" + sectionID.toString() + "&ln=" + lastNode);
    }
}
