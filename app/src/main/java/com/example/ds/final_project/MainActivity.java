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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.example.ds.final_project.db.InsertUser;
import com.example.ds.final_project.db.UpdateUser;
import com.example.ds.final_project.db.UpdateWishList;

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
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    String TAG = "phptest";
    public static Context CONTEXT;
    //메인화면
    Intent searchIntent,wishIntent,infoIntent,webIntent,shopIntent,dbTestIntent; //쇼핑시작,나의관심상품,정보수정
    //서버
    String IP_ADDRESS = "18.191.10.193";
    private String mJsonString;
    //사용자 정보
    private String uuid; //스마트폰 고유번호
    private String name;
    private String address;
    private String phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("쇼움이");
        CONTEXT=this;
        searchIntent=new Intent(getApplicationContext(),searchActivity.class);//쇼핑시작
        wishIntent=new Intent(getApplicationContext(),WishListActivity.class);//나의관심상품
        infoIntent=new Intent(getApplicationContext(),MyInfoActivity.class);//나의정보수정
        webIntent=new Intent(getApplicationContext(),WebActivity.class);//나의정보수정
        shopIntent=new Intent(getApplicationContext(),ShopActivity.class);//나의정보수정

        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permission2 != PackageManager.PERMISSION_GRANTED) { makeRequest(); }

        uuid = GetDevicesUUID(getBaseContext());
        savePreferences("uuid",uuid);
        //서버연결
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getUser.php",uuid);

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
        savePreferences("uuid",uuid);
        //서버연결
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/get_uuid.php",uuid);
    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result == null){
                Log.i(TAG,"RESULT="+result);

            }
            else {
                Log.i(TAG,"RESULT="+result);
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

        String TAG_JSON="person";
        String TAG_ID = "uuid";
        String TAG_NAME = "name";
        String TAG_ADDRESS = "address";
        String TAG_PHONENUM ="phoneNum";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String uuid = item.getString(TAG_ID);
                Log.d("가져온 uuid",uuid);
                Log.d("리얼 uuid",this.uuid);
                if(uuid.equals(this.uuid)){
                    name = item.getString(TAG_NAME);
                    address = item.getString(TAG_ADDRESS);
                    phoneNum = item.getString(TAG_PHONENUM);
                }
            }

        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
        }
        //Log.d("채윤",name+","+gender+","+height+","+top+","+bottom+","+foot);
        savePreferences("name",name);
        savePreferences("address",address);
        savePreferences("phoneNum",phoneNum);

    }

    // 값 저장하기
    private void savePreferences(String key, String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
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
    public void onWebClicked(View view) { startActivity(webIntent); }
    public void onShopClicked(View view) { startActivity(shopIntent); }
    public void onDBTestClicked(View view){ //DB테스트
//
//        UpdateWishList task = new UpdateWishList();
//        // Log.d("productURL"," 삽입"+productURL);
//        task.execute("http://" + IP_ADDRESS + "/insertWishList.php","id","pid");
        UpdateUser task = new UpdateUser();
        task.execute("http://" + IP_ADDRESS + "/updateUser.php","uid","name","채니");
    }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                101);
    }


}
