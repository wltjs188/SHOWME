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
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity {
//    private final String CLOUD_VISION_API_KEY = "AIzaSyAaYatWr1knKGmO_sAhy2j2xXLeNwjEuUM";
//    private final String ANDROID_CERT_HEADER = "X-Android-Cert";
//    private final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
//    private final int MAX_LABEL_RESULTS = 10;
//    private final String TAG = ShopActivity.class.getSimpleName();
//    Thread visionThread;
    ///////////////////////////////////////////////////////////////
    private EditText keywordEdt;
    private Button searchBtn;
    private Button moreBtn;
   // private List<Product> productList;
    private List<Option> optionList;
    private GridView gv;
    private ProductAdapter adapter;
   // ProductSearchService service;
    Intent productInfoIntent;
    String mJsonString;
    String IP_ADDRESS = "18.191.10.193";
    int ProductNum=4;
   // List<Product> products; //상품리스트
 //   List<Option> options; //상품리스트
    int more_num; //더보기 체크
    //상품정보 List
    ArrayList<String> productIds=new ArrayList<String>();
    ArrayList<String> optionNums=new ArrayList<String>();
    ArrayList<String> infos=new ArrayList<String>(); //상품 상세 정보
    ArrayList<String> images=new ArrayList<String>(); //상품 옵션 대표 이미지
    int index=0;
    //검색 정보
    String category = null;
    String color = null;
    String length = null;
    String size = null;
    String pattern = null;
    String fabric = null;
    String detail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
        keywordEdt = (EditText)findViewById(R.id.main_keyword_edt);
        searchBtn = (Button) findViewById(R.id.main_search_btn);
        moreBtn = (Button) findViewById(R.id.main_more_btn);
       // productList = new ArrayList<Product>();
       // optionList = new ArrayList<Option>();


        adapter = new ProductAdapter(this, R.layout.list_product_item, images,infos);
        gv = (GridView) findViewById(R.id.main_GridView);
        gv.setAdapter(adapter);
        Intent intent=getIntent();

        //검색정보 받아오기
        category=intent.getStringExtra("category").replaceAll("[\"]","");
        color=intent.getStringExtra("color").replaceAll("[\"]","");
        length=intent.getStringExtra("length").replaceAll("[\"]","");
        size=intent.getStringExtra("size").replaceAll("[\"]","");
        pattern=intent.getStringExtra("pattern").replaceAll("[\"]","");
        fabric=intent.getStringExtra("fabric").replaceAll("[\"]","");
        Log.d("category",category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle(category+"검색 결과");

        keywordEdt.setText(category);
        GetProduct task = new GetProduct();

        task.execute( "http://" + IP_ADDRESS + "/getSearchedProduct.php",category,color,length,size,pattern,fabric,detail);
//        service = new ProductSearchService(keyword);
//        ProductSearchThread thread = new ProductSearchThread(service, handler);
//        Toast.makeText(getApplicationContext(), "검색을 시작합니다.", Toast.LENGTH_LONG).show();
//        thread.setColor(Color);
//        thread.start();

// 상품검색
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//
//            @SuppressLint("WrongConstant")
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                keyword = keywordEdt.getText().toString();
//                service = new ProductSearchService(keyword);
//                Color="아이보리"; //테스트
//                ProductSearchThread thread = new ProductSearchThread(service, handler);
//                Toast.makeText(getApplicationContext(), "검색을 시작합니다.", 0).show();
//                thread.setColor(Color);
//                thread.start();
//            }
//        });

        //클릭시, 상세정보 페이지로 이동
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
               // Log.d(position+"정보",adapter.getInfo(position));
                //startActivity(new Intent());
               // int index=position-1;
//                visionThread = new Thread(){
//                    public void run(){
//                        Bitmap imgBitmap=toBitmap(adapter.getImage(position));
//                        callCloudVision(imgBitmap);
//
//                    }
//                };
//                visionThread.start();

                productInfoIntent.putExtra("productId", productIds.get(position));
                productInfoIntent.putExtra("optionNum", optionNums.get(position));
                productInfoIntent.putExtra("info", infos.get(position));
                productInfoIntent.putExtra("image", images.get(position));
                startActivity(productInfoIntent);
            }
        });

        //더보기
        moreBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("카테고리 : "+category+"색상 : "+color+"기장 : "+length+"사이즈 : "+size+"패턴 : "+pattern+"재질 : "+fabric);
