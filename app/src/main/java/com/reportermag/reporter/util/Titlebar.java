package com.reportermag.reporter.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.LinearLayout;

public class Titlebar {

    private LinearLayout titlebar;

    public enum Enum1 {
        SEARCH,
        SEARCH_INPUT,
        MENU,
        RINGS_TEXT,
        RINGS_CALL,
        LOGO
    }

    public static void setColor(int color, LinearLayout titlebar) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), Color.parseColor("#151515"));
        colorFade.setDuration(300);
        colorFade.start();
    }

    public static void setVisible(int... views) {

    }
}
