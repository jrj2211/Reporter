package com.reportermag.reporter.listeners;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.reportermag.reporter.R;
import com.reportermag.reporter.fragments.ArticleFragment;

public class ArticleListener implements View.OnClickListener {
    private Integer id;
    private Integer color;
    private Activity activity;

    public ArticleListener(int id, int color, Activity activity) {
        this.id = id;
        this.color = color;
        this.activity = activity;
    }

    public void onClick(View v) {
        loadArticleFragment();
    }

    public void loadArticleFragment() {

        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();

        // Set the arguments
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("color", color);

        // Add the article fragment
        Fragment articleFrag = new ArticleFragment();
        articleFrag.setArguments(bundle);

        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
        transaction.replace(R.id.fragment_container, articleFrag);
        transaction.addToBackStack(null);

        // Commit the new fragment
        transaction.commit();
    }
}
