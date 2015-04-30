package com.reportermag.reporter.listeners;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.reportermag.reporter.R;
import com.reportermag.reporter.fragments.AuthorFragment;

public class AuthorListener implements View.OnClickListener {
    private Integer id;
    private Activity activity;

    public AuthorListener(int id, Activity activity) {
        this.id = id;
        this.activity = activity;
    }

    public void onClick(View v) {
        loadAuthorFragment();
    }

    public void loadAuthorFragment() {

        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();

        // Set the arguments
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        // Add the article fragment
        Fragment articleFrag = new AuthorFragment();
        articleFrag.setArguments(bundle);

        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
        transaction.replace(R.id.fragment_container, articleFrag);
        transaction.addToBackStack(null);

        // Commit the new fragment
        transaction.commit();
    }
}
