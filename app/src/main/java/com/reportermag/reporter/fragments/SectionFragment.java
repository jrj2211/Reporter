package com.reportermag.reporter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.ArticlesList;
import com.reportermag.reporter.util.Titlebar;

import org.json.JSONObject;

import java.util.ArrayList;


public class SectionFragment extends Fragment implements AbsListView.OnScrollListener {

    private Integer sectionID;
    private Boolean loading = false;
    private ArticlesList articles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Set the titlebar
        Titlebar.setColor(getResources().getColor(R.color.graydark));
        Titlebar.setVisible(Titlebar.VIEWS.MENU, Titlebar.VIEWS.LOGO, Titlebar.VIEWS.SEARCH);

        // Clear Search
        SearchFragment.clearSearch();

        // Get Arguments
        Bundle bundle = this.getArguments();
        sectionID = bundle.getInt("section");

        // Get the container
        ListView sectionContainer = (ListView) inflater.inflate(R.layout.fragment_section, container, false);

        articles = new ArticlesList(getActivity(), new ArrayList<JSONObject>(), sectionContainer);
        articles.loadJSON(getString(R.string.URL_ARTICLES) + "?section=" + sectionID.toString());

        sectionContainer.setAdapter(articles);
        sectionContainer.setOnScrollListener(this);

        return sectionContainer;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //leave this empty
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        int threshold = 1;

        if (!articles.isLoading() && scrollState == SCROLL_STATE_IDLE) {
            if (listView.getLastVisiblePosition() >= listView.getCount() - 1 - threshold) {
                articles.loadJSON(getResources().getString(R.string.URL_ARTICLES) + "?section=" + sectionID + "&ln=" + articles.getLastNode());

            }
        }
    }
}
