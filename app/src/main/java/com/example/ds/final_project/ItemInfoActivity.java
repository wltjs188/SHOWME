package com.example.ds.final_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ItemInfoActivity extends AppCompatActivity {
    //상품정보
    Intent wishList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        getSupportActionBar().setTitle("상품 정보");
        wishList=getIntent();
        int img=wishList.getIntExtra("img",1);
        ImageView iv= (ImageView)findViewById(R.id.infoImg);
    }
}
