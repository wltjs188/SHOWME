package com.example.ds.final_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.ds.final_project.db.DTO.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Toast;


public class ShopActivity extends AppCompatActivity {


    private GridView gv;
    private ProductAdapter adapter;

    Intent productInfoIntent;

    //상품정보 List
    Product searchedProduct=null;
    ArrayList<Product> products=new ArrayList<Product>();
    ArrayList<String> infos = new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> summary = new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> images = new ArrayList<String>(); //상품 옵션 대표 이미지
    ArrayList<String> ids = new ArrayList<String>();
    ArrayList<String> sizes = new ArrayList<String>();
    ArrayList<String> sizeTables = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfoIntent = new Intent(getApplicationContext(), ProductInfo.class);
        gv=(GridView) findViewById(R.id.main_GridView);
        adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
        gv.setAdapter(adapter);


        //검색정보 받아오기
        Gson gson = new GsonBuilder().create();
        String strContact=getPreferences("remember");
        searchedProduct=gson.fromJson(strContact,Product.class);
        Log.d("검색한 상품 옵션:",searchedProduct.toString());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle(searchedProduct.getCategory() + "검색 결과");

        SearchProduct task ;
        if(searchedProduct.getStyle()==null){
           task = new SearchProduct();
            task.execute("SearchOne", searchedProduct.getCategory());
            Log.i("shop","shoptask1");
        }else if(searchedProduct.getColor()==null){
            task = new SearchProduct();
            task.execute("SearchTwo", searchedProduct.getCategory(), searchedProduct.getStyle());
            Log.i("shop","shoptask2");
        }else{
            task = new SearchProduct();
            task.execute("SearchThree", searchedProduct.getCategory(),searchedProduct.getStyle(),searchedProduct.getColor());
            Log.i("shop","shoptask3");
        }
        //클릭시, 상세정보 페이지로 이동
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position",position+"");
//                Gson gson = new GsonBuilder().create();

//                String strContact = gson.toJson(products.get(position), Product.class);

//                productInfoIntent.putExtra("product", strContact);
                productInfoIntent.putExtra("image",images.get(position));
                productInfoIntent.putExtra("productId",ids.get(position));
                productInfoIntent.putExtra("info",infos.get(position));
                productInfoIntent.putExtra("size",sizes.get(position));
                productInfoIntent.putExtra("sizeTable",sizeTables.get(position));
                startActivity(productInfoIntent);
            }
        });

    }
    // 값 불러오기
    public String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

private class SearchProduct extends AsyncTask<String, Void,String> {
    String LoadData;
    private ProgressDialog pDialog;
    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(ShopActivity.this);
        pDialog.setMessage("검색중입니다..");
        pDialog.setCancelable(false);
        pDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        String style=null;
        String color=null;

        String project = (String) params[0];
        String category = (String) params[1];
        if(params.length==3){
            style = (String) params[2];
        }else if(params.length==4){
            style = (String) params[2];
            color = (String) params[3];
        }




        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);

            HttpClient client = new DefaultHttpClient(httpParameters);

            HttpConnectionParams.setConnectionTimeout(httpParameters, 7000);
            HttpConnectionParams.setSoTimeout(httpParameters, 7000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);

            // 주소 : aws서버
            String postURL = "http://52.78.143.125:8080/showme/";

            // 로컬서버
//            String postURL = "http://10.0.2.2:8080/showme/InsertUser";

            HttpPost post = new HttpPost(postURL + project);
            //서버에 보낼 파라미터
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            //파라미터 추가하기
            if(project.equals("SearchOne")){
                Log.d("검색",category);
                postParameters.add(new BasicNameValuePair("category", category));
            }else if(project.equals("SearchTwo")){
                Log.d("검색",category+","+style);
                postParameters.add(new BasicNameValuePair("category", category));
                postParameters.add(new BasicNameValuePair("style", style));
            }else if(project.equals("SearchThree")){
                Log.d("검색",category+", "+style+", "+color);
                postParameters.add(new BasicNameValuePair("category", category));
                postParameters.add(new BasicNameValuePair("style", style));
                postParameters.add(new BasicNameValuePair("color", color));
            }


            //파라미터 보내기
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postParameters, HTTP.UTF_8);
            post.setEntity(ent);

            long startTime = System.currentTimeMillis();

            HttpResponse responsePOST = client.execute(post);

            long elapsedTime = System.currentTimeMillis() - startTime;
            Log.v("debugging", elapsedTime + " ");


            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
