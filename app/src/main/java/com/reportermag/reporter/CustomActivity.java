package com.reportermag.reporter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.reportermag.reporter.fragments.SearchFragment;

public class CustomActivity extends Activity {

    private boolean searchActivated = false;
    protected LinearLayout titlebar;
    protected static Typeface OpenSansBold;
    protected Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        activity = this;

        final ImageView logoView = (ImageView) findViewById(R.id.logo);
        EditText searchField = (EditText) findViewById(R.id.header_search_field);
        searchField.setVisibility(View.GONE);

        final ImageButton searchButton = (ImageButton) findViewById(R.id.header_search);
        final ImageButton moreButton = (ImageButton) findViewById(R.id.header_more);
        final ImageButton backButton = (ImageButton) findViewById(R.id.header_back);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText searchField = (EditText) findViewById(R.id.header_search_field);

                LinearLayout searchOptions = (LinearLayout) findViewById(R.id.search_options);

                // Change visibility of buttons
                moreButton.setVisibility(View.GONE);
                backButton.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.GONE);
                searchField.setVisibility(View.VISIBLE);
                searchField.requestFocus();

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);

                logoView.setVisibility(View.GONE);
                searchActivated = true;

                loadSearchFragment();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout searchOptions = (LinearLayout) findViewById(R.id.search_options);

                try {
                    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    View drawer = findViewById(R.id.drawer);
                    if (drawerLayout.isDrawerOpen(drawer)) {
                        drawerLayout.closeDrawer(drawer);
                    } else {
                        drawerLayout.openDrawer(drawer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Remove search fragment from backstack
                FragmentManager manager = getFragmentManager();
                manager.popBackStack();

                if(searchActivated) {

                    EditText searchField = (EditText) findViewById(R.id.header_search_field);
                    searchField.setVisibility(View.GONE);

                    // Change visibility of views
                    moreButton.setVisibility(View.VISIBLE);
                    backButton.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    searchField.setVisibility(View.GONE);
                    logoView.setVisibility(View.VISIBLE);

                    searchActivated = false;

                    // Remove keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                }
            }
        });
    }

    public void loadSearchFragment() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Set the arguments

        // Add the section fragment
        Fragment searchFrag = new SearchFragment();

        transaction.replace(R.id.fragment_container, searchFrag);

        transaction.addToBackStack(null);

        // Commit the new fragment
        transaction.commit();
    }
}
