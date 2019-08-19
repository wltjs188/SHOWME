package com.example.ds.final_project.db;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ds.final_project.ProductInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class InsertProduct extends AsyncTask<String, Void, String> {

    String TAG = "phptest";
    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

       // progressDialog = ProgressDialog.show(ProductInfo.this,"Please Wait", null, true, true);
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //progressDialog.dismiss();
        Log.d(TAG, "POST response  - " + result);
    }
    @Override
    protected String doInBackground(String... params) {

        String p_id = (String)params[1];
        String p_name = (String)params[2];
        String p_category = (String)params[3];
        String p_length = (String)params[4];
        String p_image = (String)params[5];
        String p_price=(String)params[6];
        String p_size=(String)params[7];
        String p_color=(String)params[8];
        String p_fabric=(String)params[9];
        String p_pattern=(String)params[10];
        String p_detail=(String)params[11];

        String serverURL = (String)params[0];
        String postParameters = "id=" + p_id + "&name=" + p_name + "&category=" + p_category+ "&length=" + p_length+"&image=" + URLEncoder.encode(p_image) + "&price=" + p_price
                +"&size=" + p_size +"&color=" + p_color +"&fabric=" + p_fabric +"&pattern=" + p_pattern +"&detail=" + p_detail;

        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "InsertData: Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }
}
