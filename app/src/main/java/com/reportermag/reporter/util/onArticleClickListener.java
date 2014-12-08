package com.reportermag.reporter.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.reportermag.reporter.Article;

public class onArticleClickListener implements View.OnClickListener {

    private int nodeid;
    private Activity activity;

    public onArticleClickListener(int nodeid, Activity activity) {
        this.nodeid = nodeid;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.activity, Article.class);
        intent.putExtra("id", this.nodeid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.activity.startActivity(intent);
    }
}
