package com.example.ds.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class MyInfoActivity extends AppCompatActivity {
    //나의정보수정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        getSupportActionBar().setTitle("정보 입력");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼
    }
    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기버튼 실행
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void onSaveClicked(View view){ //저장버튼
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

}
