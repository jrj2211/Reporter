package com.reportermag.reporter.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.AsyncResponse;

import org.json.JSONArray;


public class SearchFragment extends Fragment implements AsyncResponse {

    private final String TAG = "SearchFragment";
    private JSONArray json;
    private LayoutInflater inflater;
    private LinearLayout searchContainer;
    private Integer sectionID;
    private LinearLayout titlebar;
    private Boolean loading = false;
    private Integer lastNode = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get Arguments


        // Set the titlebar
        titlebar = (LinearLayout) getActivity().findViewById(R.id.header);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor("#151515"));
        colorFade.setDuration(300);
        colorFade.start();


        getActivity().findViewById(R.id.search_options).setVisibility(View.VISIBLE);

        // Get the container
        searchContainer = (LinearLayout) inflater.inflate(R.layout.fragment_section, container, false);

        this.inflater = inflater;

        return searchContainer;
    }

    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.search_options).setVisibility(View.GONE);
    }

    @Override
    public void processFinish(String result) {

    }



}
