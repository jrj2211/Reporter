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
import com.reportermag.reporter.util.ObservableScrollView;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthorFragment extends Fragment implements AsyncResponse {

    private final String TAG = "UserFragment";
    private Integer userID;
    private ObservableScrollView scrollContainer;

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
        userID = arguments.getInt("id");

        // Get the page contents for the article
        if (userID != 0) {
            Log.i(TAG, "Loading user id " + Integer.toString(userID));
            PageContents downloadPage = new PageContents(this);
            downloadPage.execute(getString(R.string.URL_AUTHOR) + "?id=" + Integer.toString(userID));
        }

        // Hide search


        // Get the container
        scrollContainer = (ObservableScrollView) inflater.inflate(R.layout.fragment_author, container, false);
        scrollContainer.setVisibility(LinearLayout.GONE);

        return scrollContainer;
    }

    @Override
    public void processFinish(String result) {
        getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
        scrollContainer.setVisibility(LinearLayout.VISIBLE);

        if(result.isEmpty()) {
            Log.e(TAG, "BADDD");
        }
        JSONObject json;

        try {
            json = new JSONObject(result.trim());
        } catch (Exception e) {
            Log.e("REPORTER", e.getMessage());
            return;
        }

        try {
            ((TextView) scrollContainer.findViewById(R.id.author_fullname)).setText(json.getString("fullname"));
        } catch (Exception e) {
            Log.e(TAG,"Could not add authors fullname");
        }

    }
}