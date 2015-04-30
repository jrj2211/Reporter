package com.reportermag.reporter.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;

import com.reportermag.reporter.R;

import java.util.EnumMap;
import java.util.Iterator;

public class Titlebar {

    private static LinearLayout titlebar;
    private static EnumMap<VIEWS, View> views = new EnumMap<VIEWS, View>(VIEWS.class);

    public static void setTitlebar(View titlebar) {
        Titlebar.titlebar = (LinearLayout) titlebar;

        // add the views
        views.put(VIEWS.SEARCH, titlebar.findViewById(R.id.header_search));
        views.put(VIEWS.SEARCH_INPUT, titlebar.findViewById(R.id.header_search_field));
        views.put(VIEWS.MENU, titlebar.findViewById(R.id.header_menu));
        views.put(VIEWS.BACK, titlebar.findViewById(R.id.header_back));
        views.put(VIEWS.RINGS_CALL, titlebar.findViewById(R.id.header_rings_call));
        views.put(VIEWS.LOGO, titlebar.findViewById(R.id.logo));
        views.put(VIEWS.LOGO_RINGS, titlebar.findViewById(R.id.logo_rings));
        views.put(VIEWS.SHARE, titlebar.findViewById(R.id.header_share));
    }

    public static void setColor(int color) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(titlebar, "backgroundColor", new ArgbEvaluator(), ((ColorDrawable) titlebar.getBackground()).getColor(), color);
        colorFade.setDuration(300);
        colorFade.start();
    }

    public static void setVisible(VIEWS... v) {
        Iterator<VIEWS> enumKeySet = views.keySet().iterator();

        while (enumKeySet.hasNext()) {
            VIEWS id = enumKeySet.next();
            View current = views.get(id);
            boolean show = false;

            for (int i = 0; i < v.length; i++) {
                if (id == v[i]) {
                    show = true;
                    break;
                }
            }

            if (show == true) {
                current.setVisibility(View.VISIBLE);
            } else if (current.isShown()) {
                current.setVisibility(View.GONE);
            }
        }
    }

    public enum VIEWS {
        SEARCH,
        SEARCH_INPUT,
        SHARE,
        MENU,
        BACK,
        RINGS_CALL,
        LOGO,
        LOGO_RINGS
    }
}
