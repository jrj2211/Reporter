package com.reportermag.reporter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.reportermag.reporter.fragments.SearchFragment;
import com.reportermag.reporter.util.Titlebar;

public class CustomActivity extends Activity {

    protected Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        activity = this;

        Titlebar.setTitlebar(findViewById(R.id.header));

        (findViewById(R.id.header_search)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadSearchFragment();
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

        (findViewById(R.id.header_rings_call)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Text Messaging
                String uri = "smsto:5856724840";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
                intent.putExtra("compose_mode", true);
                startActivity(intent);
                finish();


            }
        });
    }

    public void loadSearchFragment() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Set the arguments

        // Add the section fragment
        Fragment searchFrag = new SearchFragment();

        transaction.replace(R.id.fragment_container, searchFrag);

        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);

        transaction.addToBackStack(null);

        // Commit the new fragment
        transaction.commit();
    }
}
