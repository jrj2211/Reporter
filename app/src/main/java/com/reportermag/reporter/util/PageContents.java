package com.reportermag.reporter.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

public class PageContents extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;

    private static final int STATUS_OK = 200;

    @Override
    protected String doInBackground(String... urls) {

        String data = null;

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://reporter.rit.edu/api/article/200.json");
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            int status = response.getStatusLine().getStatusCode();
            if(STATUS_OK == status) {
                data = EntityUtils.toString(entity);
            }
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}