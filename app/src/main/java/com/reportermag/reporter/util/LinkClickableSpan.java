package com.reportermag.reporter.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class LinkClickableSpan extends ClickableSpan {

    private String URL;
    protected Activity activity;
    private int color;

    public LinkClickableSpan(Activity activity, String URL, int color) {
        this.URL = URL;
        this.activity = activity;
        this.color = color;
    }

    @Override
    public void onClick(View widget) {
        Uri uriUrl = Uri.parse(URL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        activity.startActivity(launchBrowser);
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
        p_DrawState.setColor(color);
    }
}
