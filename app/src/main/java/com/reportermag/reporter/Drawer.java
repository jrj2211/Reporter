package com.reportermag.reporter;

import android.graphics.Color;
import android.os.Bundle;


public class Drawer extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        titlebar.setBackgroundColor(Color.parseColor("#148765"));


    }
}
