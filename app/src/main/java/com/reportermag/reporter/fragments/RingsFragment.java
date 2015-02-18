package com.reportermag.reporter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.util.AsyncResponse;
import com.reportermag.reporter.util.Titlebar;

public class RingsFragment extends Fragment implements AsyncResponse {

    private final String TAG = "RingsFragment";
    private ListView scrollContainer;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

        // Set titlebar visibility
        getActivity().findViewById(R.id.header_more).setVisibility(View.GONE);
        getActivity().findViewById(R.id.header_search).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.logo).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header_back).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.header_search_field).setVisibility(View.GONE);

        // Set the titlebar
        Titlebar.setColor(getResources().getColor(R.color.rings), (LinearLayout) getActivity().findViewById(R.id.header));

        // Get the container
        scrollContainer = (ListView) inflater.inflate(R.layout.fragment_rings, container, false);

        return scrollContainer;
    }

    @Override
    public void processFinish(String result) {


    }

}
