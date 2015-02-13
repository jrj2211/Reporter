package com.reportermag.reporter.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.reportermag.reporter.R;
import com.reportermag.reporter.listeners.AuthorListener;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.InternalClickableSpan;
import com.reportermag.reporter.util.LinkClickableSpan;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.ScrollImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.zip.Inflater;

public class ArticleFragment extends Fragment implements AsyncResponse {

    private final String TAG = "ArticleFragment";
    private Integer nodeID;
    private ScrollView scrollContainer;
    private SpannableStringBuilder buffer;
    private int sectionColor;
    private LayoutInflater inflater;

    private ViewFlipper viewFlipper;
    private float lastX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

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

        // Get the container
        scrollContainer = (ScrollView) inflater.inflate(R.layout.fragment_article, container, false);

        viewFlipper = (ViewFlipper) scrollContainer.findViewById(R.id.article_slideshow);
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewFlipper) v).showNext();

                // Reset the timer
                ((ViewFlipper) v).stopFlipping();
                ((ViewFlipper) v).startFlipping();
            }
        });
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

        return scrollContainer;
    }

    @Override
    public void processFinish(String result) {

        scrollContainer.findViewById(R.id.article_load).setVisibility(LinearLayout.GONE);
        scrollContainer.findViewById(R.id.article).setVisibility(LinearLayout.VISIBLE);

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
            sectionColor = Color.parseColor(json.getString("sectionColor"));
            titlebar.setBackgroundColor(sectionColor);
        } catch (Exception e) {
            Log.e(TAG, "Could not set titlebar.");
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
            byline.setMovementMethod(LinkMovementMethod.getInstance());

            JSONArray authors = json.getJSONArray("authors");
            for (int i = 0; i < authors.length(); i++) {
                JSONObject author = authors.getJSONObject(i);

                if (i > 0) {
                    byline_text.append(", ");
                }

                int start = byline_text.length();
                byline_text.append(author.getString("fullname"));
                try {
                    byline_text.setSpan(new InternalClickableSpan(getActivity(), new AuthorListener(author.getInt("id"), getActivity()), sectionColor), start, byline_text.length(), 0);
                } catch (Exception e) {}
            }

            byline_text.append(" on " + json.getString("date_format"));

            byline.setText(byline_text, TextView.BufferType.SPANNABLE);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        // Set the slideshow
        try {
            JSONArray images = json.getJSONArray("slideshow");

            if(images.length() > 0) {
                viewFlipper.setVisibility(LinearLayout.VISIBLE);
                for (int x = 0; x < images.length(); x++) {
                    try {
                        LinearLayout slide = (LinearLayout) inflater.inflate(R.layout.article_slide, viewFlipper, false);

                        TextView caption = (TextView) slide.findViewById(R.id.slide_caption);
                        caption.setBackgroundColor(sectionColor);
                        caption.setText(images.getJSONObject(x).getString("caption"));

                        ScrollImageView image = (ScrollImageView) slide.findViewById(R.id.slide_image);

                        image.downloadImage(images.getJSONObject(x).getString("url"));
                        image.setDimensions(
                                Integer.parseInt(images.getJSONObject(x).getString("imgWidth")),
                                Integer.parseInt(images.getJSONObject(x).getString("imgHeight")));

                        viewFlipper.addView(slide);
                    } catch (Exception e) { Log.e(TAG, "Error loading slide show image " +Integer.toString(x)); }
                }
            }
        } catch (Exception e) {}

        buffer = new SpannableStringBuilder();

        TextView articleContents = (TextView) scrollContainer.findViewById(R.id.article_body);
        articleContents.setMovementMethod(LinkMovementMethod.getInstance());

        // Parse the body
        try {
            parse(json.getJSONArray("body"), true);
            articleContents.setText(buffer);
        } catch (Exception e) {
            Log.e(TAG, "Could not set the body");
        }
    }

    private void parse(JSONArray json, boolean outerContainer) {
        for (int i = 0; i < json.length(); i++) {
            try {
                if (json.get(i) instanceof JSONObject) {
                    JSONObject element = json.getJSONObject(i);
                    int start = buffer.length();
                    parse(element.getJSONArray("contents"), false);

                    boolean last = false;
                    if(outerContainer && i == json.length()-1) {
                        last = true;
                    }

                    setStyles(element.getString("tag"), start, json.getJSONObject(i), last);
                } else {
                    buffer.append(json.getString(i));
                }
            } catch (Exception e) {
                Log.w("REPORTER", e.getMessage());
            }
        }
    }

    private void setStyles(String tag, int start, JSONObject json, boolean last) {
        String newLine = System.getProperty("line.separator");
        switch (tag) {
            case "p":
            case "div":
                if(!last) {
                    buffer.append(newLine + newLine);
                }
                break;
            case "u":
                buffer.setSpan(new UnderlineSpan(), start, buffer.length(), 0);
                break;
            case "b":
            case "strong":
                buffer.setSpan(new StyleSpan(Typeface.BOLD), start, buffer.length(), 0);
                break;
            case "i":
            case "em":
                buffer.setSpan(new StyleSpan(Typeface.ITALIC), start, buffer.length(), 0);
                break;
            case "a":
                try {
                    buffer.setSpan(new LinkClickableSpan(getActivity(), json.getString("href"), sectionColor), start, buffer.length(), 0);
                } catch (Exception e) { Log.e(TAG, "Could not make link"); }
                break;
        }
    }

}
