package com.kimcheon.showme.final_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AddressActivity extends AppCompatActivity {
    String uuid="";
    String IP_ADDRESS = "18.191.10.193";
    String msg;
    String fnumber;
    private String mJsonString;
    String sproduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_address);
    uuid = getPreferences("uuid");
    String fname="엄마";
    fnumber=findNum(fname);
//    msg="치킨\n"+"http://shorturl.at/acwz8";//메세지 내용
        msg="http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=797893777";
        sproduct="ㅂ"; //공유할 관심상품
        Log.d("fnumber",fnumber);
        sendMSG(findNum("강정현"),msg);
        GetProductToShare task = new GetProductToShare();
        task.execute( "http://" + IP_ADDRESS + "/getProductToShare.php",uuid,sproduct);
    }
    String findNum(String fname){
        String number=null;
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc");
        int i=0;
        while (c.moveToNext()) {

            // 연락처 id 값
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            // 연락처 대표 이름
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
//            Log.d("name",name);
            if(name.trim().equals(fname)) {

                // ID로 전화 정보 조회
                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null, null);

                // 데이터가 있는 경우
                if (phoneCursor.moveToFirst()) {
                    Log.d("name","찾");
                    number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                }
                phoneCursor.close();

            }
        }// end while
        c.close();
        if(number!=null)
            return number;
        else
            return "그런 사람 없어";
    }
    void sendMSG(String number,String msg){
        String strMessage = "구매 부탁드립니다.\nhttp://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=1131760352\n옵션번호:1";
//        String strAttachUrl = "file://"+ Environment.getExternalStorageDirectory()+"/test.jpg";

//        Uri uri = Uri.parse("file://"+ Environment.getExternalStorageDirectory()+"/test.jpg");
        try{
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra("address", number);
            sendIntent.putExtra("subject", "MMS Test");
            sendIntent.putExtra("sms_body", strMessage);
            sendIntent.setType("image/*");
            startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.app_name)));
        }catch (Exception e){
            e.printStackTrace();
        }



//        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        sendIntent.setPackage("com.android.mms");
//        sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
//        sendIntent.putExtra("address", number);
//        sendIntent.putExtra("sms_body", strMessage);
////        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(strAttachUrl));
////
////        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(sendIntent);


//        try {
//            //전송
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(number, null, msg, null, null);
//            Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
    }
    private class GetProductToShare extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddressActivity.this,
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
                showResultGetProductToShare();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String uid = params[1];
            String wishProductName = params[2];
            String serverURL = params[0];
            String postParameters = "uid=" + uid+"&wishProductName="+wishProductName;

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
    private void showResultGetProductToShare(){
        // int count=0;
        String TAG_JSON="getProductToShare";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray 길이:",jsonArray.length()+"");
            if(jsonArray.length()>0){
                //관심상품 sproduct 존재
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    String productId = item.getString("productId");
                    String optionNum = item.getString("optionNum");
                    msg+="\nhttp://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo="+productId+"\n옵션번호 : "+optionNum;
                }
                Log.d("메세지:",msg);
                sendMSG(fnumber,"내용");
            }else {
                //관심상품 sproduct 존재 안함
                Toast.makeText(this,sproduct+" 없어용",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            //관심상품 sproduct 존재 안함
            Log.d("showResult : ", e.getMessage());
            Log.d("showResult : ", mJsonString);
            Toast.makeText(this,sproduct+" 없어용",Toast.LENGTH_LONG).show();
        }

    }
    private String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }
}
