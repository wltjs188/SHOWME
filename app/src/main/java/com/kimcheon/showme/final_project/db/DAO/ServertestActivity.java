package com.kimcheon.showme.final_project.db.DAO;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kimcheon.showme.final_project.R;
import com.kimcheon.showme.final_project.db.DTO.Product;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

public class ServertestActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    List<Product> items;

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servertest);

//        HorizontalScrollView scrollView = (HorizontalScrollView)findViewById(R.id.ScrollView);
//        scrollView.addView(new Button(this));


        getList a = new getList();
        a.execute("GetSizeDressOriginal","aaaa");
    }



    class getList extends AsyncTask<String, Void,String> {
        String LoadData;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ServertestActivity.this);
            pDialog.setMessage("검색중입니다..");
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String project = (String)params[0];
            String uid = (String)params[1];
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);

                HttpClient client = new DefaultHttpClient(httpParameters);

                HttpConnectionParams.setConnectionTimeout(httpParameters, 7000);
                HttpConnectionParams.setSoTimeout(httpParameters, 7000);
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);

                // 주소 : aws서버
                String postURL = "http://52.78.143.125:8080/showme/";

                // 로컬서버
//            String postURL = "http://10.0.2.2:8080/showme/";

                HttpPost post = new HttpPost(postURL+project);
                //서버에 보낼 파라미터
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                //파라미터 추가하기
                postParameters.add(new BasicNameValuePair("id", "942363"));
                postParameters.add(new BasicNameValuePair("size", "FREE"));
//            postParameters.add(new BasicNameValuePair("infoName", infoName));
//            postParameters.add(new BasicNameValuePair("infoValue", infoValue));
//            for(int i=0;i<params.length;i++){
//                postParameters.add(new BasicNameValuePair("\""+(String)params[i]+"\"",(String)params[i]));
//                System.out.println("\""+(String)params[i]+"\","+(String)params[i]);
//            }
//            postParameters.add(new BasicNameValuePair("uid", "0"));
//            postParameters.add(new BasicNameValuePair("wishProductName", "1"));
//            postParameters.add(new BasicNameValuePair("value", "123"));



                //파라미터 보내기
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postParameters, HTTP.UTF_8);
                post.setEntity(ent);

                long startTime = System.currentTimeMillis();

                HttpResponse responsePOST = client.execute(post);

                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.v("debugging", elapsedTime + " ");


                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    LoadData = EntityUtils.toString(resEntity, HTTP.UTF_8);
                    Log.i("가져온 데이터", LoadData);
                    JSONObject jsonObj = new JSONObject(LoadData);
                    // json객체.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("getData");
                    items = new ArrayList<Product>();
                    for (int i = 0; i < jArray.length(); i++) {
                        // json배열.getJSONObject(인덱스)
                        JSONObject row = jArray.getJSONObject(i);
                        //float 으로 저장
//                        float total=Float.valueOf(row.getString("TOTAL"));
//                        float WAIST=Float.valueOf(row.getString("WAIST"));
//                        float BREAST=Float.valueOf(row.getString("BREAST"));
//                        float CROTCH=Float.valueOf(row.getString("CROTCH"));
//                        float TAIL=Float.valueOf(row.getString("TAIL"));
                        //string으로 저장
//                        String total=row.getString("TOTAL");
//                        String WAIST=row.getString("WAIST");
//                        String BREAST=row.getString("BREAST");
//                        String CROTCH=row.getString("CROTCH");
//                        String TAIL=row.getString("TAIL");

//                        Log.i("가져온정보",total+","+WAIST+","+TAIL);

//                        Product dto = new Product();
//                        dto.setId(row.getInt("ID"));
//                        dto.setName(row.getString("NAME"));
//
//                        // ArrayList에 add
//                        items.add(dto);
//                        Log.i("가져온 데이터", dto.getId() + "");
//                        Log.i("가져온 데이터", dto.getName() + "");
                    }
                }
                if(responsePOST.getStatusLine().getStatusCode()==200){
                    System.out.println("오류없음");
                }
                else{
                    System.out.println("오류");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();

        }
    }

}

