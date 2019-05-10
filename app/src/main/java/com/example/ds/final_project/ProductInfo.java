package com.example.ds.final_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductInfo extends AppCompatActivity {
    String TAG = "phptest";
    private String mJsonString;
    private TextView product_info;
    private CheckBox wishCheck;
    private boolean infoBool;
    private String uuid=" ";
    private String productURL=" ";
    private String info=" ";
    private String image=" ";
    String IP_ADDRESS = "35.243.72.245";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        uuid = getPreferences("uuid");
        product_info=(TextView)findViewById(R.id.product_info);
        Intent intent = getIntent();
        info=intent.getStringExtra("info");
        productURL=intent.getStringExtra("url");

        image=intent.getStringExtra("image");
        product_info.setText(info);
        wishCheck=(CheckBox)findViewById(R.id.wishCheck);
        wishCheck.setOnCheckedChangeListener(new CheckBoxListener());

        //등록된 상품인지 확인
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getWishList.php",uuid);

        //uuid+상품url 으로 비교해서 서버에 없으면
       // infoBool=false;
        //서버에 있으면
        //infoBool=true;
        wishCheck.setChecked(infoBool);

    }
    // 값 불러오기
    private String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }
    public void onBackBtnClicked(View view){
        InsertData task = new InsertData();
        Log.d("info",uuid+productURL+info);
        Log.d("info",uuid+productURL+info+image);
        task.execute("http://" + IP_ADDRESS + "/insertWishList.php",uuid,productURL,info,image);
    }
    public class CheckBoxListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 체크박스를 클릭해서 상태가 바꾸었을 경우 호출되는 콜백 메서드
            if(!wishCheck.isChecked()) {
                //토스트 메세지가 왜 안뜰깡..
                Toast.makeText(ProductInfo.this,"관심 상품 등록 취소되었습니다.",Toast.LENGTH_LONG);
                Log.d("체크박스","된다");
                //DB에서 삭제
            }
            else {
                Toast.makeText(ProductInfo.this, "관심 상품으로 등록되었습니다.", Toast.LENGTH_LONG);
                //DB에 추가
                //사용자 정보 DB에 넣기
                InsertData task = new InsertData();
                Log.d("info",uuid+productURL+info);
                task.execute("http://" + IP_ADDRESS + "/insertWishList.php",uuid,productURL,info,image);
            }
        }

    }
    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ProductInfo.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }
        @Override
        protected String doInBackground(String... params) {

            String uuid = (String)params[1];
            String productURL = (String)params[2];
            String info = (String)params[3];
            String image=(String)params[4];
            String serverURL = (String)params[0];
            String postParameters = "uuid=" + uuid + "&productURL=" + productURL + "&info=" + info+"&image="+image;

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
    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ProductInfo.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result == null){
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "uuid=" + params[1];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {
                errorString = e.toString();
                return null;
            }

        }
    }

    private void showResult(){
        int count=0;
        String TAG_JSON="wishList";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String uuid = item.getString("uuid");
                String productURL = item.getString("productURL");
                if(uuid.equals(this.uuid)&&productURL.equals(this.productURL)){
                    count++;
                }
            }
            Log.d("길이길이",count+"");
            if(count>0) infoBool=true;
            else infoBool=false;

        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
        }

    }

}

