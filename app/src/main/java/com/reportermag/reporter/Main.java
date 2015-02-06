package com.reportermag.reporter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.reportermag.reporter.fragments.SectionFragment;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Main extends CustomActivity implements AsyncResponse {

    private static final String TAG = "Main";
    private JSONArray json;
    private LayoutInflater inflater;
    private Map<Integer, Fragment> sections;
    private int sectionsIsPopulated = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sections = new HashMap<>();

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

                        if (sectionsIsPopulated == -1) {
                            // Download the sections for the drawer
                            PageContents sections = new PageContents((AsyncResponse) activity);
                            sections.execute(getString(R.string.URL_SECTIONS));
                            sectionsIsPopulated = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Store the inflater
        inflater = LayoutInflater.from(this);

        // Load the default section
        loadSectionFragment(0, false);

        // Download the sections for the drawer
        PageContents sections = new PageContents((AsyncResponse) activity);
        sections.execute(getString(R.string.URL_SECTIONS));
    }

    public void processFinish(String result) {

        try {
            json = new JSONArray(result.trim());
            sectionsIsPopulated = 1;
        } catch (Exception e) {
            Log.e(TAG, "Could not parse sections JSON.");
            sectionsIsPopulated = -1;
        }

        // Loop through results
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {

                try {
                    LinearLayout drawer = (LinearLayout) findViewById(R.id.drawer_container);

                    JSONObject sectionInfo = json.getJSONObject(i);
                    Button sectionButton = (Button) inflater.inflate(R.layout.drawer_button, drawer, false);
                    sectionButton.setText(sectionInfo.getString("name").toUpperCase());

                    try {
                        sectionButton.setTag(Integer.parseInt(sectionInfo.getString("id")));
                    } catch (Exception e) {
                        sectionButton.setTag(0);
                    }

                    String sectionColor = sectionInfo.getString("color");

                    try {
                        Color.parseColor(sectionColor);
                        sectionButton.setTextColor(Color.parseColor(sectionColor));
                    } catch (Exception e) {
                        sectionButton.setTextColor(Color.parseColor("#ffffff"));
                    }

                    sectionButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {

                            // Get button Pressed
                            Integer section = (Integer) v.getTag();

                            // Load the section
                            loadSectionFragment(section, true);

                            // Close the drawer
                            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                            View drawer = findViewById(R.id.drawer);
                            drawerLayout.closeDrawer(drawer);
                        }

                    });

                    drawer.addView(sectionButton);

                } catch (Exception e) {
                    Log.e(TAG, "Couldn't add section button to drawer.");
                }

            }
        }
    }

    public void loadSectionFragment(Integer sectionID, boolean backstack) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Set the arguments
        Bundle bundle = new Bundle();
        bundle.putInt("section", sectionID);

        // Add the section fragment
        Fragment sectionFrag = sections.get(sectionID);
        if (sectionFrag == null) {
            sectionFrag = new SectionFragment();
            sectionFrag.setArguments(bundle);
            sections.put(sectionID, sectionFrag);
        }

        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

        transaction.replace(R.id.fragment_container, sectionFrag);

        if (backstack) {
            transaction.addToBackStack(null);
        }

        // Commit the new fragment
        transaction.commit();
    }
}
