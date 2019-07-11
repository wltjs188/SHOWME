package com.example.ds.final_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.CommerceDetailObject;
import com.kakao.message.template.CommerceTemplate;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ProductInfo extends AppCompatActivity {
    String IP_ADDRESS = "35.243.72.245";
    String TAG = "phptest";
    private String mJsonString;

    private TextView product_info; //상세정보 표시
    ImageView productImg; //상품 이미지 표시
    private CheckBox wishCheck; //관심상품 등록
    private boolean infoBool; //관심상품 등록 여부

    //상품 정보
    private String uuid=" ";
    private String productURL=" ";
    private String info=" ";
    private String image=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle("상품 상세 정보");
        uuid = getPreferences("uuid");
        product_info=(TextView)findViewById(R.id.product_info);
        Intent intent = getIntent();
        info=intent.getStringExtra("info");
        productURL=intent.getStringExtra("url");
        Log.d("detailurl","상세정보 : "+productURL);
        productImg=(ImageView)findViewById(R.id.productImg);
        image=intent.getStringExtra("image");
        Log.i("이미지",""+image);
        Glide.with(this).load(image).into(productImg);

        //info = getPreferences("visionResult")+"음"+info;
        if(getPreferences("visionResult")!=null){
            //visionResult 번역
            NaverTranslateTask asyncTask = new NaverTranslateTask();
            String visionResult = getPreferences("visionResult");
            asyncTask.execute(visionResult);
            //info+=getPreferences("visionResult");
        }
        //product_info.setText(info);
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
    public void onKakaoClicked(View view){
        ContentObject contentObject = ContentObject.newBuilder(
                info,
                image,
                LinkObject.newBuilder()
                        .setWebUrl("https://style.kakao.com/main/women/contentId=100")
                        .setMobileWebUrl("https://m.style.kakao.com/main/women/contentId=100")
                        .build())
                .build();

        CommerceDetailObject commerceDetailObject = CommerceDetailObject.newBuilder(208800)
                .setDiscountPrice(146160)
                .setDiscountRate(30)
                .build();

        ButtonObject firstButtonObject = new ButtonObject("구매하기",
                LinkObject.newBuilder()
                        .setWebUrl("https://style.kakao.com/main/women/contentId=100/buy")
                        .setMobileWebUrl("https://style.kakao.com/main/women/contentId=100/buy")
                        .build());

        ButtonObject secondButtobObject = new ButtonObject("공유하기",
                LinkObject.newBuilder()
                        .setWebUrl("https://style.kakao.com/main/women/contentId=100/share")
                        .setMobileWebUrl("https://m.style.kakao.com/main/women/contentId=100/share")
                        .setAndroidExecutionParams("contentId=100&share=true")
                        .setIosExecutionParams("contentId=100&share=true")
                        .build());

        CommerceTemplate params =  CommerceTemplate.newBuilder(contentObject, commerceDetailObject)
                .addButton(firstButtonObject)
                .addButton(secondButtobObject)
                .build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
                Log.d("kakao",errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }
    // 값 불러오기
    private String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public class CheckBoxListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 체크박스를 클릭해서 상태가 바꾸었을 경우 호출되는 콜백 메서드
            if(!wishCheck.isChecked()) {
                //토스트 메세지가 왜 안뜰깡..
                Toast.makeText(ProductInfo.this,"관심 상품 등록 취소되었습니다.",Toast.LENGTH_LONG).show();
                //DB에서 삭제
                DeleteData task = new DeleteData();
            //    Log.d("info",uuid+productURL+info);
                task.execute("http://" + IP_ADDRESS + "/delete.php",uuid,productURL);
            }
            else {
                Toast.makeText(ProductInfo.this, "관심 상품으로 등록되었습니다.", Toast.LENGTH_LONG).show();
                //DB에 추가
                InsertData task = new InsertData();
                Log.d("productURL"," 삽입"+productURL);
                task.execute("http://" + IP_ADDRESS + "/insertWishList.php",uuid,productURL,info,image);
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기버튼 실행
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                ((MainActivity)MainActivity.CONTEXT).onResume();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
            //String productURL = (String)params[2];
            String info = (String)params[3];
            String image=(String)params[4];
            String serverURL = (String)params[0];
            String postParameters = "uuid=" + uuid + "&productURL=" + URLEncoder.encode(productURL) + "&info=" + info+"&image="+image;

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
    class DeleteData extends AsyncTask<String, Void, String>{
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
            String serverURL = (String)params[0];
            Log.d("삭제할 데이터",uuid+productURL);
            String postParameters = "uuid=" + uuid + "&productURL=" + URLEncoder.encode(productURL);

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
    class UpdateData extends AsyncTask<String, Void, String>{
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
            Log.d(TAG, "aaaaaaaaaaaa" + result);
        }
        @Override
        protected String doInBackground(String... params) {

            String uuid = (String)params[1];
            String infoName = (String)params[2];
            String value = (String)params[3];



            String serverURL = (String)params[0];
            String postParameters = "uuid=" + uuid + "&infoName=" + infoName + "&value=" + value;

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
                Log.d(TAG, "UpdateData: Error ", e);
                Log.d("에러",e.getMessage());
                return new String("Error: " + e.getMessage());
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
//                Log.d("서버에서","받은"+uuid);
//                Log.d("서버에서","받은"+productURL);
//                Log.d("서버에서","진짜"+this.uuid);
//                Log.d("서버에서","진짜"+this.productURL);
                if(uuid.equals(this.uuid)&&productURL.equals(this.productURL)){ //DB에 있으면 count
                    count++;
                }
            }
            Log.d("길이길이",count+"");
            if(count>0) //관심상품 맞아
                infoBool=true;
            else //관심상품 아냐
                infoBool=false;
            wishCheck.setChecked(infoBool);
        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
        }

    }
    //ASYNCTASK
    public class NaverTranslateTask extends AsyncTask<String, Void, String> {

        public String resultText;
        //Naver
        String clientId = "y35TS1jdnSaYzjZFyeqv";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "J6pTI2_r1j";//애플리케이션 클라이언트 시크릿값";
        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.
        String sourceLang = "en";
        String targetLang = "ko";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {
//네이버제공 예제 복사해 넣자.
//Log.d("AsyncTask:", "1.Background");

            String sourceText = strings[0];

            try {
                //String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else { // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                //System.out.println(response.toString());
                return response.toString();

            } catch (Exception e) {
                //System.out.println(e);
                Log.d("error", e.getMessage());
                return null;
            }
        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //최종 결과 처리부
            //Log.d("background result", s.toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.

            //JSON데이터를 자바객체로 변환해야 한다.
            //Gson을 사용할 것이다.

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString()).getAsJsonObject().get("message").getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
            //Log.d("result", items.getTranslatedText());
            //번역결과를 텍스트뷰에 넣는다.
            //tvResult.setText(items.getTranslatedText());
            info+=items.getTranslatedText();
            product_info.setText(info);
            Log.i("이미지분석",info);
        }



        //자바용 그릇
        private class TranslatedItem {
            String translatedText;


            public String getTranslatedText() {

                translatedText=translatedText.replaceAll(System.getProperty("line.separator"),"&");
                return translatedText;
            }
        }
    }

}

