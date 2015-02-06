package com.reportermag.reporter.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.ScrollImageView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ArticleFragment extends Fragment implements AsyncResponse {

    private final String TAG = "ArticleFragment";
    private Integer nodeID;
    private ScrollView scrollContainer;
    private LinearLayout bodyContainer;
    private View currentView;
    private SpannableStringBuilder buffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Set titlebar visibility
        getActivity().findViewById(R.id.header_more).setVisibility(View.GONE);
        getActivity().findViewById(R.id.header_search).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.logo).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header_back).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header_search_field).setVisibility(View.GONE);

        // Get article to load
        Bundle arguments = this.getArguments();
        nodeID = arguments.getInt("id");

        // Get the page contents for the article
        if (nodeID != 0) {
            Log.i(TAG, "Loading article id " + Integer.toString(nodeID));
            PageContents downloadPage = new PageContents(this);
            downloadPage.execute(getString(R.string.URL_ARTICLE) + Integer.toString(nodeID) + ".json");
        }

        // Hide search


        // Get the container
        scrollContainer = (ScrollView) inflater.inflate(R.layout.fragment_article, container, false);
        scrollContainer.setVisibility(LinearLayout.GONE);

        bodyContainer = (LinearLayout) scrollContainer.findViewById(R.id.article_body);

        return scrollContainer;
    }

    @Override
    public void processFinish(String result) {

        scrollContainer.setVisibility(LinearLayout.VISIBLE);

        // Get the json
        JSONObject json = null;
        try {
            json = new JSONObject(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse json.");
            return;
        }

        // Set the titlebar color
        try {
            LinearLayout titlebar = (LinearLayout) getActivity().findViewById(R.id.header);
            titlebar.setBackgroundColor(Color.parseColor(json.getString("sectionColor")));
        } catch (Exception e) {
            Log.e(TAG, "Could not set title.");
            return;
        }

        // Set the title
        try {
            TextView titleView = (TextView) getActivity().findViewById(R.id.article_title);
            titleView.setText(json.getString("title"));
        } catch (Exception e) {
            Log.e(TAG, "Could not set title.");
            return;
        }

        // Set the byline
        try {

            SpannableStringBuilder byline_text = new SpannableStringBuilder();
            byline_text.append("By ");

            TextView byline = (TextView) getActivity().findViewById(R.id.article_byline);

            JSONArray authors = json.getJSONArray("authors");
            for (int i = 0; i < authors.length(); i++) {
                JSONObject author = authors.getJSONObject(i);

                if (i > 0) {
                    byline_text.append(", ");
                }


                int start = byline_text.length();
                byline_text.append(author.getString("fullname"));
                //byline_text.setSpan(click, start, byline_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            byline_text.append(" on " + json.getString("date_format"));

            byline.setText(byline_text);

        } catch (Exception e) {
            Log.e(TAG, "Could not set byline.");
            return;
        }

        // Set the image
        try {
            String imgLink = json.getString("imgLink");
            int imgWidth = Integer.parseInt(json.getString("imgWidth"));
            int imgHeight = Integer.parseInt(json.getString("imgHeight"));

            if (imgLink != null && !imgLink.isEmpty() && !imgLink.equals("[]")) {
                ScrollImageView articleThumbnail = (ScrollImageView) getActivity().findViewById(R.id.article).findViewById(R.id.article_image);
                articleThumbnail.setDimensions(imgWidth, imgHeight);
                articleThumbnail.downloadImage(imgLink);
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not get image for article id " + Integer.toString(nodeID));
        }

        buffer = new SpannableStringBuilder();

        // Parse the body
        try {
            parse(json.getJSONArray("body"), "");
        } catch (Exception e) {
            Log.e(TAG, "Could not set the body");
        }
    }

    private void parse(JSONObject json) {
        try {

            parse((JSONArray) json.get("contents"), json.getString("tag"));

            if (currentView == null) {
                currentView = new TextView(getActivity());
                currentView.setPadding(0, 0, 0, 20);
                ((TextView) currentView).setTextSize(15);
                ((TextView) currentView).setTextColor(Color.parseColor("#151515"));
            }

            if (json.getString("tag").equals("p")) {
                ((TextView) currentView).setText(buffer, TextView.BufferType.SPANNABLE);
                buffer.clear();
                bodyContainer.addView(currentView);
                currentView = null;
            }

        } catch (Exception e) {
            Log.w("REPORTER", e.getMessage());
        }
    }

    private void parse(JSONArray json, String tag) {
        for (int i = 0; i < json.length(); i++) {
            try {
                if (json.get(i) instanceof JSONObject) {
                    parse((JSONObject) json.get(i));
                } else {

                    int start = buffer.length();
                    buffer.append(json.getString(i));

                    switch (tag) {
                        case "u":
                            buffer.setSpan(new UnderlineSpan(), start, buffer.length(), 0);
                            break;
                        case "b":
                            buffer.setSpan(new StyleSpan(Typeface.BOLD), start, buffer.length(), 0);
                            break;
                        case "i":
                            buffer.setSpan(new StyleSpan(Typeface.ITALIC), start, buffer.length(), 0);
                            break;
                        case "a":
                            Log.i(TAG, "link!");
                    }

                }
            } catch (Exception e) {
                Log.w("REPORTER", e.getMessage());
            }
        }
    }
}
