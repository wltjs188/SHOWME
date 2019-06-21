package com.example.ds.final_project;

import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity {
    private final String CLOUD_VISION_API_KEY = "AIzaSyAaYatWr1knKGmO_sAhy2j2xXLeNwjEuUM";
    private final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private final int MAX_LABEL_RESULTS = 10;
    private final String TAG = ShopActivity.class.getSimpleName();
    Thread visionThread;
    ///////////////////////////////////////////////////////////////
    private EditText keywordEdt;
    private Button searchBtn;
    private Button moreBtn;
    private List<Product> productList;
    private List<Option> optionList;
    private GridView GridView;
    private ProductAdapter adapter;
    ProductSearchService service;

    String keyword; //키워드
    String Color; //색상

    Intent productInfoIntent;
    int ProductNum=4;
   // List<Product> products; //상품리스트
    List<Option> options; //상품리스트
    int more_num; //더보기 체크

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);
        productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
        keywordEdt = (EditText)findViewById(R.id.main_keyword_edt);
        searchBtn = (Button) findViewById(R.id.main_search_btn);
        moreBtn = (Button) findViewById(R.id.main_more_btn);
        productList = new ArrayList<Product>();
        optionList = new ArrayList<Option>();
        adapter = new ProductAdapter(this, R.layout.list_product_item,optionList);
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
                //startActivity(new Intent());
               // int index=position-1;
                visionThread = new Thread(){
                    public void run(){
                        Bitmap imgBitmap=toBitmap(adapter.getImage(position));
                        callCloudVision(imgBitmap);

                    }
                };
                visionThread.start();

                productInfoIntent.putExtra("info", adapter.getInfo(position));
                productInfoIntent.putExtra("url", adapter.getUrl(position));
                Log.d("detailurl", "상품검색:" + adapter.getUrl(position));
                productInfoIntent.putExtra("image", adapter.getImage(position));
                Log.i("put이미지" + position, adapter.getImage(position));
                startActivity(productInfoIntent);
            }
        });

        //더보기
        moreBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "더보기", 0).show();
                if(options.size()>more_num && (options.size()-more_num)%4==0){
                    more_num=more_product(more_num);
                }
                else {
                    more_num=0;
                    service.nextPage(keyword);
                    ProductSearchThread thread = new ProductSearchThread(service, handler);
                    thread.setColor(Color);
                    thread.start();
                }
            }
        });

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
    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
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
    }
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
    private Handler handler = new Handler(){
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
    }
}

