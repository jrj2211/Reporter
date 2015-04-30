package com.reportermag.reporter.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.reportermag.reporter.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RingsList extends ArrayAdapter<String> implements AsyncResponse {

    private static LayoutInflater inflater;
    private final Activity context;
    private final String TAG = "RingsList";
    private Integer lastNode = null;
    private ArrayList<JSONObject> rings;
    private boolean loading = false;
    private ListView container;
    private ProgressBar loadMoreView;
    private String lastHeader;

    public RingsList(Activity context, ArrayList objects, ListView container) {
        super(context, R.layout.rings_item, objects);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.container = container;

        loadMoreView = new ProgressBar(context);
        loadMoreView.setPadding(0, 30, 0, 30);

        rings = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.rings_item, null, true);
            holder = new ViewHolder();

            holder.date_day = (TextView) view.findViewById(R.id.rings_date_day);
            holder.date_numeric = (TextView) view.findViewById(R.id.rings_date_numeric);
            holder.text = (TextView) view.findViewById(R.id.rings_text);
            holder.date_header = (TextView) view.findViewById(R.id.rings_date_header);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        JSONObject ring = rings.get(position);
        String messageText = "";

        try {
            if (!ring.getString("date_group").isEmpty()) {
                holder.date_header.setText(ring.getString("date_group"));
                holder.date_header.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            holder.date_header.setVisibility(View.GONE);
        }

        try {
            JSONArray messages = ring.getJSONArray("messages");

            // Loop through messages
            for (int i = 0; i < messages.length(); i++) {
                try {
                    if (!messageText.isEmpty()) {
                        messageText += "\n\n";
                    }
                    messageText += messages.getString(i);
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't get message.");
                }
            }
            holder.text.setText(messageText + "\n\n-" + ring.getString("time"));
        } catch (Exception e) {
            Log.e(TAG, "Could not get message.");
        }

        try {
            holder.date_numeric.setText(ring.getString("date_numeric"));
        } catch (Exception e) {
            Log.e(TAG, "Could not get date.");
        }

        try {
            holder.date_day.setText(ring.getString("date_day"));
        } catch (Exception e) {
            Log.e(TAG, "Could not get date.");
        }

        return view;
    }

    public void loadJSON(String URL) {
        loading = true;

        container.addFooterView(loadMoreView);

        PageContents downloadPage = new PageContents(this);
        downloadPage.execute(URL);
    }

    @Override
    public void processFinish(String result) {

        container.removeFooterView(loadMoreView);

        loading = false;

        JSONArray json;
        try {
            json = new JSONArray(result.trim());
        } catch (Exception e) {
            Log.e(TAG, "Could not parse articles JSON.");
            return;
        }

        // Loop through results
        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject obj = json.getJSONObject(i);
                if (obj.getString("date_group").equals(lastHeader)) {
                    obj.remove("date_group");
                } else {
                    lastHeader = obj.getString("date_group");
                }
                rings.add(obj);
                lastNode = Integer.parseInt(obj.getString("id"));
            } catch (Exception e) {
                Log.e(TAG, "Couldn't get article object.");
            }
        }

        this.notifyDataSetChanged();
    }

    public boolean isLoading() {
        return loading;
    }

    public int getLastNode() {
        return lastNode;
    }

    static class ViewHolder {
        public TextView date_day;
        public TextView date_numeric;
        public TextView date_header;
        public TextView text;
    }
}