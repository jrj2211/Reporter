package com.reportermag.reporter.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.reportermag.reporter.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthorsList extends ArrayAdapter<String> {

    private static LayoutInflater inflater;
    private final String TAG = "AuthorsList";
    private ArrayList<JSONObject> authors;

    public AuthorsList(Activity context, ArrayList objects) {
        super(context, R.layout.author_result, objects);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        authors = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.author_result, null, true);
            holder = new ViewHolder();

            holder.name = (TextView) view.findViewById(R.id.search_author);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        JSONObject author = authors.get(position);

        Integer authorID = 0;

        try {
            authorID = author.getInt("id");
            holder.name.setText(author.getString("fullname"));
        } catch (Exception e) {
            Log.e(TAG, "Could not get author");
        }

        return view;
    }

    public void loadJSON(String result) {
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
                authors.add(obj);
            } catch (Exception e) {
                Log.e(TAG, "Couldn't get article object.");
            }
        }

        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView name;
    }
}
