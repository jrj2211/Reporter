package com.reportermag.reporter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        final EditText searchField = (EditText) findViewById(R.id.header_search_field);
        searchField.setVisibility(View.GONE);

        final ImageButton searchButton = (ImageButton) findViewById(R.id.header_search);
        searchButton.setOnClickListener(new View.OnClickListener() {

            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void onClick(View v) {


                LinearLayout searchOptions = (LinearLayout) findViewById(R.id.search_options);

                if (searchActivated) {
                    searchField.setVisibility(View.GONE);
                    logoView.setVisibility(View.VISIBLE);
                    searchOptions.setVisibility(View.GONE);
                    searchActivated = false;
                } else {
                    searchOptions.setVisibility(View.VISIBLE);
                    searchField.setVisibility(View.VISIBLE);
                    searchField.requestFocus();

                    imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);

                    searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                Log.w("sdaf","dsfdfs");
                                imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                                searchField.setFocusable(false);
                                searchField.setFocusable(true);
                                return true;
                            }
                            return false;
                        }
                    });

                    logoView.setVisibility(View.GONE);
                    searchActivated = true;

                    loadSearchFragment();
                }
            }
        });

        final ImageButton moreButton = (ImageButton) findViewById(R.id.header_more);
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
    }

    public void loadSearchFragment() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Set the arguments

        // Add the section fragment
        Fragment searchFrag = new SearchFragment();

        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

        transaction.replace(R.id.fragment_container, searchFrag);

        transaction.addToBackStack(null);

        // Commit the new fragment
        transaction.commit();
    }
}
