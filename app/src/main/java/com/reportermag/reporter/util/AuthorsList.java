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
import com.reportermag.reporter.listeners.AuthorListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthorsList extends ArrayAdapter<String> implements AsyncResponse {

    private static LayoutInflater inflater;
    private final String TAG = "AuthorsList";
    private ArrayList<JSONObject> authors;
    private Context context;
    private boolean loading = false;
    private ListView container;
    private ProgressBar loadMoreView;

    public AuthorsList(Activity context, ArrayList objects, ListView container) {
        super(context, R.layout.author_result, objects);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;

        this.container = container;

        loadMoreView = new ProgressBar(context);
        loadMoreView.setPadding(0, 30, 0, 30);

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

        view.setOnClickListener(new AuthorListener(authorID, (Activity) context));

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
        loading = false;

        container.removeFooterView(loadMoreView);

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

        View noResults = ((Activity) context).findViewById(R.id.search_no_results);
        if (noResults != null) {
            if (authors.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.GONE);
            }
        }

        this.notifyDataSetChanged();
    }

    public boolean isLoading() {
        return loading;
    }

    static class ViewHolder {
        public TextView name;
    }
}
