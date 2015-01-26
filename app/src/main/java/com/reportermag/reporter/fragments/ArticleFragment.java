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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.DownloadImageTask;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleFragment extends Fragment implements AsyncResponse {

    private final String TAG = "ArticleFragment";
    private Integer nodeID;
    private LinearLayout articleContainer;
    private LinearLayout bodyContainer;
    private View currentView;
    private SpannableStringBuilder buffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get article to load
        Bundle arguments = this.getArguments();
        nodeID = arguments.getInt("id");

        // Get the page contents for the article
        if (nodeID != 0) {
            Log.i(TAG, "Loading article id " + Integer.toString(nodeID));
            PageContents downloadPage = new PageContents(this);
            downloadPage.execute(getString(R.string.URL_ARTICLE) + Integer.toString(nodeID) + ".json");
        }

        // Make the container
        articleContainer = (LinearLayout) inflater.inflate(R.layout.fragment_article, container, false);
        articleContainer.setVisibility(LinearLayout.GONE);

        bodyContainer = (LinearLayout) articleContainer.findViewById(R.id.article_body);

        return articleContainer;
    }

    @Override
    public void processFinish(String result) {

        articleContainer.setVisibility(LinearLayout.VISIBLE);

        Integer mainColor = 0;
        String author = null;
        String title = null;
        Integer date = 0;
        String imgLink = null;
        JSONArray body = null;

        try {
            Log.i("REPORTER", "Downloaded page JSON contents");

            try {
                JSONObject json = new JSONObject(result.trim());

                mainColor = Color.parseColor(json.getString("sectionColor"));
                author = json.getString("author_fullname");
                title = json.getString("title");
                date = Integer.parseInt(json.getString("date"));
                imgLink = json.getString("imgLink");
                body = (JSONArray) json.get("body");
            } catch (Exception e) {
                Log.e("REPORTER", e.getMessage());
            }

            // Add the Image
            try {
                if (imgLink != null && !imgLink.isEmpty() && !imgLink.equals("[]")) {
                    ImageView articleThumbnail = (ImageView) getActivity().findViewById(R.id.article).findViewById(R.id.article_image);
                    new DownloadImageTask(articleThumbnail).execute(imgLink);
                    articleThumbnail.setAdjustViewBounds(true);
                }
            } catch (Exception e) {
                Log.e("Article", "Could not get image for article id " + Integer.toString(nodeID));
            }

            LinearLayout titlebar = (LinearLayout) getActivity().findViewById(R.id.header);
            titlebar.setBackgroundColor(mainColor);

            TextView titleView = (TextView) getActivity().findViewById(R.id.article_title);
            titleView.setText(title);

            TextView byline = (TextView) getActivity().findViewById(R.id.article_byline);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
            byline.setText("By " + author + " on " + formatter.format(new Date(date * 1000L)));
            byline.setTextColor(Color.parseColor("#151515"));

            buffer = new SpannableStringBuilder();

            parse(body, "");
        } catch (Exception e) {
            Log.e("REPORTER", "Error downloading json contents.");
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
                    }

                }
            } catch (Exception e) {
                Log.w("REPORTER", e.getMessage());
            }
        }
    }
}
