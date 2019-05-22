package com.example.ds.final_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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

    String keyword; //키워드
    String Color; //색상
    Intent productInfoIntent;
    int ProductNum=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
        keywordEdt = (EditText)findViewById(R.id.main_keyword_edt);
        searchBtn = (Button) findViewById(R.id.main_search_btn);
        moreBtn = (Button) findViewById(R.id.main_more_btn);
        productList = new ArrayList<Product>();
        adapter = new ProductAdapter(this, R.layout.list_product_item, productList);
        GridView = (GridView) findViewById(R.id.main_GridView);
        GridView.setAdapter(adapter);
        Intent intent=getIntent();

        keyword=intent.getStringExtra("keyword");
        Color=intent.getStringExtra("Color");
       // keywordEdt.setText(Color+keyword);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle(keyword+"검색 결과");

        //keyword=intent.getStringExtra("원피스");
        keywordEdt.setText(keyword);

        service = new ProductSearchService(keyword);
        ProductSearchThread thread = new ProductSearchThread(service, handler);
        Toast.makeText(getApplicationContext(), "검색을 시작합니다.", Toast.LENGTH_LONG).show();
        thread.setColor(Color);
        thread.start();

// 상품검색
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                keyword = keywordEdt.getText().toString();
                service = new ProductSearchService(keyword);
                Color="아이보리"; //테스트
                ProductSearchThread thread = new ProductSearchThread(service, handler);
                Toast.makeText(getApplicationContext(), "검색을 시작합니다.", 0).show();
                thread.setColor(Color);
                thread.start();
            }
        });

        //클릭시, 상세정보 페이지로 이동
        GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Log.d(position+"정보",adapter.getInfo(position));
                productInfoIntent.putExtra("info", adapter.getInfo(position));
                productInfoIntent.putExtra("url", adapter.getUrl(position));
                Log.d("detailurl","상품검색:"+adapter.getUrl(position));
                productInfoIntent.putExtra("image", adapter.getImage(position));
                startActivity(productInfoIntent);
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
                thread.setColor(Color);
                thread.start();
            }
        });

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
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            List<Product> products;
            if(msg.what ==1 )
            {
                Product product;
                int error;
                //arg1이 10이면 처음 검색에 대한 결과를 갖다 준걸로
                if(msg.arg1==10)
                {
                    products=checkError(msg);
                    productList.clear();
                    if (products.size() <= 0) {
                        Toast.makeText(ShopActivity.this,"검색된 상품이 없습니다.",Toast.LENGTH_LONG).show();
                    } else if (products.size() < 4) {
                        productList.addAll(products.subList(0, products.size() - 1));
                    } else {
                        productList.addAll(products.subList(0, ProductNum));
                    }
                    if(productList.size()>0)
                        adapter.notifyDataSetChanged();
                }
//                arg2이 20이면 상품추가하기
                else if(msg.arg2==20){
                    String result = "";
                    List<Product> data = (List<Product>)msg.obj;
                    for(Product p : data)
                        result += p.getProductName() +"\n";
                    products=checkError(msg);
                    if (products.size() <= 0) {
                        Toast.makeText(ShopActivity.this,"더 보여드릴 상품이 없습니다.",Toast.LENGTH_LONG).show();
                    }
                    else if(products.size()<4){
                        productList.addAll(products.subList(0,products.size()-1));
                    }
                    else {
                        productList.addAll(products.subList(0, ProductNum));
                    }
                    if(productList.size()>0)
                        adapter.notifyDataSetChanged();
                }
            }
        }
    };
    public List<Product> checkError(Message msg){
        Product errProduct;
        int error;
        for(int i=0;i<((List<Product>) msg.obj).size();i++){
            errProduct=((List<Product>) msg.obj).get(i);
            error=errProduct.errorMessage(errProduct.getProductName(),errProduct.getOptionValueList());
            if (error==0){ //검색결과 없을때 삭제
                ((List<Product>) msg.obj).remove(i);
               // productList.remove(i);
                Log.i("삭제",i+"삭제");

            }
        }
        return (List<Product>) msg.obj;
    }
}

