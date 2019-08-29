package com.example.ds.final_project.db;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteWishProduct extends AsyncTask<String, Void, String> {
    // ProgressDialog progressDialog;
    String TAG = "phptest";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //        progressDialog = ProgressDialog.show(ProductInfo.this,
        //        "Please Wait", null, true, true);
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //  progressDialog.dismiss();
        Log.d(TAG, "POST response  - " + result);
    }
    @Override
    protected String doInBackground(String... params) {
        String uid = (String)params[1];
        String productId = (String)params[2];
        String optionNum = (String)params[3];

        String serverURL = (String)params[0];
        Log.d("삭제할 데이터",uid+productId);
        String postParameters = "uid=" + uid + "&productId=" + productId+ "&optionNum=" + optionNum;

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
