package com.example.ds.final_project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.ds.final_project.db.DTO.User;
import com.example.ds.final_project.db.DAO.ServertestActivity;
import com.example.ds.final_project.db.UpdateWishProductName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import static android.speech.tts.TextToSpeech.ERROR;
import org.apache.http.message.BasicNameValuePair;
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public static Context CONTEXT;
    //메인화면
    Intent searchIntent,wishIntent,infoIntent,webIntent,shopIntent,dbTestIntent,reviewIntent; //쇼핑시작,나의관심상품,정보수정

    //사용자 정보
    private String uuid; //스마트폰 고유번호
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Ask for permision
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.SEND_SMS}, 1);
        }
        else {
// Permission has already been granted
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setTitle("쇼우미");
        CONTEXT=this;
        searchIntent=new Intent(getApplicationContext(),ChatbotActivity.class);//쇼핑시작
        wishIntent=new Intent(getApplicationContext(),WishListActivity.class);//나의관심상품
        infoIntent=new Intent(getApplicationContext(),MyInfoActivity.class);//나의정보수정
        webIntent=new Intent(getApplicationContext(),WebActivity.class);//나의정보수정
        shopIntent=new Intent(getApplicationContext(),ShopActivity.class);//나의정보수정
        reviewIntent=new Intent(getApplicationContext(),ReviewActivity.class); //리뷰

        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permission2 != PackageManager.PERMISSION_GRANTED) { makeRequest(); }

        uuid = GetDevicesUUID(getBaseContext());
        savePreferences("uuid",uuid);
        Log.d("연결 uuid",uuid);
        //서버연결
        SelectData task = new SelectData();
        task.execute( "SelectUser",uuid);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);

                }
            }
        });
//
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak("화면 아무 곳이나 터치하시면 쇼우미가 시작됩니다.",TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 1000);
        //키해시 구하기
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.ds.final_project", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

   public void onResume(){
        super.onResume();
//       tts.speak("화면 아무 곳이나 터치하시면 쇼우미가 시작됩니다.",TextToSpeech.QUEUE_FLUSH, null);
        savePreferences("uuid",uuid);
        //서버연결
       SelectData task = new SelectData();
        task.execute( "SelectUser",uuid);
    }

    class SelectData extends AsyncTask<String, Void,String> {
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

                    Log.d("가져온 데이터", LoadData);
                    return LoadData;
                }
                if(responsePOST.getStatusLine().getStatusCode()==200){
                    Log.d("오류없음","굳");
                }
                else{
                    Log.d("error","오류");
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("??","?");
            Log.d("??",result);

//            pDialog.dismiss();
            if (result == null||result==""){
                Log.d("로긴","실패");
//                User user=new User();
//                Gson gson = new GsonBuilder().create();
//
//                String strContact = gson.toJson(user, User.class);
//                savePreferences("USER",strContact);
                removePreferences("USER");
//                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    // json객체.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("userData");
//                    items = new ArrayList<Product>();
                    if(jArray.length()==0){
//                        User user=new User();
//                        Gson gson = new GsonBuilder().create();
//
//                        String strContact = gson.toJson(user, User.class);
                        Log.d("로긴","로그인 실패");
                        removePreferences("USER");
                    }else {
                        Log.i("로긴","성공"+result);
                        User user=new User();
                        for (int i = 0; i < jArray.length(); i++) {
                            // json배열.getJSONObject(인덱스)
                            JSONObject row = jArray.getJSONObject(i);
                            String id=row.getString("id");
                            String name=row.getString("name");
                            String address=row.getString("address");
                            String phoneNum=row.getString("phoneNum");

                            user.setId(id);
                            user.setName(name);
                            user.setAddress(address);
                            user.setPhoneNum(phoneNum);

                            Gson gson = new GsonBuilder().create();

                            String strContact = gson.toJson(user, User.class);
                            savePreferences("USER",strContact);

                            Log.d("가져온 데이터",id);
                            Log.d("가져온 데이터", address);
                            Log.d("가져온 데이터", name);
                            Log.d("가져온 데이터", phoneNum);
                        }

                    }

                } catch (JSONException e) {
                    Log.d("error : ", e.getMessage());
                }
            }
        }
    }

    // 값 저장하기
    private void savePreferences(String key, String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }

    // 값 삭제하기
    public void removePreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
    //스마트폰 고유번호 가져오기
    private String GetDevicesUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        String uuid=" ";
        try{
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
            uuid = deviceId;
            Log.d("uuid0",uuid);
        }
        catch(SecurityException e){ }
        return uuid;
    }
    public void onSearchClicked(View view) { //쇼핑시작
        startActivity(searchIntent);
    } //쇼핑시작버튼
    public void onWishClicked(View view) { //나의관심상품
        startActivity(wishIntent);
    } //관심상품버튼
    public void onInfoClicked(View view) { //나의정보수정
        startActivityForResult(infoIntent,1);
    } //나의정보수정버튼
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            Toast.makeText(getApplicationContext(), "정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
        }
    }
    public void onReviewClicked(View view) { //쇼핑시작
        startActivity(reviewIntent);
    }
    public void onWebClicked(View view) { startActivity(webIntent); }
    public void onShopClicked(View view) { startActivity(shopIntent); }

    public void onDBTestClicked(View view){ //DB테스트
//        String name="ㅂㅈ";
////        String change="sbsb";
////        UpdateWishProductName task1 = new UpdateWishProductName(); //사용자정보 수정
////        task1.execute("http://" + IP_ADDRESS + "/updateWishProductName.php",uuid,name,change);
////        Log.d("나",uuid);
////        Toast.makeText(this,"업데이트",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(), ServertestActivity.class);
        startActivity(intent);
    }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                101);
    }

}
