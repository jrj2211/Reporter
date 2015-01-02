package com.reportermag.reporter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Article extends CustomActivity implements AsyncResponse {

    private PageContents page = new PageContents();
    private int mainColor;
    private String author;
    private String title;
    private String section;
    private JSONArray body;
    private String imgLink;
    private int date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

        int nodeid = this.getIntent().getIntExtra("id", 0);

        if(nodeid != 0) {
            page.delegate = this;
            page.execute("http://reporter.rit.edu/api/article/"+Integer.toString(nodeid)+".json");
        }
    }

    public void processFinish(String result)  {
        LinearLayout container =(LinearLayout) this.findViewById(R.id.body);
        Log.w("REPORTER", "Downloaded page JSON contents");

        try {
            JSONObject json = new JSONObject(result.trim());
            mainColor = Color.parseColor(json.get("sectionColor").toString());
            author = json.get("author_fullname").toString();
            title = json.get("title").toString();
            section = json.get("section").toString();
            date = Integer.decode(json.get("date").toString());
            imgLink = json.get("imgLink").toString();
            body = (JSONArray) json.get("body");
        } catch(Exception e) {
            Log.w("REPORTER","Error parsing JSON");
        }


        titlebar.setBackgroundColor(mainColor);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(25);
        titleView.setTextColor(mainColor);
        titleView.setTypeface(OpenSansBold);
        container.addView(titleView);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");

        TextView byline = new TextView(this);
        byline.setText("By " + author + " on " + formatter.format(new Date(date * 1000L)) );
        byline.setTextSize(15);
        byline.setTextColor(Color.BLACK);
        container.addView(byline);

        parse(body);
    }


    private void parse(JSONObject json) {
        try {
            String tag = json.get("tag").toString();
            parse((JSONArray) json.get("contents"));
        } catch(Exception e) {
            Log.w("REPORTER","Invalid JSON format.");
        }
    }

    private void parse(JSONArray json) {
        LinearLayout container =(LinearLayout) this.findViewById(R.id.body);

        for(int i = 0; i < json.length(); i++) {
            try {
                if(json.get(i) instanceof JSONObject) {
                    parse((JSONObject) json.get(i));
                } else if(json.get(i) instanceof String) {
                    TextView content = new TextView(this);
                    content.setText(json.get(i).toString());
                    content.setTextSize(10);
                    content.setTextColor(Color.BLACK);
                    content.setPadding(0,0,0,20);
                    container.addView(content);
                }
            } catch(Exception e) {
                Log.w("REPORTER","Invalid JSON format.");
            }
        }
    }
}