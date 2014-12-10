package com.reportermag.reporter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.PageContents;
import com.reportermag.reporter.util.onArticleClickListener;

import org.json.JSONArray;
import org.json.JSONObject;


public class Home extends CustomActivity implements AsyncResponse {

    private PageContents page = new PageContents();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        page.delegate = this;
        page.execute("http://reporter.rit.edu/api/frontpage-mobile.json");
    }

    @Override
    public void processFinish(String result) {
        LinearLayout container = (LinearLayout) this.findViewById(R.id.body);

        try {
            JSONArray json = new JSONArray(result.trim());

            for(int i = 0; i < json.length(); i++) {

                try {
                    JSONObject article = json.getJSONObject(i);

                    // Create article container
                    RelativeLayout articleContainer = new RelativeLayout(this);
                    articleContainer.setPadding(0,0,0,10);
                    container.addView(articleContainer);
                    articleContainer.setOnClickListener(new onArticleClickListener(article.getInt("nid"), this));

                    // Add the Title
                    TextView articleTitle = new TextView(this);
                    articleTitle.setText(article.getString("node_title"));
                    articleTitle.setTextColor(Color.BLACK);
                    articleTitle.setTypeface(OpenSansBold);
                    articleTitle.setTextSize(20);
                    articleTitle.setId(R.id.homeArticleTitle);
                    RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    articleContainer.addView(articleTitle, titleParams);

                    // Add Section
                    TextView articleSection = new TextView(this);
                    articleSection.setText(article.getString("field_section"));
                    articleSection.setTextColor(Color.WHITE);
                    articleSection.setPadding(3,3,3,3);
                    String color = article.getString("field_color");
                    if(!color.startsWith("#")) {
                        color = "#" + color;
                    }
                    articleSection.setBackgroundColor(Color.parseColor(color));
                    articleSection.setTextSize(15);
                    articleSection.setId(R.id.homeArticleSection);
                    RelativeLayout.LayoutParams sectionParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    sectionParams.setMargins(0,0,10,0);
                    sectionParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    sectionParams.addRule(RelativeLayout.BELOW, R.id.homeArticleTitle);
                    articleContainer.addView(articleSection, sectionParams);

                    // Add Byline
                    TextView articleByline = new TextView(this);
                    articleByline.setText("By author on 12/12/2012");
                    articleByline.setTextColor(Color.BLACK);
                    articleByline.setTextSize(15);
                    articleByline.setPadding(3,3,3,3);
                    articleByline.setId(R.id.homeArticleByline);
                    RelativeLayout.LayoutParams bylineParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bylineParams.addRule(RelativeLayout.BELOW, R.id.homeArticleTitle);
                    bylineParams.addRule(RelativeLayout.RIGHT_OF, R.id.homeArticleSection);
                    articleContainer.addView(articleByline, bylineParams);

                    // Add the Image
                    ImageView thumbnail = new ImageView(this);
                    thumbnail.setAdjustViewBounds(true);
                    thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.net));
                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    imageParams.addRule(RelativeLayout.BELOW, R.id.homeArticleSection);
                    articleContainer.addView(thumbnail, imageParams);

                } catch(Exception e) {
                    Log.w("REPORTER", "Error adding article");
                }

            }
        } catch(Exception e) {
            Log.w("REPORTER", "Error parsing JSON");
        }
    }
}
