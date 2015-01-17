package com.reportermag.reporter.util;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageContents extends AsyncTask<String, Void, List<String>> {

    private AsyncResponse delegate = null;
    private static final int STATUS_OK = 200;

    public PageContents(AsyncResponse context) {
        delegate = context;
    }

    @Override
    protected List<String> doInBackground(String... urls) {

        List<String> data = new ArrayList<>();
        data.add(0, urls[0]);

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(urls[1]);
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            int status = response.getStatusLine().getStatusCode();
            if (STATUS_OK == status) {
                data.add(1, EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}