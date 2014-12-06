package com.reportermag.reporter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class Splash extends Activity implements AsyncResponse {

    PageContents page = new PageContents();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page.delegate = this;

        setContentView(R.layout.activity_splash);
        page.execute("http://reporter.rit.edu/api/article/200.json");
    }

    public void processFinish(String result)  {
        LinearLayout container =(LinearLayout) this.findViewById(R.id.body);

        try {
            JSONObject object = new JSONObject(result.trim());

            TextView title = new TextView(this);
            title.setText(object.get("title").toString());
            title.setTextSize(25);
            title.setTextColor(Color.BLUE);

            parse((JSONArray) object.get("body"));
        } catch (Exception e) {}
    }


    private void parse(JSONObject object) {
        LinearLayout container =(LinearLayout) this.findViewById(R.id.body);

        try {
            String tag = object.get("tag").toString();
            parse((JSONArray) object.get("contents"));
        } catch(Exception e) {}
    }

    private void parse(JSONArray object) {
        LinearLayout container =(LinearLayout) this.findViewById(R.id.body);

        for(int i = 0; i < object.length(); i++) {
            try {
                if(object.get(i) instanceof JSONObject) {
                    parse((JSONObject) object.get(i));
                } else if(object.get(i) instanceof String) {
                    TextView content = new TextView(this);
                    content.setText(object.get(i).toString());
                    content.setTextSize(10);
                    content.setTextColor(Color.BLACK);
                    content.setPadding(0,0,0,20);
                    container.addView(content);
                }
            } catch(Exception e) {}
        }
    }
}
