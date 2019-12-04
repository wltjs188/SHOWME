package com.example.ds.final_project;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    ArrayList<Product> products=new ArrayList<Product>();
//    ArrayList<Integer> productIds = new ArrayList<Integer>();
//    ArrayList<String> optionNums = new ArrayList<String>();
    ArrayList<String> infos = new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> images = new ArrayList<String>(); //상품 옵션 대표 이미지
    int page = 0;
    //검색 정보
    String category = null;
    String color = null;
    String length = null;
    String size = null;
    String pattern = null;
    String detail = null;

    private GestureDetector gDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfoIntent = new Intent(getApplicationContext(), ProductInfo.class);
        gv=(GridView) findViewById(R.id.main_GridView);
        adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
        gv.setAdapter(adapter);



        Intent intent = getIntent();

        //검색정보 받아오기
        category = intent.getStringExtra("category").replaceAll("[\"]", "");
        color = intent.getStringExtra("color").replaceAll("[\"]", "");
        size = intent.getStringExtra("size").replaceAll("[\"]", "");
        Log.d("category", category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle(category + "검색 결과");

        GetProduct task = new GetProduct();
        task.execute("GetProduct", category, color, size);
//        GetDiscountInfo task2 =new GetDiscountInfo();
//        task2.execute("http://" + IP_ADDRESS + "/getDiscountInfo.php", category, color, length, size, pattern, detail);
        //클릭시, 상세정보 페이지로 이동
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new GsonBuilder().create();

                String strContact = gson.toJson(products.get(position), Product.class);

                productInfoIntent.putExtra("product", strContact);
                startActivity(productInfoIntent);
            }
        });

    }


