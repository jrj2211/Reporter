package com.reportermag.reporter.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.ArticlesList;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONObject;

import java.util.ArrayList;

public class AuthorFragment extends Fragment implements AsyncResponse, AbsListView.OnScrollListener {

    private final String TAG = "UserFragment";
    private Integer userID;
    private ListView mainContainer;
    private ArticlesList articles;
    private LinearLayout profile;

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

        // Set the titlebar
        LinearLayout titlebar = (LinearLayout) getActivity().findViewById(R.id.header);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor("#151515"));
        colorFade.setDuration(300);
        colorFade.start();

        // Get the page contents for the article
        if (userID != 0) {
            Log.i(TAG, "Loading user id " + Integer.toString(userID));
            PageContents downloadPage = new PageContents(this);
            downloadPage.execute(getString(R.string.URL_AUTHOR) + "?id=" + Integer.toString(userID));
        }

        // Get the container
        mainContainer = (ListView) inflater.inflate(R.layout.fragment_author, container, false);
        mainContainer.setVisibility(LinearLayout.GONE);

        profile = (LinearLayout) inflater.inflate(R.layout.author_profile, null, false);

        mainContainer.addHeaderView(profile);
        mainContainer.setOnScrollListener(this);

        return mainContainer;
    }

    @Override
    public void processFinish(String result) {

        mainContainer.setVisibility(LinearLayout.VISIBLE);

        JSONObject json;

        try {
            json = new JSONObject(result.trim());
        } catch (Exception e) {
            Log.e("REPORTER", e.getMessage());
            return;
        }

        try {
            ((TextView) profile.findViewById(R.id.author_fullname)).setText(json.getString("fullname") + "'s Profile");
        } catch (Exception e) {
            Log.e(TAG, "Could not add authors fullname");
        }

        try {
            ((TextView) profile.findViewById(R.id.author_about)).setText(json.getString("about"));
        } catch (Exception e) {
            Log.e(TAG, "Could not add authors about");
        }

        articles = new ArticlesList(getActivity(), new ArrayList<JSONObject>(), mainContainer);

        ListView author_articles = (ListView) getActivity().findViewById(R.id.author_articles);
        author_articles.setAdapter(articles);
        articles.loadJSON(getString(R.string.URL_ARTICLES) + "?user=" + userID);
        articles.showLess();
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
                articles.loadJSON(getResources().getString(R.string.URL_ARTICLES) + "?user=" + userID + "&ln=" + articles.getLastNode());

            }
        }
    }
}