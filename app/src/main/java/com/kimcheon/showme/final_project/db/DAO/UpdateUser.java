package com.kimcheon.showme.final_project.db.DAO;

import android.os.AsyncTask;
import android.util.Log;

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

public class UpdateUser extends AsyncTask<String, Void,String> {
    String LoadData;
    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        String project = (String)params[0];
        String uid = (String)params[1];
        String attribute = (String)params[2];
        String value = (String)params[3];
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
//            String postURL = "http://10.0.2.2:8080/showme/InsertUser";

            HttpPost post = new HttpPost(postURL+project);
            //서버에 보낼 파라미터
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            //파라미터 추가하기
            postParameters.add(new BasicNameValuePair("uid", uid));
            postParameters.add(new BasicNameValuePair("attribute", attribute));
            postParameters.add(new BasicNameValuePair("value", value));



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
                Log.i("수정 가져온 데이터", LoadData);
                JSONObject jsonObj = new JSONObject(LoadData);
                // json객체.get("변수명")
                JSONArray jArray = (JSONArray) jsonObj.get("count");
//                    items = new ArrayList<Product>();
//                    for (int i = 0; i < jArray.length(); i++) {
                // json배열.getJSONObject(인덱스)
//                        JSONObject row = jArray.getJSONObject(i);
//                        Product dto = new Product();
//                        dto.setId(row.getInt("ID"));
//                        dto.setName(row.getString("NAME"));

                // ArrayList에 add
//                        items.add(dto);

//                        Log.i("가져온 데이터", dto.getId() + "");
//                        Log.i("가져온 데이터", dto.getName() + "");
//                    }
            }
            if(responsePOST.getStatusLine().getStatusCode()==200){
                Log.d("수정","오류없음");
            }
            else{
                Log.d("수정","오류");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

    }
}