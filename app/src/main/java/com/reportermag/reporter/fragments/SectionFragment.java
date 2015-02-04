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
import android.widget.ListView;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.listeners.ArticleListener;
import com.reportermag.reporter.util.ArticlesList;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.DownloadImageTask;
import com.reportermag.reporter.util.ObservableScrollView;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.ScrollViewListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SectionFragment extends Fragment implements AsyncResponse {

    private final String TAG = "SectionFragment";
    private ListView sectionContainer;
    private Integer sectionID;
    private LinearLayout titlebar;
    private Boolean loading = false;
    private ArticlesList articles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Set titlebar visibility
        getActivity().findViewById(R.id.header_more).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header_search).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.logo).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header_back).setVisibility(View.GONE);
        getActivity().findViewById(R.id.header_search_field).setVisibility(View.GONE);

        // Clear Search
        SearchFragment.clearSearch();

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
        ListView sectionContainer = (ListView) inflater.inflate(R.layout.fragment_section, container, false);

        articles = new ArticlesList(getActivity(), new ArrayList<JSONObject>());

        sectionContainer.setAdapter(articles);

        return sectionContainer;
    }

    @Override
    public void processFinish(String result) {

        getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
        articles.loadJSON(result);

        // Unset loading
        loading = false;
    }
}
