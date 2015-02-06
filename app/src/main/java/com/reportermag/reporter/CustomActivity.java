package com.reportermag.reporter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.Window;

import com.reportermag.reporter.fragments.SearchFragment;

public class CustomActivity extends Activity {

    protected Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        activity = this;

        (findViewById(R.id.header_search)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadSearchFragment();
            }
        });

        (findViewById(R.id.header_more)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        (findViewById(R.id.header_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Remove search fragment from backstack
                FragmentManager manager = getFragmentManager();
                manager.popBackStack();
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
