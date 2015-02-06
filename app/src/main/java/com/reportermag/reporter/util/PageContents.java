package com.reportermag.reporter.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PageContents extends AsyncTask<String, Void, String> {

    private static final int STATUS_OK = 200;
    private AsyncResponse delegate = null;

    public PageContents(AsyncResponse context) {
        delegate = context;
    }

    @Override
    protected String doInBackground(String... urls) {

        String data = null;

        String url = urls[0].replaceAll(" ", "%20");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            Log.i("PageContents", url);
            HttpEntity entity = response.getEntity();

            int status = response.getStatusLine().getStatusCode();
            if (STATUS_OK == status) {
                data = EntityUtils.toString(entity);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}