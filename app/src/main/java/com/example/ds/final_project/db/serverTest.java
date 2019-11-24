package com.example.ds.final_project.db;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ds.final_project.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

public class serverTest extends AsyncTask<Void, String, Void> {
    String LoadData;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... param) {
        // TODO Auto-generated method stub

        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);

            HttpClient client = new DefaultHttpClient(httpParameters);

            HttpConnectionParams.setConnectionTimeout(httpParameters, 7000);
            HttpConnectionParams.setSoTimeout(httpParameters, 7000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);

            // 주소
            String postURL = "http://52.78.143.125:8080/showme/DBConnection";

            HttpPost post = new HttpPost(postURL);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

//                params.add(new BasicNameValuePair("ProjectID", PID));
//                params.add(new BasicNameValuePair("Itemleft", IL));
//                params.add(new BasicNameValuePair("Itemright", IR));

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//            post.setEntity(ent);

            long startTime = System.currentTimeMillis();

            HttpResponse responsePOST = client.execute(post);

            long elapsedTime = System.currentTimeMillis() - startTime;
            Log.v("debugging", elapsedTime + " ");

            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
                LoadData = EntityUtils.toString(resEntity, HTTP.UTF_8);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
    }
}



