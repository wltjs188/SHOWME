package com.example.ds.final_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WishListActivity extends AppCompatActivity {

    GridView gv;
    int index=0;
    //나의관심상품
    Intent itemInfoIntent; //상품정보
    String mJsonString;
    String IP_ADDRESS = "35.243.72.245";
    private WishAdapter adapter;
    String uuid;
    //String productURL;
    ArrayList<String> productURLs=new ArrayList<String>();
    //String info;
    ArrayList<String> infos=new ArrayList<String>();
 //   String image;
    ArrayList<String> images=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼
        getSupportActionBar().setTitle("관심상품");
        itemInfoIntent = new Intent(getApplicationContext(),ItemInfoActivity.class);
//        int img[] = { //이미지배열
//            R.drawable.test_img1,R.drawable.test_img2,R.drawable.test_img3,R.drawable.test_img4,R.drawable.test_img5,R.drawable.test_img6
//        };

        uuid = getPreferences("uuid");
        //상품들 가져오기
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getWishList.php",uuid);
       // adapter = new WishAdapter(this, R.layout.activity_wish_list, images,index);
        gv = (GridView)findViewById(R.id.gridView1);
        //gv.setAdapter(adapter);
//        // 그리드뷰 어댑터 생성
//        final MyAdapter adapter = new MyAdapter (
//                getApplicationContext(),
//                R.layout.wish_item,       // GridView 항목의 레이아웃 wish_item.xml
//                img);
//        GridView gv = (GridView)findViewById(R.id.gridView1);
//        gv.setAdapter(adapter);
//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//                int img=adapter.getImg(position);
//                itemInfoIntent.putExtra("img",img);
//                startActivity(itemInfoIntent);
//            }
//        });

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
    private class GetData extends AsyncTask<String, Void, String> {

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
            String postParameters = "uuid=" + params[1];

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
        String TAG_JSON="wishList";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray",jsonArray.length()+"");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String uuid = item.getString("uuid");

                if(uuid.equals(this.uuid)){
                    Log.d("uuid같음",item.getString("productURL"));
                    productURLs.add(item.getString("productURL"));
                    infos.add(item.getString("info")) ;
                    images.add(item.getString("image"));
                  //adapter.notifyDataSetChanged();
                }
            }
            adapter = new WishAdapter(this, R.layout.activity_wish_list, images,index);
            gv.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
        }

        if(images.size()>0)
        {  for(int i=0;i<images.size();i++){
            Log.d("관심",productURLs.get(i)+", "+infos.get(i)+", "+images.get(i));
        }}
    }
    // 값 불러오기
    private String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }
}

//그리드뷰 어댑터
//class MyAdapter extends BaseAdapter {
//    Context context;
//    int layout;
//    int img[];
//    LayoutInflater inf;
//
//    public MyAdapter(Context context, int layout, int[] img) {
//        this.context = context;
//        this.layout = layout;
//        this.img = img;
//        inf = (LayoutInflater) context.getSystemService
//                (Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return img.length;
//    }
//
//    public int getImg(int position){
//        return img[position];
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return img[position];
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView==null)
//            convertView = inf.inflate(layout, null);
//        ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
//        iv.setImageResource(img[position]);
//
//        return convertView;
//    }
//
//}

class WishAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
   // int i=0;
  //  private List<Product> productList;
    private ImageLoader imageLoader;
    ArrayList<String> images;
    int index;
    public WishAdapter(Context context, int resource, ArrayList<String> images,int index) {
        super(context, resource,images);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.resource = resource;
        this.images = images;
        imageLoader= new ImageLoader(context);
        this.index=index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
//        return super.getView(position, convertView, parent);

        //getView메소드는 리스트뷰의 한줄의 아이템을 그리기 위한 뷰를 만들어내는 함수이고
        //한줄의 아이템에 대해서 UI를 인플레이션하고 그 객체를 리턴하면됨

        ProductViewHolder holder;
        if(convertView == null){
            //이미 인플레이션 한 뷰가 있다면 매개변수 convertView에 들어와 재사용 가능하므로
            //convertView가 null일때만 인플레이션 해줌
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.wish_item, parent, false);
            holder = new ProductViewHolder();
            holder.imageView
                    = (ImageView) convertView.findViewById(R.id.imageView1);
//            holder.product_Info
//                    =(TextView)convertView.findViewById(R.id.product_Info);
            //처음 인플레이션 될 때 홀더 객체를 만들어서
            //홀더 셋트의 위젯 참조변수들이 findViewById

            //holder객체는 각 위젯들이 findViewById한 결과들 집합
            convertView.setTag(holder);


        }
        else{
            holder = (ProductViewHolder) convertView.getTag();

        }
        Log.d("codbs","여기실행안되냐?");
        Glide.with(WishAdapter.super.getContext()).load(images.get(position)).into(holder.imageView);
     //   new ImageDownLoader(holder.imageView,position).execute();
        return convertView;

    }
    class ImageDownLoader extends AsyncTask<String, Void, Bitmap>
    {
        ImageView imageView;
        int position;
     //   int i=0;
        public ImageDownLoader(ImageView imageView,int position){
            this.imageView = imageView;
            this.position=position;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Bitmap bitmap = null;
            try {
               // URL url = new URL(params[0]);
                Log.d("codbs",images.get(position));

                URL url = new URL(images.get(position));

                BufferedInputStream bi = new BufferedInputStream(url.openStream());
                bitmap = BitmapFactory.decodeStream(bi);
                bi.close();
              //  }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result != null)
                imageView.setImageBitmap(result);
        }

    }

    static class ProductViewHolder{
        public ImageView imageView;
     //   public TextView product_Info;

    }
}