//    private class GetDiscountInfo extends AsyncTask<String, Void, String> {
//
//        ProgressDialog progressDialog;
//        String errorString = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(ShopActivity.this,
//                    "Please Wait", null, true, true);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//
//            if (result == null) {
//
//            } else {
//                mJsonString = result;
//                showDIResult();
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String category = params[1];
//            String color = params[2];
//            String length = params[3];
//            String size = params[4];
//            String pattern = params[5];
//       //     String fabric = params[6];
//            String detail = params[6];
//        //    String category_detail = params[7];
//            String serverURL = params[0];
//
//
//            String postParameters = "category=" + category;
//            if (color != "" && color != null && !color.equals("없음"))
//                postParameters += "&color=" + color;
//            if (length != "" && length != null && !length.equals("없음"))
//                postParameters += "&length=" + length;
//            if (size != "" && size != null && !size.equals("없음"))
//                postParameters += "&size=" + size;
//            if (pattern != "" && pattern != null && !pattern.equals("없음"))
//                postParameters += "&pattern=" + pattern;
////            if (fabric != "" && fabric != null && !fabric.equals("없음")) {
////                Log.d("fabric:", fabric);
////                postParameters += "&fabric=" + fabric;
////            }
//            if (detail != "" && detail != null && !detail.equals("없음")) {
//                postParameters += "&detail=" + detail;
//            }
//            try {
//
//                URL url = new URL(serverURL);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//
//                httpURLConnection.setReadTimeout(5000);
//                httpURLConnection.setConnectTimeout(5000);
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();
//
//                int responseStatusCode = httpURLConnection.getResponseCode();
//                InputStream inputStream;
//                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    inputStream = httpURLConnection.getInputStream();
//                } else {
//                    inputStream = httpURLConnection.getErrorStream();
//                }
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuilder sb = new StringBuilder();
//                String line;
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    sb.append(line);
//                }
//                bufferedReader.close();
//                return sb.toString().trim();
//            } catch (Exception e) {
//                errorString = e.toString();
//                return null;
//            }
//        }
//    }
//    private void showDIResult() {
//
//        String TAG_JSON = "SearchedProduct";
//        try {
//
//            JSONObject jsonObject = new JSONObject(mJsonString);
//            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
//            Log.d("jsonArray", jsonArray.length() + "");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject item = jsonArray.getJSONObject(i);
//                Log.d("상품", "?");
//                productIds.add(item.getString("productId"));
//                optionNums.add(item.getString("optionNum"));
//                String info="";
//                info+=item.getString("name").equals("null")?"":("상품명: " +item.getString("name")+ "\n");
//
//                info+=item.getString("length").equals("null")?"":item.getString("length")+" ";
//                info+=item.getString("category").equals("null")?"":item.getString("category");
//                info+=item.getString("category_detail").equals("null")?"\n":" > "+item.getString("category_detail")+ "\n";
//                info+=item.getString("price").equals("null")?"":"가격: " +item.getString("price")+"\n";
//                info+=item.getString("size").equals("null")?"":"사이즈: " +item.getString("size")+"\n";
//                info+=item.getString("color").equals("null")?"":"색상: " +item.getString("color")+"\n";
//                info+=item.getString("pattern").equals("null")?"":"패턴: " +item.getString("pattern")+"\n";
////                info+=item.getString("fabric").equals("null")?"":"재질: " +item.getString("fabric")+"\n";
//                info+=item.getString("detail").equals("null")?"":"기타: " +item.getString("detail");
//                infos.add(info);
//                images.add(item.getString("image"));
//                Log.d("가져온 상품:", infos.get(i));
//
//            }
//            if(images.size()<=4) {
//                adap_images=images;
//                adap_infos=infos;
//             //   Log.d("하이",adap_images.toString());
////                adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
////                gv.setAdapter(adapter);
//                //adapter.notifyDataSetChanged();
//            }else{
//                adap_images=new ArrayList<String>(images.subList(0,4));
//                adap_infos=new ArrayList<String>(infos.subList(0,4));
//
//            }
//            adapter = new ProductAdapter(this, R.layout.list_product_item, adap_images, adap_infos);
//            gv.setAdapter(adapter);
//            //  adapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            Log.d("showResult : ", e.getMessage());
//            Log.d("phptest: ", mJsonString);
//            Log.d("상품", "오류");
//            Toast.makeText(ShopActivity.this,"검색된 상품이 없습니다.",Toast.LENGTH_LONG).show();
//        }
//
//    }
class GetProduct extends AsyncTask<String, Void,String> {
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
        String project = (String) params[0];
        String category = (String) params[1];
        String color = (String) params[2];
        String size = (String) params[3];

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
            postParameters.add(new BasicNameValuePair("category", category));
            postParameters.add(new BasicNameValuePair("color", color));
            postParameters.add(new BasicNameValuePair("size", size));
//            for(int i=0;i<params.length;i++){
//                postParameters.add(new BasicNameValuePair("\""+(String)params[i]+"\"",(String)params[i]));
//                System.out.println("\""+(String)params[i]+"\","+(String)params[i]);
//            }
//            postParameters.add(new BasicNameValuePair("uid", "0"));
//            postParameters.add(new BasicNameValuePair("wishProductName", "1"));
//            postParameters.add(new BasicNameValuePair("value", "123"));


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
                Log.i("가져온 데이터", LoadData);
                return LoadData;

            }
            if (responsePOST.getStatusLine().getStatusCode() == 200) {
                System.out.println("오류없음");

            } else {
                System.out.println("오류");
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
                    Log.d("로긴","로그인 실패");
                }else {
                    Log.i("로긴","성공"+result);
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

                        product.setName(name);
                        product.setId(id);
                        product.setCategory(category);
                        product.setColor(color);
                        product.setSize(size);
                        product.setImage(image);
                        product.setStyle(style);
                        product.setPrice(price);

                        products.add(product);

                        Log.i("가져온 데이터", name);
//                        productIds.add(product.getId());
                        infos.add(product.toString());
                        images.add(image);
                    }

                }

            } catch (JSONException e) {
                Log.d("showResult : ", e.getMessage());
            }

        }
    }
}
//    private class GetProduct extends AsyncTask<String, Void, String> {
//
//        ProgressDialog progressDialog;
//        String errorString = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(ShopActivity.this,
//                    "Please Wait", null, true, true);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//
//            if (result == null) {
//
//            } else {
//                mJsonString = result;
//                showResult();
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String category = params[1];
//            String color = params[2];
//            String length = params[3];
//            String size = params[4];
//            String pattern = params[5];
//            //     String fabric = params[6];
//            String detail = params[6];
//            //    String category_detail = params[7];
//            String serverURL = params[0];
//
//
//            String postParameters = "category=" + category;
//            if (color != "" && color != null && !color.equals("없음"))
//                postParameters += "&color=" + color;
//            if (length != "" && length != null && !length.equals("없음"))
//                postParameters += "&length=" + length;
//            if (size != "" && size != null && !size.equals("없음"))
//                postParameters += "&size=" + size;
//            if (pattern != "" && pattern != null && !pattern.equals("없음"))
//                postParameters += "&pattern=" + pattern;
////            if (fabric != "" && fabric != null && !fabric.equals("없음")) {
////                Log.d("fabric:", fabric);
////                postParameters += "&fabric=" + fabric;
////            }
//            if (detail != "" && detail != null && !detail.equals("없음")) {
//                postParameters += "&detail=" + detail;
//            }
//            try {
//
//                URL url = new URL(serverURL);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//
//                httpURLConnection.setReadTimeout(5000);
//                httpURLConnection.setConnectTimeout(5000);
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();
//
//                int responseStatusCode = httpURLConnection.getResponseCode();
//                InputStream inputStream;
//                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    inputStream = httpURLConnection.getInputStream();
//                } else {
//                    inputStream = httpURLConnection.getErrorStream();
//                }
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuilder sb = new StringBuilder();
//                String line;
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    sb.append(line);
//                }
//                bufferedReader.close();
//                return sb.toString().trim();
//            } catch (Exception e) {
//                errorString = e.toString();
//                return null;
//            }
//        }
//    }
//private void showResult() {
//
//        String TAG_JSON = "SearchedProduct";
//        try {
//
//            JSONObject jsonObject = new JSONObject(mJsonString);
//            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
//            Log.d("jsonArray", jsonArray.length() + "");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject item = jsonArray.getJSONObject(i);
//                Log.d("상품", "?");
//                productIds.add(item.getString("productId"));
//                optionNums.add(item.getString("optionNum"));
//                String info="";
//
////                info+=(i+1)+"번째 상품입니다.\n";
//                info+=item.getString("name").equals("null")?"":("상품명: " +item.getString("name")+ "\n");
//
//                info+=item.getString("length").equals("null")?"":item.getString("length")+" ";
//                info+=item.getString("category").equals("null")?"":item.getString("category");
//                info+=item.getString("category_detail").equals("null")?"\n":" > "+item.getString("category_detail")+ "\n";
//                info+=item.getString("price").equals("null")?"":"가격: " +item.getString("price")+"\n";
//                info+=item.getString("size").equals("null")?"":"사이즈: " +item.getString("size")+"\n";
//                info+=item.getString("color").equals("null")?"":"색상: " +item.getString("color")+"\n";
//                info+=item.getString("pattern").equals("null")?"":"패턴: " +item.getString("pattern")+"\n";
////                info+=item.getString("fabric").equals("null")?"":"재질: " +item.getString("fabric")+"\n";
//                info+=item.getString("detail").equals("null")?"":"기타: " +item.getString("detail")+"\n";
//
//                String discount="";
//                discount+=item.getString("shipping").equals("null")?"":"배송정보: " +item.getString("shipping")+"\n";
//                discount+=item.getString("point").equals("null")?"":"포인트 적립: " +item.getString("point")+"\n";
//                discount+=item.getString("interestFree").equals("null")?"":"무이자 개월: " +item.getString("interestFree")+"\n";
//                if(discount!=""){
//                    info+="<할인정보>\n"+discount;
//                }else{
//                    Log.d("할인 없어","채윤");
//                }
//                infos.add(info);
//                String img=item.getString("image");
//                img=img.replaceFirst("90x90","170x170");
//                images.add(img);
//
//                Log.d("가져온 상품:", img);
//
//            }
////            if(images.size()<=4) {
////                adap_images=images;
////                adap_infos=infos;
////                //   Log.d("하이",adap_images.toString());
//////                adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
//////                gv.setAdapter(adapter);
////                //adapter.notifyDataSetChanged();
////            }else{
////                adap_images=new ArrayList<String>(images.subList(0,4));
////                adap_infos=new ArrayList<String>(infos.subList(0,4));
////
////            }
////            adapter = new ProductAdapter(this, R.layout.list_product_item, adap_images, adap_infos);
//            adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
//            gv.setAdapter(adapter);
//            //  adapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            Log.d("showResult : ", e.getMessage());
//            Log.d("phptest: ", mJsonString);
//            Log.d("상품", "오류");
//            Toast.makeText(ShopActivity.this,"검색된 상품이 없습니다.",Toast.LENGTH_LONG).show();
//        }
//
//    }

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

}

class ProductAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;

    ArrayList<String> images;
    ArrayList<String> infos;

    public ProductAdapter(Context context, int resource, ArrayList<String> images,ArrayList<String> infos) {
        super(context, resource,images);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.resource = resource;
        this.images = images;
        this.infos=infos;
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
        return infos.get(i).toString();
    }
    class ProductViewHolder{
        public ImageView imageView;
    }
}
