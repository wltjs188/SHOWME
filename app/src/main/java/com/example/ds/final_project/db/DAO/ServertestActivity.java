package com.example.ds.final_project.db.DAO;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ds.final_project.MainActivity;
import com.example.ds.final_project.R;
import com.example.ds.final_project.db.DTO.Product;

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
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
public class ServertestActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    List<Product> items;

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servertest);

        getList a = new getList();
        a.execute();
    }



    class getList extends AsyncTask<Void, String, Void> {
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
        protected Void doInBackground(Void... param) {
            // TODO Auto-generated method stub
//            try {
//                // httpclient-4.2.2.jar와 httpcore-4.2.2.jar를 mvnrepository에서 찾아 Files에 Download jars를 해 받음
//                // app/libs에 추가
//                // 이때, android상태에서는 libs가 안보임으로 project로 변경해서
//                // lib에 받은 jar파일 2개를 넣어준다.
//
//                // http client 객체
//                HttpClient http = new DefaultHttpClient();
//                // post 방식으로 전송하는 객체
//                HttpPost httpPost = new HttpPost("http://52.78.143.125:8080/showme/GetProduct");
//                // http클라이언트.execute(httppost객체) : 웬서버에 데이터를 전달
//                // 결과(json)가 response로 넘어옴
//                HttpResponse response = http.execute(httpPost);
//                // body에 json 스트링이 넘어옴
//                String body = EntityUtils.toString(response.getEntity());
//                // string을 JSONObject로 변환
//                JSONObject jsonObj = new JSONObject(body);
//                // json객체.get("변수명")
//                JSONArray jArray = (JSONArray) jsonObj.get("getData");
//                Log.d("데이터",jArray.toString());
//                for (int i = 0; i < jArray.length(); i++) {
//                    // json배열.getJSONObject(인덱스)
//                    JSONObject row = jArray.getJSONObject(i);
//                    Product dto = new Product();
//                    dto.setId(row.getInt("ID"));
//                    dto.setName(row.getString("NAME"));
//
//                    items.add(dto);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);

                HttpClient client = new DefaultHttpClient(httpParameters);

                HttpConnectionParams.setConnectionTimeout(httpParameters, 7000);
                HttpConnectionParams.setSoTimeout(httpParameters, 7000);
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);

                // 주소
                String postURL = "http://52.78.143.125:8080/showme/DeleteWishProduct";

                HttpPost post = new HttpPost(postURL);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);

                long startTime = System.currentTimeMillis();

                HttpResponse responsePOST = client.execute(post);

                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.v("debugging", elapsedTime + " ");

                HttpEntity resEntity = responsePOST.getEntity();
//                if (resEntity != null) {
//                    LoadData = EntityUtils.toString(resEntity, HTTP.UTF_8);
//                    Log.i("가져온 데이터",LoadData);
//                    JSONObject jsonObj =new JSONObject(LoadData);
//                    // json객체.get("변수명")
//                    JSONArray jArray = (JSONArray)jsonObj.get("getData");
//                    items=new ArrayList<Product>();
//                    for(int i=0; i<jArray.length();i++){
//                        // json배열.getJSONObject(인덱스)
//                        JSONObject row = jArray.getJSONObject(i);
//                        Product dto =new Product();
//                        dto.setId(row.getInt("ID"));
//                        dto.setName(row.getString("NAME"));
//
//                        // ArrayList에 add
//                        items.add(dto);
//                        Log.i("가져온 데이터",dto.getId()+"");
//                        Log.i("가져온 데이터",dto.getName()+"");
//                    }
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();

        }
    }

}

