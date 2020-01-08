package com.example.ds.final_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ds.final_project.db.DTO.WishProduct;

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

public class WishListActivity extends AppCompatActivity {

    WishProductDialog wishProductDialog;
    Intent productInfoIntent; //상세정보 intent
    GridView gv;
    private WishAdapter adapter;
    String uuid="";


    //STT
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //상품정보 List
    ArrayList<WishProduct> wishProducts=new ArrayList<WishProduct>();
    ArrayList<String> productIds=new ArrayList<String>();
    ArrayList<String> optionNums=new ArrayList<String>();
    ArrayList<String> infos=new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> images=new ArrayList<String>(); //상품 옵션 대표 이미지
    ArrayList<String> aliases=new ArrayList<String>(); //네이밍
    ArrayList<String> sizes=new ArrayList<String>();
    ArrayList<String> sizeTables=new ArrayList<String>();
    ArrayList<String> product_names=new ArrayList<String>();
    int pos;
    String alias;
    String size;
    String sizeTable;
    String product_name;
    String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle("관심상품");

        productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
        gv = (GridView)findViewById(R.id.gridView1);

        wishProductDialog=new WishProductDialog(this);

        uuid = getPreferences("uuid");

        GetWishProduct task = new GetWishProduct();
        task.execute( "GetWishProduct",uuid);

        adapter = new WishAdapter(this, R.layout.activity_wish_list, images,infos,aliases);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
                String productId=productIds.get(position);
                String info=infos.get(position);
                String image=images.get(position);
                alias=aliases.get(position);
                size=sizes.get(position);
                sizeTable=sizeTables.get(position);
               // productInfoIntent.putExtra("info", infos.get(position));
                PopupMenu popup= new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미

