package com.kimcheon.showme.final_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProductImageActivity extends AppCompatActivity {

    Button btn;
    ImageView product_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image);

        getSupportActionBar().setTitle("상품 이미지");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        
        btn = findViewById(R.id.btn_close);
        product_image = findViewById(R.id.product_image);

        Intent intent=getIntent();
        String image = intent.getStringExtra("image");

        Glide.with(this).load(image).into(product_image);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기 버튼 실행
        Intent homeIntent=new Intent(this,ChatbotActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (item.getItemId()){
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
//                startActivity(homeIntent);
                finish();
                return true;

            case R.id.showme:
                startActivity(homeIntent);
                finish();
                return true;
            case R.id.help:
                AlertDialog.Builder oDialog = new AlertDialog.Builder(ProductImageActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                oDialog.setTitle("도움말")
                        .setMessage("상품의 이미지가 크게 보여집니다.\n" +
                                "닫기 버튼을 누르면 화면이 닫힙니다."
                                )
                        .setPositiveButton("닫기", null)
                        .setCancelable(true)
                        .show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
