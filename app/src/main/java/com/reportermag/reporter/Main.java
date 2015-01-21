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


public class Main extends CustomActivity implements AsyncResponse {

    private static final String TAG = "Main";
    private JSONArray json;
    private LinearLayout articles;
    private LayoutInflater inflater;
    private LinearLayout fragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Store the inflater
        inflater = LayoutInflater.from(this);

        // Load the default section
        loadSectionFragment("home");

        // Download the sections for the drawer
        PageContents sections = new PageContents(this);
        sections.execute(getString(R.string.URL_SECTIONS));
    }

    public void processFinish(String result) {
        try {
            json = new JSONArray(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse sections JSON.");
        }

        // Loop through results
        for (int i = 0; i < json.length(); i++) {

            try {
                LinearLayout drawer = (LinearLayout) findViewById(R.id.drawer_container);

                JSONObject sectionInfo = json.getJSONObject(i);
                Button sectionButton = (Button) inflater.inflate(R.layout.drawer_button, drawer, false);
                sectionButton.setText(sectionInfo.getString("name").toUpperCase());

                String sectionColor = sectionInfo.getString("color");
                if (!sectionColor.startsWith("#")) {
                    sectionColor = "#" + sectionColor;
                }

                try {
                    Color.parseColor(sectionColor);
                    sectionButton.setTextColor(Color.parseColor(sectionColor));
                } catch (Exception e) {
                }

                sectionButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        // Get button Pressed
                        Button selected = (Button) v;
                        String section = selected.getText().toString().toLowerCase();

                        // Load the section
                        loadSectionFragment(section);

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

    public void loadSectionFragment(String section) {
        loadSectionFragment(section, true);
    }

    public void loadSectionFragment(String section, boolean backstack) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Set the arguments
        Bundle bundle = new Bundle();
        bundle.putString("section", section);

        // Add the section fragment
        Fragment sectionFrag = new SectionFragment();
        sectionFrag.setArguments(bundle);

        transaction.replace(R.id.fragment_container, sectionFrag);
        transaction.addToBackStack(null);

        // Commit the new fragment
        transaction.commit();
    }
}
