package com.example.ds.final_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity {
    Intent productInfoIntent; //상세정보 intent
    GridView gv;
    int index=0;
    String mJsonString;
    String IP_ADDRESS = "18.191.10.193";
    private WishAdapter adapter;
    String uuid="";
    //상품정보 List
    ArrayList<String> productIds=new ArrayList<String>();
    ArrayList<String> optionNums=new ArrayList<String>();
    ArrayList<String> infos=new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> images=new ArrayList<String>(); //상품 옵션 대표 이미지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle("관심상품");

        productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
        gv = (GridView)findViewById(R.id.gridView1);

        uuid = getPreferences("uuid");

        GetWishProduct task = new GetWishProduct();
        task.execute( "http://" + IP_ADDRESS + "/getWishProduct.php",uuid);

        adapter = new WishAdapter(this, R.layout.activity_wish_list, images,infos,index);
        gv.setAdapter(adapter);


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
               // productInfoIntent.putExtra("info", infos.get(position));
                productInfoIntent.putExtra("productId", productIds.get(position));
                productInfoIntent.putExtra("optionNum", optionNums.get(position));
                productInfoIntent.putExtra("info", infos.get(position));
                productInfoIntent.putExtra("image", images.get(position));
                startActivity(productInfoIntent);
            }
        });
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

private class GetWishProduct extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;
    String errorString = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(WishListActivity.this,
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
        String serverURL = params[0];
        String postParameters = "uid=" + params[1];
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
        String TAG_JSON="WishProduct";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray",jsonArray.length()+"");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String uuid = item.getString("uid");

                if(uuid.equals(this.uuid)){
                    Log.d("가져온 data",item.getString("productId"));
                    Log.d("가져온 data",item.getString("optionNum"));
                    productIds.add(item.getString("productId"));
                    optionNums.add(item.getString("optionNum"));
                    infos.add(item.getString("info")) ;
                    images.add(item.getString("image"));

                    //adapter 설정
                    adapter = new WishAdapter(this, R.layout.activity_wish_list, images,infos,index);
                    gv.setAdapter(adapter);
                }
            }


        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
            Log.d("phptest: ",mJsonString);
        }

    }

    // 값 불러오기
    private String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }
}

class WishAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;

    ArrayList<String> images;
    ArrayList<String> infos;
    int index;
    public WishAdapter(Context context, int resource, ArrayList<String> images,ArrayList<String> infos,int index) {
        super(context, resource,images);
        this.context = context;
        this.resource = resource;
        this.images = images;
        this.infos=infos;
        this.index=index;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
        Glide.with(WishAdapter.super.getContext()).load(images.get(position)).into(holder.imageView);
        return convertView;
    }
    public String getInfo(int i){
        return infos.get(i);
    }
    static class ProductViewHolder{
        public ImageView imageView;
    }
}

