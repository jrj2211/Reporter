package com.reportermag.reporter.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.DownloadImageTask;
import com.reportermag.reporter.util.ObservableScrollView;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends Fragment implements AsyncResponse {

    private final String TAG = "SearchFragment";
    private JSONArray json;
    private LayoutInflater inflater;
    private LinearLayout searchContainer;
    private LinearLayout titlebar;
    private Boolean loading = false;
    private Integer lastNode = null;
    private AsyncResponse activity;
    private Boolean searchForArticles = true;
    private String searchTerms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = this;
        searchTerms = "";

        // Set the titlebar
        titlebar = (LinearLayout) getActivity().findViewById(R.id.header);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor("#151515"));
        colorFade.setDuration(300);
        colorFade.start();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Set Listener
        final EditText searchField = (EditText) getActivity().findViewById(R.id.header_search_field);

        searchField.setText("");

        // Get the container
        ObservableScrollView scrollContainer = (ObservableScrollView) inflater.inflate(R.layout.fragment_search, container, false);

        searchContainer = (LinearLayout) scrollContainer.findViewById(R.id.search);

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    searchField.clearFocus();

                    getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);

                    searchContainer.removeAllViews();

                    // Load the page contents
                    PageContents downloadPage = new PageContents(activity);
                    searchTerms = searchField.getText().toString();

                    if(searchForArticles) {
                        downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms);
                    } else {
                        downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms + "&t=user");
                    }

                    return true;
                }
                return false;
            }
        });

        ((Button)getActivity().findViewById(R.id.search_articles_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchForArticles && !searchTerms.isEmpty()) {
                    searchContainer.removeAllViews();
                    PageContents downloadPage = new PageContents(activity);
                    downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms);
                }

                Button articles = (Button) v;
                Button authors = (Button) getActivity().findViewById(R.id.search_authors_button);

                articles.setTextColor(Color.parseColor("#ffffff"));
                articles.setBackgroundColor(getResources().getColor(R.color.graydark));

                authors.setTextColor(Color.parseColor("#151515"));
                authors.setBackgroundColor(getResources().getColor(R.color.graylight));

                searchForArticles = true;
            }
        });

        ((Button)getActivity().findViewById(R.id.search_authors_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchForArticles && !searchTerms.isEmpty()) {
                    searchContainer.removeAllViews();
                    PageContents downloadPage = new PageContents(activity);
                    downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms + "&t=user");
                }

                Button authors = (Button) v;
                Button articles = (Button) getActivity().findViewById(R.id.search_articles_button);

                authors.setTextColor(Color.parseColor("#ffffff"));
                authors.setBackgroundColor(getResources().getColor(R.color.graydark));

                articles.setTextColor(Color.parseColor("#151515"));
                articles.setBackgroundColor(getResources().getColor(R.color.graylight));

                searchForArticles = false;
            }
        });

        this.inflater = inflater;

        return scrollContainer;
    }

    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.search_options).setVisibility(View.GONE);
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

        if(searchForArticles) {
            Log.i(TAG, "Searching articles...");

            if(json.length() != 0) {
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
                    LinearLayout articleContainer = (LinearLayout) inflater.inflate(R.layout.article_abstract, searchContainer, false);
                    searchContainer.addView(articleContainer);

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

                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Could not get image for article id " + Integer.toString(nodeID));
                    }
                }
            } else {
                // No Results
                TextView noresults = new TextView(getActivity());
                noresults.setText("No results found.");
                noresults.setTextColor(Color.parseColor("#151515"));
                noresults.setTextSize(20);
                searchContainer.addView(noresults);
            }
        } else {

            Log.i(TAG, "Searching authors...");

            // Loop through results
            if(json.length() != 0) {
                for (int i = 0; i < json.length(); i++) {

                    JSONObject author;
                    Integer authorID;

                    // Get the article object
                    try {
                        author = json.getJSONObject(i);
                        authorID = author.getInt("id");
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't get author object.");
                        continue;
                    }

                    // Create author container
                    LinearLayout authorContainer = (LinearLayout) inflater.inflate(R.layout.author_result, searchContainer, false);
                    searchContainer.addView(authorContainer);

                    try {
                        ((TextView) authorContainer.findViewById(R.id.search_author)).setText(author.getString("fullname"));
                    } catch (Exception e) {

                    }
                }
            } else {
                // No Results
                TextView noresults = new TextView(getActivity());
                noresults.setText("No results found.");
                noresults.setTextColor(Color.parseColor("#151515"));
                noresults.setTextSize(20);
                searchContainer.addView(noresults);
            }
        }
    }
}