                getMenuInflater().inflate(R.menu.option_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.showProductInfo:
//                                Toast.makeText(getApplication(),"메뉴1",Toast.LENGTH_SHORT).show();
                                productInfoIntent.putExtra("productId", productId);
                                productInfoIntent.putExtra("info", info);
                                productInfoIntent.putExtra("image", image);
                                productInfoIntent.putExtra("alias",alias);
                                productInfoIntent.putExtra("size",size);
                                productInfoIntent.putExtra("sizeTable",sizeTable);
                                productInfoIntent.putExtra("name",product_name);
                                Log.d("챈",aliases.get(position));
                                startActivity(productInfoIntent);
                                break;
                            case R.id.editWishProductName:
                                //별칭 수정

                                wishProductDialog.setDialogListener(new DialogListener() {
                                    @Override
                                    public void onPositiveClicked(String name) {
//
                                        UpdateWishProductAlias task1 = new UpdateWishProductAlias(); //사용자정보 수정
                                        task1.execute("UpdateWishProductAlias",uuid,alias,name);
                                        Name=name;
                                        pos=position;
                                    }

                                    @Override
                                    public void onNegativeClicked() {
                                        Log.d("dialog","취소");
                                    }
                                });
                                wishProductDialog.show();
                                wishProductDialog.setDialog_tv("수정할 이름을 입력해 주세요");
//                                dialog.setEditTextHint("뀨");
//                                dialog.setEditText(adapter.getName(position));
//                                Toast.makeText(getApplication(),"메뉴1",Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });

                popup.show();//Popup Menu 보이기


            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("destroy","종료");
        setResult(RESULT_OK);
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
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                startActivity(homeIntent);
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
                        .setMessage("관심상품으로 등록된 상품 목록을 보여줍니다.\n" +
                                "상품을 누르면 자세한 상품 상세 정보 보기와 별칭 수정 두가지 메뉴를 선택할 수 있습니다.\n" +
                                "상품 상세정보 보기를 선택하면 자세한 상품의 정보가 보이는 화면으로 이동합니다.\n" +
                                "별칭 수정을 선택하면 새로운 음성 또는 자판을 이용해 상품을 별칭을 입력할 수 있습니다. \n" +
                                "별칭을 입력하고 확인을 누르면 별칭이 수정됩니다.")
                        .setPositiveButton("닫기", null)
                        .setCancelable(true)
                        .show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //관심상품 가져오기
    private class GetWishProduct extends AsyncTask<String, Void,String> {
        String LoadData;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String project = (String)params[0];
            String uid = (String)params[1];
            for(String p:params){
                Log.d("hi",p);
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

                HttpPost post = new HttpPost(postURL+project);
                //서버에 보낼 파라미터
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                //파라미터 추가하기

                postParameters.add(new BasicNameValuePair("uid", uid));

                //파라미터 보내기
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postParameters, HTTP.UTF_8);
                post.setEntity(ent);

                long startTime = System.currentTimeMillis();

                HttpResponse responsePOST = client.execute(post);

                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.v("debugging", elapsedTime + " ");


                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    LoadData = EntityUtils.toString(resEntity, HTTP.UTF_8);

                    Log.d("가져온 데이터", LoadData);
                    return LoadData;
                }
                if(responsePOST.getStatusLine().getStatusCode()==200){
                    Log.d("오류없음","굳");
                }
                else{
                    Log.d("error","오류");
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

//            pDialog.dismiss();
            if (result == null||result==""){
                Log.d("wishList","없  ");
            }
            else {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    // json객체.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("getData");
//                    items = new ArrayList<Product>();
                    if(jArray.length()==0){
                        Log.d("wishList","");
                    }else {
                        Log.d("wishList","맞음");
                        WishProduct wishProduct=new WishProduct();
                        for (int i = 0; i < jArray.length(); i++) {
                            // json배열.getJSONObject(인덱스)
                            JSONObject row = jArray.getJSONObject(i);
                            String uid=row.getString("UID");
                            String alias=row.getString("ALIAS");
                            String id=row.getString("ID");
                            String image=row.getString("IMAGE");
                            String info=row.getString("INFO");
                            String size =row.getString("SIZE");
                            String sizeTable=row.getString("SIZE_TABLE");
                            String name=row.getString("NAME");

                            wishProduct.setUid(uid);
                            wishProduct.setId(id);
                            wishProduct.setAlias(alias);
                            wishProduct.setImage(image);
                            wishProduct.setInfo(info);
                            wishProduct.setSize(size);
                            wishProduct.setSizeTable(sizeTable);
                            wishProduct.setName(name);

                            Log.d("가져온 데이터",wishProduct.getInfo());
                            productIds.add(id);
                            images.add(image);
                            infos.add(info);
                            aliases.add(alias);
                            wishProducts.add(wishProduct);
                            sizes.add(size);
                            sizeTables.add(sizeTable);
                            product_names.add(name);
                        }
                    }
                    adapter.notifyDataSetChanged();
//                    adapter = new WishAdapter(WishListActivity.this, R.layout.list_product_item, images, infos,aliases);
//                    gv.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.d("error : ", e.getMessage());
                }
            }
        }
    }
    //관심상품 등록 클래스
    public class UpdateWishProductAlias extends AsyncTask<String, Void, String> {
        String LoadData;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String project = (String)params[0];
            String uid = (String)params[1];
            String alias = (String)params[2];
            String value = (String)params[3];

            for(String p:params){
                Log.d("hi",p);
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

                HttpPost post = new HttpPost(postURL+project);
                //서버에 보낼 파라미터
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
//            파라미터 추가하기
                postParameters.add(new BasicNameValuePair("uid", uid));
                postParameters.add(new BasicNameValuePair("alias", alias));
                postParameters.add(new BasicNameValuePair("value", value));

                //파라미터 보내기
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(postParameters, HTTP.UTF_8);
                post.setEntity(ent);

                long startTime = System.currentTimeMillis();

                HttpResponse responsePOST = client.execute(post);

                long elapsedTime = System.currentTimeMillis() - startTime;
                Log.v("debugging", elapsedTime + " ");


                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    LoadData = EntityUtils.toString(resEntity, HTTP.UTF_8);
                    Log.d("성공",LoadData);
                }
                if(responsePOST.getStatusLine().getStatusCode()==200){
//                    Log.d("디비 ","성공 이지만 중복이라 실패 ");

//                    return "fail";
                }
                else{

                    Log.d("실패 ","떙 ");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (LoadData == null){
                insertFail();
            }
            else {
                try {
                    JSONObject jsonObj = new JSONObject(LoadData);

                    // json객체.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("count");
                    int count=(int)jArray.get(0);
                    Log.d("ㅇㅍㄴ",count+"");
                    if(jArray.length()==0){
                        Log.d("별칭 수정"," 실패");
                        insertFail();
                    }else {
                        if(count>0) {
                            alias=Name;
                            aliases.set(pos,alias);
                            Toast.makeText(getApplicationContext(),"별칭을 "+Name+"으로 수정하였습니다.",Toast.LENGTH_LONG).show();;
                            Log.i("별칭 수정", "성공" + result);
                            wishProductDialog.setEditText("");
                            wishProductDialog.dismiss();
                        }
                        else
                            insertFail();
                    }

                } catch (JSONException e) {
                    insertFail();
                    Log.d("별칭수정 오류 : ", e.getMessage());
                }

            }
        }
    }
    private void insertFail(){
        wishProductDialog.setEditText("");
        Toast.makeText(getApplicationContext(),"별칭이 중복됩니다. 다시 입력해 주세요.",Toast.LENGTH_LONG).show();
    }

    // 값 불러오기
    private String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    wishProductDialog.setEditText(result.get(0)); //다이얼로그 editText 수정
                }
                break;
            }

        }
    }
}

class WishAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;

    ArrayList<String> images;
    ArrayList<String> infos;
    ArrayList<String> aliases;

    public WishAdapter(Context context, int resource, ArrayList<String> images,ArrayList<String> infos,ArrayList<String> aliases) {
        super(context, resource,images);
        this.context = context;
        this.resource = resource;
        this.images = images;
        this.infos=infos;
        this.aliases=aliases;
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
            //톡백 설정한 이름이 읽히게 변경
            //holder.imageView.setContentDescription(getInfo(position));
            holder.imageView.setContentDescription(getName(position));
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
    public String getName(int i){
        return aliases.get(i);
    }
    static class ProductViewHolder{
        public ImageView imageView;
    }
}

