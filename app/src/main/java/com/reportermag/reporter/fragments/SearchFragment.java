package com.reportermag.reporter.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.ArticlesList;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.AuthorsList;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONObject;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements AsyncResponse {

    private static Boolean searchForArticles = true;
    private static String searchTerms;
    private LinearLayout searchContainer;
    private ListView bodyContainer;
    private LinearLayout titlebar;
    private Boolean loading = false;
    private AsyncResponse activity;
    private ArticlesList articlesList;
    private AuthorsList authorsList;

    public static void clearSearch() {
        searchTerms = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = this;

        // Setup results containers
        articlesList = new ArticlesList(getActivity(), new ArrayList<JSONObject>());
        authorsList = new AuthorsList(getActivity(), new ArrayList<JSONObject>());

        // Load results if search terms exist
        if (!searchTerms.isEmpty()) {
            PageContents downloadPage = new PageContents(this);

            if (searchForArticles) {
                downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms);
            } else {
                downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms + "&t=user");
            }
        }

        final EditText searchField = (EditText) getActivity().findViewById(R.id.header_search_field);
        searchField.setText(searchTerms);

        // Set visibility
        getActivity().findViewById(R.id.header_more).setVisibility(View.GONE);
        getActivity().findViewById(R.id.header_search).setVisibility(View.GONE);
        getActivity().findViewById(R.id.logo).setVisibility(View.GONE);
        getActivity().findViewById(R.id.header_back).setVisibility(View.VISIBLE);

        searchField.setVisibility(View.VISIBLE);
        searchField.requestFocus();
        searchField.setSelection(searchField.getText().length());

        // Close the drawer
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        View drawer = getActivity().findViewById(R.id.drawer);
        drawerLayout.closeDrawer(drawer);

        // Set the titlebar
        titlebar = (LinearLayout) getActivity().findViewById(R.id.header);
        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor("#151515"));
        colorFade.setDuration(300);
        colorFade.start();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);

        // Get the container
        searchContainer = (LinearLayout) inflater.inflate(R.layout.fragment_search, container, false);

        bodyContainer = (ListView) searchContainer.findViewById(R.id.search);

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    searchField.clearFocus();

                    getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);

                    // Load the page contents
                    PageContents downloadPage = new PageContents(activity);
                    searchTerms = searchField.getText().toString();

                    if (searchForArticles) {
                        downloadPage.execute(getString(R.string.URL_SECTION) + "?search=" + searchTerms);
                    } else {
                        downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms + "&t=user");
                    }

                    return true;
                }
                return false;
            }
        });

        (searchContainer.findViewById(R.id.search_articles_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchForArticles) {
                    selectButton(0);
                    if (!searchTerms.isEmpty()) {
                        getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                        PageContents downloadPage = new PageContents(activity);
                        downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms);
                    }
                }

                searchForArticles = true;
            }
        });

        (searchContainer.findViewById(R.id.search_authors_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchForArticles) {
                    selectButton(1);
                    if (!searchTerms.isEmpty()) {
                        getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                        PageContents downloadPage = new PageContents(activity);
                        downloadPage.execute(getString(R.string.URL_SEARCH) + "?s=" + searchTerms + "&t=user");
                    }
                }

                searchForArticles = false;
            }
        });

        if (searchForArticles) {
            selectButton(0);
        } else {
            selectButton(1);
        }

        return searchContainer;
    }

    public void onStop() {
        super.onStop();
        EditText searchField = (EditText) getActivity().findViewById(R.id.header_search_field);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    @Override
    public void processFinish(String result) {
        getActivity().findViewById(R.id.loading).setVisibility(View.GONE);
        getActivity().findViewById(R.id.search_no_results).setVisibility(View.GONE);

        // Unset loading
        loading = false;

        if (searchForArticles) {
            articlesList.loadJSON(result);
        } else {
            authorsList.loadJSON(result);
        }
    }

    private void selectButton(int type) {
        Button articles = (Button) searchContainer.findViewById(R.id.search_articles_button);
        Button authors = (Button) searchContainer.findViewById(R.id.search_authors_button);

        if (type == 0) {
            articles.setBackgroundColor(Color.parseColor("#151515"));
            articles.setTextColor(Color.WHITE);
            authors.setBackgroundColor(Color.parseColor("#c0c0c0"));
            authors.setTextColor(Color.BLACK);
            bodyContainer.setAdapter(articlesList);
            authorsList.clear();
        } else {
            authors.setBackgroundColor(Color.parseColor("#151515"));
            authors.setTextColor(Color.WHITE);
            articles.setBackgroundColor(Color.parseColor("#c0c0c0"));
            articles.setTextColor(Color.BLACK);
            bodyContainer.setAdapter(authorsList);
            articlesList.clear();
        }
    }
}