//                ToastMessage("로그인 성공");

                LoadData = EntityUtils.toString(resEntity, HTTP.UTF_8);
                Log.i("shop가져온 데이터", LoadData);
                return LoadData;

            }
            if (responsePOST.getStatusLine().getStatusCode() == 200) {
                Log.d("search","오류없음");

            } else {
                Log.d("search","오류있음");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        pDialog.dismiss();
        if (result == null){
            Log.i("로긴","실패");
//            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
        }
        else {
            try {
                JSONObject jsonObj = new JSONObject(LoadData);
                // json객체.get("변수명")
                JSONArray jArray = (JSONArray) jsonObj.get("getData");
//                    items = new ArrayList<Product>();
                if(jArray.length()==0){
                    Log.d("검색"," 실패");
                    Toast.makeText(getApplicationContext(),"검색된 상품이 없습니다.",Toast.LENGTH_LONG).show();


                }else {
//                    Log.i("검색","성공"+result);
                    Product product=new Product();
                    for (int i = 0; i < jArray.length(); i++) {
                        // json배열.getJSONObject(인덱스)
                        JSONObject row = jArray.getJSONObject(i);
                        String name=row.getString("NAME");
                        String category=row.getString("CATEGORY");
                        int id=row.getInt("ID");
                        String image = row.getString("IMAGE");
                        String style=row.getString("STYLE");
                        String color=row.getString("COLOR");
                        String size=row.getString("SIZE");
                        int price=row.getInt("PRICE");
                        String sizeTable=row.getString("SIZE_TABLE");

                        product.setName(name);
                        product.setId(id);
                        product.setCategory(category);
                        product.setColor(color);
                        product.setSize(size);
                        product.setImage(image);
                        product.setStyle(style);
                        product.setPrice(price);
                        product.setSize_table(sizeTable);
                        products.add(product);
                        infos.add(product.toString());
                        summary.add(product.getSummary());
                        images.add(product.getImage());
                        ids.add(product.getId()+"");
                        sizes.add(product.getSize());
                        sizeTables.add(product.getSize_table());
                        Log.i("shop가져온 데이터><", product.toString());

                    }
//                    adapter.notifyDataSetChanged();
                    adapter = new ProductAdapter(ShopActivity.this, R.layout.list_product_item, images, summary);
                    gv.setAdapter(adapter);

                }

            } catch (JSONException e) {
                Log.d("검색 오류 : ", e.getMessage());
            }

        }
    }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기 버튼 실행
        Intent homeIntent=new Intent(this,ChatbotActivity.class);
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
//                startActivity(homeIntent);
                finish();
                return true;
            }
            case R.id.showme:
                startActivity(homeIntent);
                finish();
                return true;
            case R.id.help:
                AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                oDialog.setTitle("도움말")
                        .setMessage("")
                        .setPositiveButton("닫기", null)
                        .setCancelable(true)
                        .show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

class ProductAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;

    ArrayList<String> images;
    ArrayList<String> summarys;

    public ProductAdapter(Context context, int resource, ArrayList<String> images,ArrayList<String> summarys) {
        super(context, resource,images);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.resource = resource;
        this.images = images;
        this.summarys=summarys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ProductViewHolder holder;
        if(convertView == null){
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.wish_item, parent, false);
            holder = new ProductViewHolder();
            holder.imageView
                    = (ImageView) convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);
            holder.imageView.setContentDescription(getInfo(position));
        }
        else{
            holder = (ProductViewHolder) convertView.getTag();
        }
        Glide.with(ProductAdapter.super.getContext()).load(images.get(position)).into(holder.imageView);
        return convertView;
    }

    public String getInfo(int i){
        return summarys.get(i).toString();
    }
    class ProductViewHolder{
        public ImageView imageView;
    }
}