//                Toast.makeText(getApplicationContext(), "더보기", 0).show();
//                if(options.size()>more_num && (options.size()-more_num)%4==0){
//                    more_num=more_product(more_num);
//                }
//                else {
//                    more_num=0;
//                    service.nextPage(category);
//                    ProductSearchThread thread = new ProductSearchThread(service, handler);
//                    thread.setColor(color);
//                    thread.start();
//                }
            }

        });

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

            if (result == null){

            }
            else {
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
            if(color!=""&&color!=null&&!color.equals("없음"))
                postParameters+="&color="+color;
            if(length!=""&&length!=null&&!length.equals("없음"))
                postParameters+="&length="+length;
            if(size!=""&&size!=null&&!size.equals("없음"))
                postParameters+="&size="+size;
            if(pattern!=""&&pattern!=null&&!pattern.equals("없음"))
                postParameters+="&pattern="+pattern;
            if(fabric!=""&&fabric!=null&&!fabric.equals("없음")) {
                Log.d("fabric:",fabric);
                postParameters += "&fabric=" + fabric;
            }
            if(detail!=""&&detail!=null&&!detail.equals("없음")){
                Log.d("detail:","엥");
                postParameters+="&detail="+detail;}
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

        String TAG_JSON="SearchedProduct";
        try {

            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray",jsonArray.length()+"");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                Log.d("상품","?");
                productIds.add(item.getString("productId"));
                optionNums.add(item.getString("optionNum"));
                String info=item.getString("name")+"\n"
                        +item.getString("category")+"\n"
                        +item.getString("length")+"\n"
                        +item.getString("price")+"\n"
                        +item.getString("size")+"\n"
                        +item.getString("color")+"\n"
                        +item.getString("fabric")+"\n"
                        +item.getString("pattern")+"\n"
                        +item.getString("detail");
                infos.add(info) ;
                images.add(item.getString("image"));
                Log.d("가져온 상품:",infos.get(i));

            }
            adapter = new ProductAdapter(this, R.layout.list_product_item, images,infos);
            gv.setAdapter(adapter);


        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
            Log.d("phptest: ",mJsonString);
            Log.d("상품","오류");
        }

    }
    public Bitmap toBitmap(String imgurl){
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
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return imgBitmap;
    }

    /*private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {

                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);

                Feature textEetection = new Feature();
                textEetection.setType("TEXT_DETECTION");
                textEetection.setMaxResults(MAX_LABEL_RESULTS);
                add(textEetection);

                Feature objectEetection = new Feature();
                objectEetection.setType("OBJECT_LOCALIZATION");
                objectEetection.setMaxResults(MAX_LABEL_RESULTS);
                add(objectEetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<ShopActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(ShopActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            ShopActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {

                Log.d("visionResult",result);
                savePreferences("visionResult",result);
            }
        }

    }

    private void callCloudVision(Bitmap bitmap) {
        // Switch text to loading


        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new ShopActivity.LableDetectionTask(ShopActivity.this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }


    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("");  //이미지 상세정보
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();

        if (labels != null) {
            for (EntityAnnotation label : labels) {
               // if(label.getScore()>=0.9) {
                    //message.append(String.format(Locale.KOREA, "%.3f: %s", label.getScore(), label.getDescription()));
                    if(label.getDescription().equals("Clothing")){}
                    else {
                        message.append(String.format(Locale.KOREA, "%s", label.getDescription()));
                        message.append("\n");
                    }
                //}
            }
        }
        else {
           // message.append("nothing\n");
        }

        if(texts !=null){
            message.append(texts.get(0).getDescription());
        }
        else {
          //  message.append("nothing\n");
        }


        return message.toString();
    }*/
    // 값 저장하기
    private void savePreferences(String key, String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
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
    /*private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what ==1 )
            {
                //Product product;
                int error;
                //arg1이 10이면 처음 검색에 대한 결과를 갖다 준걸로
                if(msg.arg1==10)
                {
                    options=checkError(msg);
//                    optionList.clear();
                    if (options.size() <= 0) {
                        Toast.makeText(ShopActivity.this,"검색된 상품이 없습니다.",Toast.LENGTH_LONG).show();
                    } else if (options.size() < 4) {
                        optionList.addAll(options.subList(0, options.size() - 1));
                        more_num=options.size() - 1;
                    }
                    else {
                        optionList.addAll(options.subList(0, ProductNum));
                        more_num=ProductNum;
                    }
                    if(optionList.size()>0){
                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < optionList.size(); i++) {
                            Log.i("사이트", optionList.get(i).getOptionValue() + "&&" + optionList.get(i).getOptionOrder()+"&&"+optionList.get(i).getProductDetailUrl());;
                        }
                    }



                }
//                arg2이 20이면 상품추가하기
                else if(msg.arg2==20){
                    //String result = "";
                    List<Option> data = (List<Option>)msg.obj;
//                    for(Option o : data)
//                        result += p.getProductName() +"\n";
                    options=checkError(msg);
                    if (options.size() <= 0) {
                        Toast.makeText(ShopActivity.this,"더 보여드릴 상품이 없습니다.",Toast.LENGTH_LONG).show();
                    }
                    else if(options.size()<4){
                        optionList.addAll(options.subList(0,options.size()-1));
                        more_num=options.size() - 1;
                    }
                    else {
                        optionList.addAll(options.subList(0, ProductNum));
                        more_num=ProductNum;
                    }
                    if(optionList.size()>0) {
                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < optionList.size(); i++) {
                            Log.i("사이트", optionList.get(i).getOptionValue() + "&&" + optionList.get(i).getOptionOrder()+"&&"+optionList.get(i).getProductDetailUrl());;
                        }
                    }
                }
            }
//            else if(msg.what==2){
//                Bundle bd=msg.getData();
//                visionResult = bd.getString("visionResult");
//        //        Log.d("visionResult",visionResult);
//            }
        }
    };
    public int more_product(int more_num){
        if (options.size() <= 0) {
            Toast.makeText(ShopActivity.this,"더 보여드릴 상품이 없습니다.",Toast.LENGTH_LONG).show();
        }
        else if(options.size()-more_num<4){
            optionList.addAll(options.subList(more_num,options.size()));
            more_num+=ProductNum;
        }
        else {
            optionList.addAll(options.subList(more_num,ProductNum + more_num));
            more_num+=ProductNum;
        }

        if(optionList.size()>0)
            adapter.notifyDataSetChanged();

        return more_num;

    }

    public List<Option> checkError(Message msg){
        Option errOption;
        int error;
        for(int i=((List<Option>) msg.obj).size()-1;i>=0;i--){
            errOption=((List<Option>) msg.obj).get(i);

//            error=errOption.errorMessage(errOption.getProductName(),errOption.getOptionValueMap());
            if (errOption.getOptionPrice()==null){ //검색결과 없을때 삭제
                ((List<Option>) msg.obj).remove(i);
               // productList.remove(i);
            }
        }
        return (List<Option>) msg.obj;
    }*/
}
//class ProductAdapter extends ArrayAdapter<String> {
//    private Context context;
//    private int resource;
//    // int i=0;
//    //  private List<Product> productList;
//    private ImageLoader imageLoader;
//    ArrayList<String> images;
//    ArrayList<String> infos;
//    int index;
//    public ProductAdapter(Context context, int resource, ArrayList<String> images,ArrayList<String> infos,int index) {
//        super(context, resource,images);
//        // TODO Auto-generated constructor stub
//        this.context = context;
//        this.resource = resource;
//        this.images = images;
//        this.infos=infos;
//        imageLoader= new ImageLoader(context);
//        this.index=index;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // TODO Auto-generated method stub
//
//        ProductViewHolder holder;
//        if(convertView == null){
//            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflate.inflate(R.layout.wish_item, parent, false);
//            holder = new ProductViewHolder();
//            holder.imageView
//                    = (ImageView) convertView.findViewById(R.id.imageView1);
//            convertView.setTag(holder);
//            holder.imageView.setContentDescription(getInfo(position));
//        }
//        else{
//            holder = (ProductViewHolder) convertView.getTag();
//        }
//        Glide.with(ProductAdapter.super.getContext()).load(images.get(position)).into(holder.imageView);
//        return convertView;
//    }
//    public String getInfo(int i){
//        return infos.get(i).toString();
//    }
//    class ProductViewHolder{
//        public ImageView imageView;
//    }
//}

class ProductAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
    ArrayList<String> images;
    ArrayList<String> infos;
    private ImageLoader imageLoader;
   // ArrayList<String> images=new ArrayList<String>();
    public ProductAdapter(Context context, int resource, ArrayList<String> images,ArrayList<String> infos) {
        super(context, resource ,images);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.resource = resource;
        this.images = images;
        this.infos=infos;
        imageLoader= new ImageLoader(context);
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
            convertView = inflate.inflate(R.layout.list_product_item, parent, false);
            holder = new ProductViewHolder();
            holder.imageView
                    = (ImageView) convertView.findViewById(R.id.product_img);
            holder.product_Info
                    =(TextView)convertView.findViewById(R.id.product_Info);
            convertView.setTag(holder);
            //holder.imageView.setContentDescription(infos.get(position));
        }
        else{
            holder = (ProductViewHolder) convertView.getTag();
        }

        //여기부터 이제 홀더객체 안의 각  위젯에 book객체의 각 멤버면수값들이랑 바인딩하면 됨ㅇㅇ
//        holder.imageView.setImageResource(R.drawable.ic_launcher);
        //int a=o.errorMessage(p.getProductName(),p.getOptionValueMap());
        if(images.size()<0){ //검색결과 없을때
            holder.product_Info.setText("검색결과가 없습니다.");
        }
        else { //검색결과 있을때
            //holder.product_Info.setText(getInfo(position));
            new ImageDownLoader(holder.imageView).execute(images.get(position));
        }

        return convertView;

    }
    public String getInfo(int i){
        return infos.get(i);
    }

    public String getImage(int i){

        return images.get(i);
    }


    class ImageDownLoader extends AsyncTask<String, Void, Bitmap>
    {
        ImageView imageView;
        public ImageDownLoader(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                images.add(params[0]);
                BufferedInputStream bi = new BufferedInputStream(url.openStream());
                bitmap = BitmapFactory.decodeStream(bi);
                bi.close();
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
        public TextView product_Info;

    }
}