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
    private String mJsonString;

    private TextView product_info; //상세정보 표시
    ImageView productImg; //상품 이미지 표시
    private CheckBox wishCheck; //관심상품 등록
    private boolean infoBool; //관심상품 등록 여부

    //상품 정보
    private String uuid=" ";
    private String productId=" ";
    private String optionNum="";
    private String productURL=" ";
    private String info=" ";
    private String image=" ";

    private String Url="http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle("상품 상세 정보");
        uuid = getPreferences("uuid");
        product_info=(TextView)findViewById(R.id.product_info);
        Intent intent = getIntent();
        productId=intent.getStringExtra("productId");
        optionNum=intent.getStringExtra("optionNum");
        info=intent.getStringExtra("info");
        image=intent.getStringExtra("image");

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
            if(wishCheck.isChecked()) {
                Toast.makeText(ProductInfo.this, "관심 상품으로 등록되었습니다.", Toast.LENGTH_LONG).show();
                //DB에 추가
                InsertWishProduct task = new InsertWishProduct();
                task.execute("http://" + IP_ADDRESS + "/insertWishProduct.php",uuid,productId,optionNum,image,info);

            }
            else {
                Toast.makeText(ProductInfo.this,"관심 상품 등록 취소되었습니다.",Toast.LENGTH_LONG).show();
                //DB에서 삭제
                DeleteWishProduct task = new DeleteWishProduct();
                task.execute("http://" + IP_ADDRESS + "/deleteWishProduct.php",uuid,productId,optionNum);
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
    private void showResult(){
       // int count=0;
        String TAG_JSON="getWishListItem";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray 길이:",jsonArray.length()+"");
            if(jsonArray.length()>0){
                infoBool=true;
            }else { infoBool=false; }
            wishCheck.setChecked(infoBool);
        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
            Log.d("showResult : ", mJsonString);
        }
    }
}

