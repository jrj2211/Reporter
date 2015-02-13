package com.reportermag.reporter.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class InternalClickableSpan extends ClickableSpan {

    protected Activity activity;
    private int color;
    private View.OnClickListener listener;

    public InternalClickableSpan(Activity activity, View.OnClickListener listener, int color) {
        this.activity = activity;
        this.color = color;
        this.listener = listener;
    }

    @Override
    public void onClick(View widget) {
        listener.onClick(widget);
    }

    public void updateDrawState(TextPaint p_DrawState) {
        super.updateDrawState(p_DrawState);
        p_DrawState.setUnderlineText(false);
        p_DrawState.setColor(color);
    }
}
