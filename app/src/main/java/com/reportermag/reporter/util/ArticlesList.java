package com.reportermag.reporter.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reportermag.reporter.R;
import com.reportermag.reporter.listeners.ArticleListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by joe on 2/3/2015.
 */
public class ArticlesList extends ArrayAdapter<String> {

    private final Activity context;
    private final String TAG = "ArticlesList";
    private Integer lastNode = null;
    private ArrayList<JSONObject> articles;
    private static LayoutInflater inflater;

    static class ViewHolder {
        public ImageView thumbnail;
        public TextView title;
        public TextView summary;
        public TextView byline;
        public TextView section;
    }

    public ArticlesList(Activity context, ArrayList objects) {
        super(context,R.layout.article_abstract, objects);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;

        articles = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            view = inflater.inflate(R.layout.article_abstract, null, true);
            holder = new ViewHolder();

            holder.title = (TextView) view.findViewById(R.id.abstract_title);
            holder.summary = (TextView) view.findViewById(R.id.abstract_summary);
            holder.byline = (TextView) view.findViewById(R.id.abstract_byline);
            holder.section = (TextView) view.findViewById(R.id.abstract_section);
            holder.thumbnail = (ImageView) view.findViewById(R.id.abstract_image);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        JSONObject article  = articles.get(position);

        Integer nodeID = 0;
        Integer nodeColor = 0;

        try {
            nodeID = article.getInt("nid");
            lastNode = nodeID;
            nodeColor = Color.parseColor("#151515");
        } catch (Exception e) {
            Log.e(TAG, "Could not get article node");
        }

        // Add the section circle
        try {
            String nodeSection = Character.toString(article.getString("section_name").charAt(0));
            nodeColor = Color.parseColor(article.getString("section_color"));

            if (!nodeSection.isEmpty()) {
                holder.section.setText(nodeSection);
                GradientDrawable bgShape = (GradientDrawable) holder.section.getBackground();
                bgShape.setColor(nodeColor);
            } else {
                holder.section.setVisibility(TextView.GONE);
            }

        } catch (Exception e) {
            Log.e(TAG, "Could not get section for article id " + Integer.toString(nodeID));
        }

        // Add the Title
        try {
            holder.title.setText(article.getString("title"));
            holder.title.setOnClickListener(new ArticleListener(nodeID, nodeColor, context));
        } catch (Exception e) {
            Log.e(TAG, "Could not get title for article id " + Integer.toString(nodeID));
        }

        // Add the byline
        try {
            JSONArray authors = article.getJSONArray("authors");
            String author = authors.getJSONObject(0).getString("fullname");
            holder.byline.setText("By " + author + " on " + article.getString("date_format"));
        } catch (Exception e) {
            Log.e(TAG, "Could not get byline for article id " + Integer.toString(nodeID));
        }

        // Add the summary
        try {
            String nodeBody = Html.fromHtml(article.getString("body")).toString();
            holder.summary.setText(nodeBody);
        } catch (Exception e) {
            Log.e(TAG, "Could not get summary/trimmed for article id " + Integer.toString(nodeID));
        }

        // Add the Image
        try {
            String nodeImage = article.getString("imgLink");
            if (!nodeImage.isEmpty() && !nodeImage.equals("[]")) {
                holder.thumbnail.setAdjustViewBounds(true);
                holder.thumbnail.setVisibility(ImageView.VISIBLE);
                holder.thumbnail.setOnClickListener(new ArticleListener(nodeID, nodeColor, context));

                // Download the image
                new DownloadImageTask(holder.thumbnail).execute(nodeImage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not get image for article id " + Integer.toString(nodeID));
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
                articles.add(json.getJSONObject(i));
            } catch (Exception e) {
                Log.e(TAG, "Couldn't get article object.");
            }
        }

        this.notifyDataSetChanged();
        Log.i(TAG, "notified");
    }
}
