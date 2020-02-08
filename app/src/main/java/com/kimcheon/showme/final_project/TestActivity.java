package com.kimcheon.showme.final_project;


import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



        sendSMS("01039354325","m.store.musinsa.com","경기도");


    }
    public void sendSMS(String num,String productUrl,String address){
        String text="이 상품 구매 부탁드립니다. \n";
        text+="주소: "+address+"\n";
        text+=productUrl;
        Uri smsUri = Uri.parse("sms:"+num);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
        intent.putExtra("sms_body", text);
        startActivity(intent);
    }

}
