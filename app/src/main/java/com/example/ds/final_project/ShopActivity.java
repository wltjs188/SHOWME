package com.example.ds.final_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private EditText keywordEdt;
    private Button searchBtn;
    private Button moreBtn;
    private List<Product> productList;
    private GridView GridView;
    private ProductAdapter adapter;
    ProductSearchService service;
    String keyword;
    Intent productInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfo = new Intent(getApplicationContext(),ProductInfo.class);
        keywordEdt = (EditText)findViewById(R.id.main_keyword_edt);
        searchBtn = (Button) findViewById(R.id.main_search_btn);
        moreBtn = (Button) findViewById(R.id.main_more_btn);
        productList = new ArrayList<Product>();
        adapter = new ProductAdapter(this, R.layout.list_product_item, productList);
        GridView = (GridView) findViewById(R.id.main_GridView);
        GridView.setAdapter(adapter);
        Intent intent=getIntent();
        //keyword=intent.getStringExtra("원피스");
        keyword="원피스";
        keywordEdt.setText(keyword);
        service = new ProductSearchService(keyword);
        ProductSearchThread thread = new ProductSearchThread(service, handler);
        Toast.makeText(getApplicationContext(), "검색을 시작합니다.", Toast.LENGTH_LONG).show();
        thread.start();

// 상품검색
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                keyword = keywordEdt.getText().toString();
                service = new ProductSearchService(keyword);
                ProductSearchThread thread = new ProductSearchThread(service, handler);
                Toast.makeText(getApplicationContext(), "검색을 시작합니다.", 0).show();
                thread.start();
            }
        });

        //클릭시, 상세정보 페이지로 이동
        GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                //int img=adapter.getImg(position);
                //productInfo.putExtra("img",img);
                startActivity(productInfo);
            }
        });

        //더보기
        moreBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                service.nextPage(keyword);
                ProductSearchThread thread = new ProductSearchThread(service, handler);
                Toast.makeText(getApplicationContext(), "더보기", 0).show();
                thread.start();
            }
        });

    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what ==1 )
            {
                //arg1이 10이면 처음 검색에 대한 결과를 갖다 준걸로
                if(msg.arg1==10)
                {
                    productList.clear();
                    productList.addAll((List<Product>) msg.obj);
                    adapter.notifyDataSetChanged();
                }
//                arg2이 20이면 상품추가하기
                else if(msg.arg2==20){
                    String result = "";
                    List<Product> data = (List<Product>)msg.obj;
                    for(Product p : data)
                        result += p.getProductName() +"\n";
                    productList.addAll((List<Product>) msg.obj);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };
}

