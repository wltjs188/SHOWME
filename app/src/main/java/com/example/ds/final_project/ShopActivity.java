package com.example.ds.final_project;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Toast;


public class ShopActivity extends AppCompatActivity implements OnGestureListener {



    private Button moreBtn;
    private GridView gv;
    private ProductAdapter adapter;

    Intent productInfoIntent;
    String mJsonString;
    String IP_ADDRESS = "18.191.10.193";

    //상품정보 List
    ArrayList<String> productIds = new ArrayList<String>();
    ArrayList<String> optionNums = new ArrayList<String>();
    ArrayList<String> infos = new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> images = new ArrayList<String>(); //상품 옵션 대표 이미지
    ArrayList<String> adap_infos = new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> adap_images = new ArrayList<String>(); //상품 옵션 대표 이미지
    int page = 0;
    //검색 정보
    String category = null;
    String color = null;
    String length = null;
    String size = null;
    String pattern = null;
    String fabric = null;
    String detail = null;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector gDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfoIntent = new Intent(getApplicationContext(), ProductInfo.class);
        moreBtn = (Button) findViewById(R.id.main_more_btn);

        gDetector = new GestureDetector(this);

        adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
        gv = (GridView) findViewById(R.id.main_GridView);
        gv.setAdapter(adapter);
        Intent intent = getIntent();

        //검색정보 받아오기
        category = intent.getStringExtra("category").replaceAll("[\"]", "");
        color = intent.getStringExtra("color").replaceAll("[\"]", "");
        length = intent.getStringExtra("length").replaceAll("[\"]", "");
        size = intent.getStringExtra("size").replaceAll("[\"]", "");
        pattern = intent.getStringExtra("pattern").replaceAll("[\"]", "");
        fabric = intent.getStringExtra("fabric").replaceAll("[\"]", "");
        Log.d("category", category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle(category + "검색 결과");

        GetProduct task = new GetProduct();

        task.execute("http://" + IP_ADDRESS + "/getSearchedProduct.php", category, color, length, size, pattern, fabric, detail);

        //클릭시, 상세정보 페이지로 이동
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                productInfoIntent.putExtra("productId", productIds.get(page*4+position));
                productInfoIntent.putExtra("optionNum", optionNums.get(page*4+position));
                productInfoIntent.putExtra("info", infos.get(page*4+position));
                productInfoIntent.putExtra("image", images.get(page*4+position));
                startActivity(productInfoIntent);
            }
        });

        //더보기
        moreBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("카테고리 : " + category + "색상 : " + color + "기장 : " + length + "사이즈 : " + size + "패턴 : " + pattern + "재질 : " + fabric);

            }

        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gDetector.onTouchEvent(me);
    }

    public boolean onDown(MotionEvent e) {
     //   viewA.setText("-" + "DOWN" + "-");
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            Log.d("동작","되냐");
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //다음
             //Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                if(images.size()<=page*4+3){
                    Toast.makeText(getApplicationContext(), "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    page++;
                    if(images.size()<=page*4+3){
                        adap_images= new ArrayList<String>(images.subList(page*4,images.size()));
                        adap_infos=new ArrayList<String>(images.subList(page*4,infos.size()));
                    }else{
                        adap_images=new ArrayList<String>(images.subList(page*4,page*4+4));
                        adap_infos=new ArrayList<String>(infos.subList(page*4,page*4+4));
                    }
                    adapter = new ProductAdapter(this, R.layout.list_product_item, adap_images, adap_infos);
                    gv.setAdapter(adapter);
                }
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //이전
                //Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                if(page==0){
                    Toast.makeText(getApplicationContext(), "첫번째 페이지 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    page--;
                    if(images.size()<=page*4+3){
                        adap_images=new ArrayList<String>(images.subList(page*4,images.size()));
                        adap_infos=new ArrayList<String>(infos.subList(page*4,images.size()));
                    }else{
                        adap_images=new ArrayList<String>(images.subList(page*4,page*4+4));
                        adap_infos=new ArrayList<String>(infos.subList(page*4,page*4+4));
                    }
                    adapter = new ProductAdapter(this, R.layout.list_product_item, adap_images, adap_infos);
                    gv.setAdapter(adapter);
                }
            }
//            // down to up swipe
//            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();
//            }
//            // up to down swipe
//            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception e) {

        }
        return true;
    }

    public void onLongPress(MotionEvent e) {
        Toast mToast = Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT);
        mToast.show();
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
       // viewA.setText("-" + "SCROLL" + "-");
        return true;
    }

    public void onShowPress(MotionEvent e) {
        //viewA.setText("-" + "SHOW PRESS" + "-");
    }

    public boolean onSingleTapUp(MotionEvent e) {
        Toast mToast = Toast.makeText(getApplicationContext(), "Single Tap", Toast.LENGTH_SHORT);
        mToast.show();
        return true;
    }


    private class GetProduct extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ShopActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result == null) {

            } else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String category = params[1];
            String color = params[2];
            String length = params[3];
            String size = params[4];
            String pattern = params[5];
            String fabric = params[6];
            String detail = params[7];

            String serverURL = params[0];


            String postParameters = "category=" + category;
            if (color != "" && color != null && !color.equals("없음"))
                postParameters += "&color=" + color;
            if (length != "" && length != null && !length.equals("없음"))
                postParameters += "&length=" + length;
            if (size != "" && size != null && !size.equals("없음"))
                postParameters += "&size=" + size;
            if (pattern != "" && pattern != null && !pattern.equals("없음"))
                postParameters += "&pattern=" + pattern;
            if (fabric != "" && fabric != null && !fabric.equals("없음")) {
                Log.d("fabric:", fabric);
                postParameters += "&fabric=" + fabric;
            }
            if (detail != "" && detail != null && !detail.equals("없음")) {
                Log.d("detail:", "엥");
                postParameters += "&detail=" + detail;
            }
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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
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

    private void showResult() {

        String TAG_JSON = "SearchedProduct";
        try {

            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray", jsonArray.length() + "");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Log.d("상품", "?");
                productIds.add(item.getString("productId"));
                optionNums.add(item.getString("optionNum"));
                String info = item.getString("name") + "\n"
                        + item.getString("category") + "\n"
                        + item.getString("length") + "\n"
                        + item.getString("price") + "\n"
                        + item.getString("size") + "\n"
                        + item.getString("color") + "\n"
                        + item.getString("fabric") + "\n"
                        + item.getString("pattern") + "\n"
                        + item.getString("detail");
                infos.add(info);
                images.add(item.getString("image"));
                Log.d("가져온 상품:", infos.get(i));

            }
            if(images.size()<=4) {
                adapter = new ProductAdapter(this, R.layout.list_product_item, images, infos);
                gv.setAdapter(adapter);
            }else{
                adap_images=new ArrayList<String>(images.subList(0,4));
                adap_infos=new ArrayList<String>(infos.subList(0,4));
                adapter = new ProductAdapter(this, R.layout.list_product_item, adap_images, adap_infos);
                gv.setAdapter(adapter);
            }

        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
            Log.d("phptest: ", mJsonString);
            Log.d("상품", "오류");
        }

    }

    public Bitmap toBitmap(String imgurl) {
        URL url;
        Bitmap imgBitmap = null;
        try {
            url = new URL(imgurl);
            URLConnection conn = url.openConnection();
            conn.connect();
            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgBitmap;
    }

    // 값 저장하기
    private void savePreferences(String key, String s) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기버튼 실행
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                ((MainActivity) MainActivity.CONTEXT).onResume();
                finish();
                return true;
            }
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
