package com.reportermag.reporter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.RingsList;
import com.reportermag.reporter.util.Titlebar;

import org.json.JSONObject;

import java.util.ArrayList;

public class RingsFragment extends Fragment {

    private final String TAG = "RingsFragment";
    private ListView scrollContainer;
    private LayoutInflater inflater;
    private RingsList ringsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

        // Set the titlebar
        Titlebar.setColor(getResources().getColor(R.color.rings_dark));
        Titlebar.setVisible(Titlebar.VIEWS.BACK, Titlebar.VIEWS.LOGO_RINGS, Titlebar.VIEWS.RINGS_CALL);

        // Get the container
        LinearLayout bodyContainer = (LinearLayout) inflater.inflate(R.layout.fragment_rings, container, false);
        scrollContainer = (ListView) bodyContainer.findViewById(R.id.rings);

        ringsList = new RingsList(getActivity(), new ArrayList<JSONObject>(), scrollContainer);
        ringsList.loadJSON(getString(R.string.URL_RINGS));

        scrollContainer.setAdapter(ringsList);

        return bodyContainer;
    }

}
