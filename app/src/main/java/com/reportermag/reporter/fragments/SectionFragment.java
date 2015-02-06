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
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.ArticlesList;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONObject;

import java.util.ArrayList;


public class SectionFragment extends Fragment implements AsyncResponse, AbsListView.OnScrollListener {

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
        sectionContainer.setOnScrollListener(this);

        return sectionContainer;
    }

    @Override
    public void processFinish(String result) {

        getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
        articles.loadJSON(result);

        // Unset loading
        loading = false;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        //leave this empty
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        int threshold = 1;

        if (!loading && scrollState == SCROLL_STATE_IDLE) {
            if (listView.getLastVisiblePosition() >= listView.getCount() - 1 - threshold) {

                loading = true;
                //load more list items:
                PageContents downloadPage = new PageContents(this);
                downloadPage.execute(getResources().getString(R.string.URL_SECTION) + "?s=" + sectionID + "&ln=" + articles.getLastNode());
            }
        }
    }
}
