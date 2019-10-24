package com.example.ds.final_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.ds.final_project.db.DeleteWishProduct;
import com.example.ds.final_project.db.InsertWishProduct;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ProductInfo extends AppCompatActivity {
    String IP_ADDRESS = "18.191.10.193";
    Intent reviewIntent;
    private String mJsonString;
    int error=0;
    private TextView product_info; //상세정보 표시
    ImageView productImg; //상품 이미지 표시
    private CheckBox wishCheck; //관심상품 등록
    private boolean infoBool=false; //관심상품 등록 여부
    private int check=0;
    //상품 정보
    private String wishProductName="";
    private String uuid=" ";
    private String productId=" ";
    private String optionNum="";
    private String productURL=" ";
    private String info=" ";
    private String image=" ";

    //수신자 정보
    String phoneName = "";
    String phoneNo = "";

//    private String wishProductName=" ";
    WishProductDialog dialog;
    private String Url="http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dialog=new WishProductDialog(this);
        dialog.setDialogListener(new DialogListener() {
            @Override
            public void onPositiveClicked(String name) {
                wishProductName=name;
                Log.i("관심상품등록",uuid+wishProductName);
                InsertWishProduct task = new InsertWishProduct();
                task.execute("http://" + IP_ADDRESS + "/insertWishProduct.php",uuid,productId,optionNum,image,info,wishProductName);
//                Toast.makeText(ProductInfo.this, "관심 상품으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                Log.i("관심2",wishProductName);
            }

            @Override
            public void onNegativeClicked() {
                Log.d("dialog","취소");
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼

        uuid = getPreferences("uuid");
        product_info=(TextView)findViewById(R.id.product_info);
        product_info.setMovementMethod(new ScrollingMovementMethod());
        Intent intent = getIntent();
        productId=intent.getStringExtra("productId");
        optionNum=intent.getStringExtra("optionNum");
        info=intent.getStringExtra("info");
        image=intent.getStringExtra("image");
        wishProductName=intent.getStringExtra("wishProductName");
        if(wishProductName=="")
            getSupportActionBar().setTitle("상품 상세 정보");
        else
            getSupportActionBar().setTitle(wishProductName+" 상세 정보");
        reviewIntent=new Intent(getApplicationContext(),ReviewActivity.class); //리뷰

        productImg=(ImageView)findViewById(R.id.productImg);
        Log.i("이미지",""+image);
        Glide.with(this).load(image).into(productImg);

        product_info.setText(info);
        Url = Url + productId;
        info = info + "\n" + Url;

        wishCheck=(CheckBox)findViewById(R.id.wishCheck);
        wishCheck.setOnCheckedChangeListener(new CheckBoxListener());

        //등록된 상품인지 확인
        GetWishListItem task = new GetWishListItem();
        task.execute( "http://" + IP_ADDRESS + "/getWishListItem.php",uuid,productId,optionNum);
        wishCheck.setChecked(infoBool);

    }
    public void onReviewClicked(View view){
        reviewIntent.putExtra("product", productId);
        startActivity(reviewIntent);
    }
    //공유 다이얼로그
    public void onShareClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("공유 방식을 선택해주세요.");

        builder.setItems(R.array.LAN, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.LAN);
                Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
                //문자공유
                if(items[pos].equals("문자")){
                    inputPhonNo();
                    phoneNo = findNum(phoneName);
                    if(!phoneNo.equals("그런 사람 없어")){
                        sendMMS();
                        Toast.makeText(getApplicationContext(),phoneName+"님께 해당 상품을 공유했습니다.",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),phoneName+"님의 연락처는 없습니다.",Toast.LENGTH_LONG).show();
                    }
                }
                //카톡공유
                else{
                    ShareKakao();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void inputPhonNo(){
        AlertDialog.Builder ad = new AlertDialog.Builder(ProductInfo.this);

        // 제목 설정
        ad.setTitle("누구에게 공유하시겠습니까? 주소록에 저장된 이름으로 입력해주세요.");
        // 내용 설정
        //ad.setMessage("Message");

        // EditText 삽입하기
        final EditText et = new EditText(ProductInfo.this);
        ad.setView(et);

        // 전송 버튼 설정
        ad.setNegativeButton("보내기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.v(TAG, "Yes Btn Click");

                // Text 값 받아서 로그 남기기
                phoneName = et.getText().toString();
                //Log.v(TAG, value);

                dialog.dismiss();     //닫기
                // Event
            }
        });

        // 취소 버튼 설정
        ad.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.v(TAG,"No Btn Click");
                dialog.dismiss();     //닫기
                // Event
            }
        });

        // 창 띄우기
        ad.show();
    }
    //주소록에서 번호 가져오기
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
        if(number!=null) {
            return number;
        }
        else {
            return "그런 사람 없어";
        }
    }
    private void sendMMS() {
        String sms = info;

        try {
            //전송
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void ShareKakao(){
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
            if(wishCheck.isChecked()&&check!=0) {
                //check=1;
//                wishProductName="";

                dialog.show();
                //DB에 추가
                //InsertWishProduct task = new InsertWishProduct();
//                Log.i("관심상품등록",uuid+wishProductName);
//                task.execute("http://" + IP_ADDRESS + "/insertWishProduct.php",uuid,productId,optionNum,image,info,wishProductName);
//                Toast.makeText(ProductInfo.this, "관심 상품으로 등록되었습니다.", Toast.LENGTH_SHORT).show();

            }
            else if(!wishCheck.isChecked()&&check!=0&&error==0){
                //check=1;ㅎ
                Toast.makeText(ProductInfo.this,"관심 상품 등록 취소되었습니다.",Toast.LENGTH_SHORT).show();
                //DB에서 삭제
                DeleteWishProduct task = new DeleteWishProduct();
                task.execute("http://" + IP_ADDRESS + "/deleteWishProduct.php",uuid,productId,optionNum);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기 버튼 실행
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                ((MainActivity)MainActivity.CONTEXT).onResume();
                finish();
                return true;
            }
            case R.id.showoomi:
                Intent homeIntent=new Intent(this,MainActivity.class);
                startActivity(homeIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetWishListItem extends AsyncTask<String, Void, String>{

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
            String uid = params[1];
            String productId = params[2];
            String optionNum = params[3];
            String serverURL = params[0];
            String postParameters = "uid=" + uid+"&productId="+productId+"&optionNum="+optionNum;

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
    public class InsertWishProduct extends AsyncTask<String, Void, String> {
        //    ProgressDialog progressDialog;
        String TAG = "phptest";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null||result.equals("")){
                Toast.makeText(ProductInfo.this, "관심 상품으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                showResult2();
            }
            Log.d(TAG, "관심상품 등록2" + result);
        }
        @Override
        protected String doInBackground(String... params) {

            String uid = (String)params[1];
            String productId = (String)params[2];
            String optionNum = (String)params[3];
            String image = (String)params[4];
            String info = (String)params[5];
            String wishProductName = (String)params[6];
            String serverURL = (String)params[0];
            String postParameters = "uid=" + uid + "&productId=" + productId+"&optionNum=" + optionNum+"&image=" + image+"&info=" + info+"&wishProductName=" + wishProductName ;

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

                return sb.toString().trim();
            } catch (Exception e) {
                error=1;
                Log.d(TAG, "UpdateData: Error ", e);
                Log.d("에러",e.getMessage());
                return "error";
            }

        }
    }
    private void showResult(){
        // int count=0;
        String TAG_JSON="getWishListItem";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray 길이:",jsonArray.length()+"");
            if(jsonArray.length()>0){
                //관심상품임
                infoBool=true;

            }else {
                //관심상품아님
                infoBool=false;
                Log.d("관심","ㄴ");}
            wishCheck.setChecked(infoBool);
            check=1;
        } catch (JSONException e) {
            //관심상품아님
            Log.d("showResult : ", e.getMessage());
            Log.d("showResult : ", mJsonString);
            infoBool=false;
            Log.d("관심","s");
            wishCheck.setChecked(infoBool);

            check=1;
        }
    }
    private void showResult2(){
        infoBool=false;
        wishCheck.setChecked(infoBool);
        Toast.makeText(ProductInfo.this,"같은 이름으로 등록된 관심상품이 있습니다.",Toast.LENGTH_LONG).show();
        error=0;
    }
}

