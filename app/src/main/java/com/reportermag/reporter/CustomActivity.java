package com.reportermag.reporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Joe on 12/7/2014.
 */
public class CustomActivity extends Activity {

    private boolean searchActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_splash);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        final Activity ActivityAsContainer = this;

        final ImageView logoView = (ImageView) findViewById(R.id.logo);
        final EditText searchField = (EditText) findViewById(R.id.header_search_field);
        searchField.setVisibility(View.GONE);

        final ImageButton searchButton = (ImageButton) findViewById(R.id.header_search);
        searchButton.setOnClickListener(new View.OnClickListener() {

          final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void onClick(View v) {

                if(false == searchActivated) {
                    searchField.setVisibility(View.VISIBLE);
                    searchField.requestFocus();

                    imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);

                    logoView.setVisibility(View.GONE);
                    searchActivated = true;
                } else {
                    searchField.setVisibility(View.GONE);
                    logoView.setVisibility(View.VISIBLE);
                    searchActivated = false;
                }
            }
        });

        final ImageButton moreButton = (ImageButton) findViewById(R.id.header_more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityAsContainer.getClass() != Drawer.class) {
                    Intent intent = new Intent(ActivityAsContainer, Drawer.class);
                    startActivity(intent);
                }
            }
        });
    }
}
