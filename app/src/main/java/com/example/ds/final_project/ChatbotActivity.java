package com.example.ds.final_project;
import com.example.ds.final_project.db.DTO.Product;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ds.final_project.db.DAO.InsertUser2;
import com.example.ds.final_project.db.DTO.User;
import com.example.ds.final_project.db.DAO.UpdateUser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import android.widget.Button;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

import static android.speech.tts.TextToSpeech.ERROR;

public class ChatbotActivity extends AppCompatActivity implements AIListener{
    Button btn_chat_send;
    //서버
    String IP_ADDRESS = "18.191.10.193";

    //구글 SST 음성인식
    Intent sstIntent;
    SpeechRecognizer mRecognizer;

    //상품검색
    String query;
    String action;
    String speech;
    String remenu="";

    AIRequest aiRequest;
    AIDataService aiDataService;
    AIRequest aiRequest2;
    AIDataService aiDataService2;
    ResponseMessage.ResponseSpeech responseMessageFirst;
    ResponseMessage.ResponseSpeech responseMessageSecond;

    private ListView listView;
    private View btnSend;
    private View btnSTT;
    private EditText editText;
    //boolean isMine;
    static private List<ChatMessage> chatMessages = new ArrayList<>();
    private ArrayAdapter<ChatMessage> adapter;//= new MessageAdapter(this, 0, chatMessages);
    Gson gson = new GsonBuilder().create();
    //사용자 정보
    HashMap<String,JsonElement> parameter=new HashMap<String,JsonElement>();
    private User user=null;
    private String uuid;
//    private String user_uuid;
//    private String user_name=null;
//    private String user_phone=null;
//    private String user_address=null;
    private TextToSpeech tts;

    //stt
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int SHOP_ACTIVITY=200;
    private final int WISHLIST_ACTIVITY=300;

    ArrayList<Product> searched_products=new ArrayList<Product>(); //검색된 상품들, 버튼으로 띄울 애덜
    Product remember;
   //검색 정보
    String category = null;
    String style=null;
    String color = null;
    //    String fabric = null;
    private String mJsonString;
    String ShareType = null; //공유타입(문자/카톡)
    String fname= null; //공유할 사람 이름
    String fnumber=null; //공유할 사람 번호
    String smsg="";//공유할 메세지 내용
    String sproduct= null; //공유할 관심상품
    ArrayList<String> wishProductNames;

    Intent wishIntent,shopIntent;
    //챗봇 액션
    String ACTION="";

    // 5개까지의 멀티터치를 다루기 위한 배열
    int id[] = new int[5];
    int x[] = new int[5];
    int y[] = new int[5];
    String result;
    //tts 지연 핸들러
    final Handler handler=new Handler();
    //tts
    HashMap<String, String> params = new HashMap<String, String>();



    @Override
    protected void onResume() {
        super.onResume();
        if(getPreferences("remember")==null||getPreferences("remember")=="") //이전 상품 존재 안함
            remember=new Product();
        else{ //이전 상품 존재함
            String strContact=getPreferences("remember");
            remember=gson.fromJson(strContact,Product.class);
            Log.d("이전",strContact);
            Log.d("이전",remember.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId"); //tts
        if(user==null){

            //사용자 정보 등록 안됨
            //등록되지 않은 사용자
            final AIConfiguration config2 = new AIConfiguration("9642984963944e239cd1381a0e174ff0",
                    AIConfiguration.SupportedLanguages.Korean,
                    AIConfiguration.RecognitionEngine.System);

            aiDataService2 = new AIDataService(this,config2);
            aiRequest2 = new AIRequest();

            ChatMessage chatMessage = new ChatMessage("안녕하세요. 쇼우미입니다 쇼우미를 이용하시려면 사용자 정보를 입력하셔야합니다. \n 이름을 입력해주세요.", true);
            chatMessages.add(chatMessage);
            adapter.notifyDataSetChanged();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
                    }
                }, 1000);
        }else{
            //등록된 사용자
//            final AIConfiguration config = new AIConfiguration("b8dda671eb584e3586aba41efdd554cf",
//                    AIConfiguration.SupportedLanguages.Korean,
//                    AIConfiguration.RecognitionEngine.System);

//            aiDataService = new AIDataService(this,config);
//            aiRequest = new AIRequest();
            Log.d("채?","?");
            makeMenuMsg();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        getSupportActionBar().setTitle("쇼우미");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) { makeRequest(); }
        btn_chat_send=(Button)findViewById(R.id.btn_chat_send);
        wishIntent=new Intent(getApplicationContext(),WishListActivity.class);//나의관심상품
        shopIntent=new Intent(getApplicationContext(),ShopActivity.class); //상품검색



        if(getPreferences("remember")==null||getPreferences("remember")=="") //이전 상품 존재 안함
            remember=new Product();
        else{ //이전 상품 존재함
            String strContact=getPreferences("remember");
            remember=gson.fromJson(strContact,Product.class);
            Log.d("이전",strContact);
            Log.d("이전",remember.toString());
        }
        wishProductNames=new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_msg);
        listView.setEnabled(false);
        btnSend = findViewById(R.id.btn_chat_send);
        btnSTT=findViewById(R.id.btn_stt);
        editText = (EditText) findViewById(R.id.msg_type);

        //사용자 정보 받아오기
        uuid=getPreferences("uuid");
        if(!(getPreferences("USER")==null||getPreferences("USER")=="")){
            String strContact=getPreferences("USER");
            user=gson.fromJson(strContact,User.class);
            Log.d("uuid 정보",user.getName()+user.getAddress()+user.getPhoneNum());
        }



        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount() - 1);
//        ChatMessage chatMessage;

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        //tts 완료 시점 이후 실행함
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
            @Override
            public void onStart(String utteranceId) {
            }
            @Override
            public void onDone(String utteranceId) {
                if(user==null){
                    //등록되지 않은 사용자
                    aiRequest2.setQuery(editText.getText().toString());
                    Log.e("입력",editText.getText().toString());
                    new AITask().execute(aiRequest2);
                }else{
                    //등록된 사용자
                    aiRequest.setQuery(editText.getText().toString());
                    Log.e("입력",editText.getText().toString());
                    new AITask().execute(aiRequest);
                }
            }
            @Override
            public void onError(String utteranceId) {
            }
        });

        //dialogflow
        final AIConfiguration config = new AIConfiguration("b8dda671eb584e3586aba41efdd554cf",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);
//        final AIConfiguration config2 = new AIConfiguration("9642984963944e239cd1381a0e174ff0",
//                AIConfiguration.SupportedLanguages.Korean,
//                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this,config);
        aiRequest = new AIRequest();
//        aiDataService2 = new AIDataService(this,config2);
//        aiRequest2 = new AIRequest();


        //전송버튼
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    tts.speak("텍스트를 입력해주세요.",TextToSpeech.QUEUE_FLUSH, null);
                }
//                else if(editText.getText().toString().length() !=8 && chatMessages.get(chatMessages.size()-1).toString().contains("번호")){
//                    Toast.makeText(getApplicationContext(),"010을 제외한 8자리 번호를 입력해주세요.",Toast.LENGTH_LONG).show();
//                }
                else {

//                    tts.speak(editText.getText().toString()+"라고 말했습니다.",TextToSpeech.QUEUE_FLUSH, params);
                    if(user==null){
                        //등록되지 않은 사용자
                        aiRequest2.setQuery(editText.getText().toString());
                        Log.e("입력",editText.getText().toString());
                        new AITask().execute(aiRequest2);
                    }else{
                        //등록된 사용자
                        aiRequest.setQuery(editText.getText().toString());
                        Log.e("입력",editText.getText().toString());
                        new AITask().execute(aiRequest);
                    }
                }
//                editText.requestFocus();
            }
        });
        //STT 버튼
        btnSTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointer_count = event.getPointerCount(); //현재 터치 발생한 포인트 수를 얻는다.
        if(pointer_count > 5) pointer_count = 5; //4개 이상의 포인트를 터치했더라도 3개까지만 처리를 한다.

        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: //한 개 포인트에 대한 DOWN을 얻을 때.
                result = "싱글터치 : \n";
                id[0] = event.getPointerId(0); //터치한 순간부터 부여되는 포인트 고유번호.
                x[0] = (int) (event.getX());
                y[0] = (int) (event.getY());
                result = "싱글터치 : \n";
                result += "("+x[0]+","+y[0]+")";
                break;

            case MotionEvent.ACTION_POINTER_DOWN: //두 개 이상의 포인트에 대한 DOWN을 얻을 때.
                result = "멀티터치 :\n";
                for(int i = 0; i < pointer_count; i++) {
                    id[i] = event.getPointerId(i); //터치한 순간부터 부여되는 포인트 고유번호.
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));
                    result += "id[" + id[i] + "] ("+x[i]+","+y[i]+")\n";
                }
                break;

            case MotionEvent.ACTION_MOVE:
                result = "멀티터치 MOVE:\n";
                for(int i = 0; i < pointer_count; i++) {
                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));
                    result += "id[" + id[i] + "] ("+x[i]+","+y[i]+")\n";
                }
                break;
            case MotionEvent.ACTION_UP:
                result = "";
                break;
        }

        Log.d("좌표",result);

        return super.onTouchEvent(event);
    }
    //stt
    //주소록에서 번호 가져오기
    String findNum(String fname){
        Log.d("채윤 이름:",fname);
        String number=null;
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc");
        int i=0;
        while (c.moveToNext()) {

            // 연락처 id 값
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            // 연락처 대표 이름
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
//            Log.d("name",name);
            if(name.trim().equals(fname)) {

                // ID로 전화 정보 조회
                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null, null);

                // 데이터가 있는 경우
                if (phoneCursor.moveToFirst()) {
                    Log.d("name","찾");
                    number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                }
                phoneCursor.close();

            }
        }// end while
        c.close();
        if(number!=null)
            return number;
        else
            return "그런 사람 없어";
    }
    protected void makeChatNoPerson(String name){
        ChatMessage chatMessage = new ChatMessage(name + "으로 저장된 연락처는 없습니다. 정확한 이름을 다시한번 말씀해주세요.", true);
        chatMessages.add(chatMessage);

        //TTS 챗봇 읽어주기
        adapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 1000);
    }
    //공유 메세지 보내기 - 문자
    void sendMSG(String number,String msg){
        try {
            //String mm ="http://deal.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=1708920758&cls=3791&trTypeCd=102";
            //전송
            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(number, null, msg, null, null);
            smsManager.sendTextMessage(number, null, msg, null, null);
            Log.d("문자확인","보냈다.");
            //Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //메시지 실패 여기 바꿔라
            //Toast.makeText(getApplicationContext(), fname+"님께 전송 완료!", Toast.LENGTH_LONG).show();
            Log.d("메세지 오류",e.getMessage());
            e.printStackTrace();
        }
    }
    //관심상품 이름으로 검색하고 문자보냄
    private class GetProductToShare extends AsyncTask<String, Void, String> {

//        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(ChatbotActivity.this,
//                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            progressDialog.dismiss();

            if (result == null){
            }
            else {
                mJsonString = result;
                showResultGetProductToShare();
//                fname = null;
//                sproduct = null;
//                fnumber = null;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String uid = params[1];
            String wishProductName = params[2];
            String serverURL = params[0];
            String postParameters = "uid=" + uid+"&wishProductName="+wishProductName;

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
    private void showResultGetProductToShare(){
        // int count=0;
        String TAG_JSON="getProductToShare";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray 길이:",jsonArray.length()+"");
            if(jsonArray.length()>0){
                //관심상품 sproduct 존재
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    String productId = item.getString("productId");
                    String optionNum = item.getString("optionNum");
                    smsg="이 상품 구매 부탁드립니다!!\nhttp://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo="+productId+"\n옵션번호 : "+optionNum;
                }
                Log.d("메세지:",smsg+"\n번호:"+fnumber);
                if(ShareType.equals("카톡")){
                    ShareKakao();
                    complateKShare((sproduct));
                }
                else{
                    sendMSG(fnumber,smsg);
                    //Toast.makeText(this,sproduct+" 있어용",Toast.LENGTH_LONG).show();
                    complateMShare((fname));
                }

                fname=null;
                fnumber=null;
                sproduct=null;

            }else {
                //관심상품 sproduct 존재 안함

                Toast.makeText(this,sproduct+" 없어용",Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), sproduct+"상품은 없습니다. 다시 입력해주세요.", Toast.LENGTH_LONG).show();
//                aiRequest.setQuery("문자다시");
//                Log.e("입력",editText.getText().toString());
//                new AITask().execute(aiRequest);
//                aiRequest.setQuery("문자다시");
//                Log.e("입력",editText.getText().toString());
//                new AITask().execute(aiRequest);
            }

        } catch (JSONException e) {
            //관심상품 sproduct 존재 안함
            Log.d("showResult : ", e.getMessage());
            Log.d("showResult : ", mJsonString);
            Toast.makeText(this,sproduct+"는 관심상품에 없습니다.",Toast.LENGTH_LONG).show();
            fnumber = null; sproduct = null; fname = null;

            if(ShareType.equals("카톡")){
                aiRequest.setQuery("카톡다시");
                Log.e("입력",editText.getText().toString());
                new AITask().execute(aiRequest);
            }
            else{
                aiRequest.setQuery("문자다시");
                Log.e("입력",editText.getText().toString());
                new AITask().execute(aiRequest);
            }


        }

    }
    //STT 음성 입력
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                    btn_chat_send.callOnClick();
                }
                break;
            }
            case SHOP_ACTIVITY:{
//                makeMenuMsg();
                break;
            }
            case WISHLIST_ACTIVITY:{
//                makeMenuMsg();
                break;
            }
        }
    }



    //리메뉴
    protected String getRemenu(Result result){
        responseMessageSecond = (ResponseMessage.ResponseSpeech)result.getFulfillment().getMessages().get(1);
        remenu=responseMessageSecond.getSpeech().get(0);
        result.getContexts().clear();
        return remenu;
    }
    //메뉴 메세지
    protected void makeMenuMsg(){

        ChatMessage chatMessage = new ChatMessage(user.getName()+"님 안녕하세요?\n메뉴를 선택해주세요\n" +
                "1. 상품검색\n" +
                "2. 이전 검색 다시보기\n" +
                "3. 관심상품보기\n" +
                "4. 관심상품 공유하기\n"+
                "5. 사용자 정보 수정", true);
        chatMessages.add(chatMessage);

        //TTS 챗봇 읽어주기
        adapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        }, 1000);
    }
    //문자공유완료
    protected void complateMShare(String name){

        ChatMessage chatMessage = new ChatMessage(name+"님께 공유했습니다.\n메뉴를 선택해주세요\n" +
                "1. 상품검색\n" +
                "2. 이전 검색 다시보기\n" +
                "3. 관심상품보기\n" +
                "4. 관심상품 공유하기\n"+
                "5. 사용자 정보 수정", true);
        chatMessages.add(chatMessage);

        //TTS 챗봇 읽어주기

        adapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 1000);
    }
    //문자공유완료
    protected void complateKShare(String sproduct){

        ChatMessage chatMessage = new ChatMessage(sproduct+"공유를 위해 카톡으로 연결합니다", true);
        chatMessages.add(chatMessage);

        //TTS 챗봇 읽어주기
        adapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 1000);
    }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }
    //    public void ButtonClicked(View view){
//        aiService.startListening();
//    }
    private class AITask extends AsyncTask<AIRequest, Void, AIResponse>{
        protected AIResponse doInBackground(AIRequest... requests) {
            final AIRequest request = requests[0];
            try {
                final AIResponse response;
                if(user==null){
                    response= aiDataService2.request(aiRequest2);
                    return response;
                }else {
                    response= aiDataService.request(aiRequest);
                    return response;
                }

            } catch (AIServiceException e) {
                Log.e("에러",e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            if (aiResponse != null) {
                onResult(aiResponse);
            }
        }
    }



    public void onResult(AIResponse response) {
        final Result result = response.getResult();
//        Log.d("yoon response",response.toString());
        ACTION=result.getAction();
        Log.i("액션",ACTION);
        String so=null;
        //챗봇 액션 처리
        switch (ACTION){

            case "ACTION_USER"://사용자등록 : 이름받아오기
                String name="";
                String address="";
                String phoneNum="";
                parameter=getParameter(result);
                //이름
                if(parameter.containsKey("user_name")){
                    name=""+parameter.get("user_name");
                    name=name.substring(9);
                    name=name.substring(0,name.length()-2);
                }
                //핸드폰 번호
                if(parameter.containsKey("user_phone")){
                    phoneNum=""+parameter.get("user_phone");
                    phoneNum=(phoneNum.replaceAll("\"","")).replaceAll("-","");

                }
                //주소 시,구,동
                if(parameter.containsKey("city")) {
                    if(parameter.containsKey("state")){ //도
                        address = "" + parameter.get("state");
                    }
                    address = address + parameter.get("city"); //시
                    if(parameter.containsKey("county")){ //구,군
                        address=address+parameter.get("county");
                    }
                    if(parameter.containsKey("county1")){ //면,읍,리
                        address=address+parameter.get("county1");
                    }
                    if(parameter.containsKey("village")){//동
                        address=address+parameter.get("village");
                    }
                    if(parameter.containsKey("address")){ //상세주소,도로명주소
                        address=address+parameter.get("address");
                    }
                    address=address.replaceAll("\"","");
                }

                //사용자 정보 DB에 넣기
                if( !name.equals("") && !address.equals("") && !phoneNum.equals("") ) {
                    user=new User(uuid,name,address,phoneNum);

                    String strContact = gson.toJson(user, User.class);
                    savePreferences("USER",strContact);
                    Log.d("사용자 정보 DB등록",user.getName()+", "+user.getAddress()+","+user.getPhoneNum());

                    InsertUser2 task = new InsertUser2();
                    task.execute(user.getId(),user.getName(),user.getAddress(),user.getPhoneNum());
                    Log.i("액션USER",ACTION);
                }
                break;
            case "ACTION_M_NAME"://사용자정보수정 : 이름
                parameter=getParameter(result);
                name=""+parameter.get("user_name");
                name=name.substring(9);
                name=name.substring(0,name.length()-2);
                user.setName(name);


                String strContact = gson.toJson(user, User.class);
                savePreferences("USER",strContact);
                UpdateUser task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("UpdateUser",user.getId(),"name",user.getName());
//                remenu=getRemenu(result);
                break;
            case "ACTION_M_PHONE"://사용자정보수정 : 핸드폰번호
                phoneNum=""+parameter.get("user_phone");
                phoneNum=(phoneNum.replaceAll("\"","")).replaceAll("-","");
                user.setPhoneNum(phoneNum);

                strContact = gson.toJson(user, User.class);
                savePreferences("USER",strContact);
                task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("UpdateUser",user.getId(),"phoneNum",user.getPhoneNum());
//                remenu=getRemenu(result);
                break;
            case "ACTION_M_ADDRESS"://사용자정보수정 : 주소
                parameter=getParameter(result);
                //주소 시,구,동 받아오기
                address = "" + parameter.get("city")+parameter.get("county");
                if(parameter.containsKey("county1")){
                    address=address+parameter.get("county1");
                }
                address=address+parameter.get("village");
                address=address.replaceAll("\"","");
                user.setAddress(address);
                strContact = gson.toJson(user, User.class);
                savePreferences("USER",strContact);
                task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("UpdateUser",user.getId(),"address",user.getAddress());
//                remenu=getRemenu(result);
                result.getContexts().clear();

                break;
            case "Product_Category": //카테고리
                parameter=getParameter(result);
                category = parameter.get("Category").toString().replaceAll("\"","");
                break;
            case "Search_Style.Search_Style-no": //카테고리만 입력
                so=category;
                remember.setCategory(category);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);

                SearchProduct task = new SearchProduct();
                task.execute("SearchOne", category);

                category = null; style=null; color = null;
                Log.d("yoon search","카테고리로 검색: "+so);
                break;

            case "Product_Style": //스타일
                parameter=getParameter(result);
                style = parameter.get("Style").toString().replaceAll("\"","");
                Log.d("yoon style",style);
                break;
            case "Search_Style.Search_Color.Search_Color-no": //카테고리, 스타일로 검색
                so=category+", "+style;
                remember.setCategory(category);
                remember.setStyle(style);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);

                task = new SearchProduct();
                task.execute("SearchTwo", category,style);

                category = null; style=null; color = null;
                Log.d("yoon search","카테고리, 스타일로 검색: "+so);

                break;
            case "Product_Color": //색상 , (카테고리,스타일,색상 다 입력 됨)
                parameter=getParameter(result);
                Log.d("yoon color",parameter.toString());
                color = parameter.get("Color").toString().replaceAll("\"","");

                so="카테고리: "+category+" 스타일:"+style+" 색상 : "+color;
                Log.d("yoon search","카테고리, 스타일, 색상 으로 검색: "+so);


                remember.setCategory(category);
                remember.setStyle(style);
                remember.setColor(color);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);

                task = new SearchProduct();
                task.execute("SearchThree", category,style,color);

                category = null; style=null; color = null;


                break;


            case "ACTION_MENU" :
                parameter=getParameter(result);
                if(parameter.containsKey("Wish_Item")){ //관심상품이동
                    startActivityForResult(wishIntent,WISHLIST_ACTIVITY);
                    result.getContexts().clear();
                }
                else if(parameter.containsKey("pre_search")) {
                    //이전 검색
                    if (remember == null) {
                        //이전 검색 못해
                    } else {
                        if(remember.getStyle()==null){
                            task = new SearchProduct();
                            task.execute("SearchOne", remember.getCategory());
                        }else if(remember.getColor()==null){
                            task = new SearchProduct();
                            task.execute("SearchTwo", remember.getCategory(), remember.getStyle());
                        }else{
                            task = new SearchProduct();
                            task.execute("SearchThree", remember.getCategory(),remember.getStyle(),remember.getColor());
                        }

                    }

                }
                break;
            case "Share_K_Product"://카카오 공유하기
                parameter=getParameter(result);
                Log.d("카카오",parameter.toString());

                //공유할 상품
                if(parameter.containsKey("ShareKProduct")){
                    Log.d("카카오","카카카캌");
                    sproduct = parameter.get("ShareKProduct").toString().replace('\"',' ').trim();
                }

                if(sproduct==null) {
                    GetWishProductName task3 = new GetWishProductName();
                    task3.execute("http://" + IP_ADDRESS + "/getWishProductName.php", user.getId());
                }
                else{
                    ShareType = "카톡";
                    GetProductToShare task2 = new GetProductToShare();
                    task2.execute( "http://" + IP_ADDRESS+"/getProductToShare.php",user.getId(),sproduct);
                }

                break;
            case "Share_M_Person"://메시지 공유하기
                Log.d("챗봇","문자");
                parameter=getParameter(result);
                //공유할 사람
                if(parameter.containsKey("ShareMPerson")) {
                    fname = parameter.get("ShareMPerson").toString().replace('\"',' ').trim();
                }
                //공유할 상품
                if(parameter.containsKey("ShareMProduct")){
                    sproduct = parameter.get("ShareMProduct").toString().replace('\"',' ').trim();
                }
                if(sproduct==null) {
                    GetWishProductName task4 = new GetWishProductName();
                    task4.execute("http://" + IP_ADDRESS + "/getWishProductName.php", user.getId());
                }

//                    }
//                    else {
//                        String m="";
//                        for (int i = 0; i < wishProductNames.size(); i++) {
//                            if (i == wishProductNames.size() - 1)
//                                m += wishProductNames.get(i);
//                            else
//                                m += wishProductNames.get(i) + ", ";
//                        }
//                        Toast.makeText(this.getApplicationContext(), "관심상품에 " + m + "가 있습니다.", Toast.LENGTH_LONG).show();
//                    }

//                Toast.makeText(this.getApplicationContext(),"관심상품에 ~~~가 있습니다.",Toast.LENGTH_LONG).show();

                //2가지 다 입력되었다면,
               if( fname != null && sproduct != null){

                   fnumber = findNum(fname); // 공유자 이름으로 번호 찾기

                   if(!fnumber.equals("그런 사람 없어")){
                       //관심상품 있는지 검사
                       Log.d("메세지",fnumber);
                       // 연락처 조회 된 경우, 공유 실행
                       ShareType = "문자";
                       GetProductToShare task2 = new GetProductToShare();
                       task2.execute( "http://" + IP_ADDRESS+"/getProductToShare.php",user.getId(),sproduct);

                   }
                   else{
                       Toast.makeText(getApplicationContext(), fname+"님의 번호가 없습니다. 다시 입력해주세요.", Toast.LENGTH_LONG).show();
                       fnumber = null; sproduct = null; fname = null;

                       aiRequest.setQuery("문자다시");
                       Log.e("입력",editText.getText().toString());
                       new AITask().execute(aiRequest);
                   }
                   result.getContexts().clear();
                }

                break;
//            case "Again_Share_M"://문자 다시
//                Log.d("챗봇","again");
//                parameter=getParameter(result);
//                //공유할 사람
//                if(parameter.containsKey("AgainMPerson")) {
//
//                    fname = parameter.get("AgainMPerson").toString().replace('\"',' ').trim();
//                    //Toast.makeText(getApplicationContext(), fname+"재시도", Toast.LENGTH_LONG).show();
//                }
//                //공유할 상품
//                if(parameter.containsKey("AgainMProduct")){
//                    sproduct = parameter.get("AgainMProduct").toString().replace('\"',' ').trim();
//                }
//                if(sproduct==null) {
//                    GetWishProductName task = new GetWishProductName();
//                    task.execute("http://" + IP_ADDRESS + "/getWishProductName.php", user_uuid);
//                }
//                //2가지 다 입력되었다면,
//                if( fname != null && sproduct != null){
//                    fnumber = findNum(fname); // 공유자 이름으로 번호 찾기
////                   Log.d("먕","uuid"+user_uuid+" 번호"+fnumber+" 이름"+fname+findNum("강정현"));
//                    Log.d("메세지",fnumber);
//                    if(!fnumber.equals("그런 사람 없어")) {
//                        //관심상품 있는지 검사
//                        //if(있으면){
//                        // 연락처 조회 된 경우, 공유 실행
//                        GetProductToShare task2 = new GetProductToShare();
//                        task2.execute("http://" + IP_ADDRESS + "/getProductToShare.php", user_uuid, sproduct);
//                        //}
////                        else{
////                            Toast.makeText(getApplicationContext(), sproduct+"상품은 없습니다. 다시 입력해주세요.", Toast.LENGTH_LONG).show();
////                            //failShare((fname));
////                            aiRequest.setQuery("문자다시");
////                            Log.e("입력",editText.getText().toString());
////                            new AITask().execute(aiRequest);
////                        }
//                    }
//                    else{
//                        Toast.makeText(getApplicationContext(), fname+"님의 번호가 없습니다. 다시 입력해주세요.", Toast.LENGTH_LONG).show();
//                        //failShare((fname));
//                        fnumber = null; sproduct = null; fname = null;
//                        aiRequest.setQuery("문자다시");
//                        Log.e("입력",editText.getText().toString());
//                        new AITask().execute(aiRequest);
//                    }
////                    fnumber = null; sproduct = null; fname = null;
//                    result.getContexts().clear();
//                }
//                break;
        }

        query=result.getResolvedQuery();
//        if(ACTION.equals("ACTION_USER") || ACTION.equals("ACTION_M_NAME") || ACTION.equals("ACTION_M_PHONE") || ACTION.equals("ACTION_M_ADDRESS") ){
//            responseMessageSecond = (ResponseMessage.ResponseSpeech)result.getFulfillment().getMessages().get(0);
//            speech=responseMessageSecond.getSpeech().get(0);
//        }
//        else {
//        if(get)

        speech = result.getFulfillment().getSpeech();
//        }



        ChatMessage chatMessage;
        chatMessage = new ChatMessage(query, false);
        Log.d("쿼리",query);
        if(!query.equals("문자다시")){
            if(!query.equals("카톡다시")) {
                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
                editText.setText("");
            }
        }
        chatMessage = new ChatMessage(speech, true);
        Log.d("대답",speech);
        if(!speech.equals("")){
            chatMessages.add(chatMessage);
            adapter.notifyDataSetChanged();
            tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
        }
        if(remenu!=""){
            chatMessage = new ChatMessage(remenu, true);
            chatMessages.add(chatMessage);
            adapter.notifyDataSetChanged();
            tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
            remenu="";
        }
    }
    @Override
    public void onError(AIError error) { }
    @Override
    public void onAudioLevel(float level) { }
    @Override
    public void onListeningStarted() { }
    @Override
    public void onListeningCanceled() { }
    @Override
    public void onListeningFinished() { }

    private class GetWishProductName extends AsyncTask<String, Void, String> {

//        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(ChatbotActivity.this,
//                    "잠시만 기다려주세요", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            progressDialog.dismiss();

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
        Log.i("공유","showREsult()");
        String TAG_JSON="WishProductName";
        wishProductNames.clear();
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.d("jsonArray",jsonArray.length()+"");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String wishProductName = item.getString("wishProductName");
                wishProductNames.add(wishProductName);
            }
            if(wishProductNames.size()>0) {
                String m="";
                for (int i=0;i<wishProductNames.size();i++){
                    if(i==wishProductNames.size()-1)
                        m+=wishProductNames.get(i);
                    else
                        m+=wishProductNames.get(i)+", ";
                }
                Toast.makeText(ChatbotActivity.this, "관심상품에 "+m+"가 있습니다.", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(ChatbotActivity.this,"등록된 관심상품이 없습니다.",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
            Log.d("phptest: ",mJsonString);
            Toast.makeText(ChatbotActivity.this,"등록된 관심상품이 없습니다.",Toast.LENGTH_LONG).show();
        }

    }
    // 값 저장하기
    private void savePreferences(String key, String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }
    // 값 불러오기
    public String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

    //챗봇 파라미터 가져오기
    private HashMap<String,JsonElement> getParameter(Result result){
        HashMap<String,JsonElement> parameter=new HashMap<String, JsonElement>();
        for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
            parameter.put(entry.getKey(),entry.getValue());
        }
        return parameter;
    }
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

    private void ShareKakao(){
        Log.d("공유","카카오");
        TextTemplate params = TextTemplate.newBuilder(smsg, LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()).build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
                Log.d("kakao",errorResult.toString());
            }
            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }
    class SearchProduct extends AsyncTask<String, Void,String> {
        String LoadData;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ChatbotActivity.this);
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
                    postParameters.add(new BasicNameValuePair("category", category));
                }else if(project.equals("SearchTwo")){
                    postParameters.add(new BasicNameValuePair("category", category));
                    postParameters.add(new BasicNameValuePair("style", style));
                }else if(project.equals("SearchThree")){
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
                    Log.i("가져온 데이터", LoadData);
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
                    }else {
                        Log.i("검색","성공"+result);
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

                            searched_products.add(product);

                            Log.i("가져온 데이터", product.toString());

                        }

                    }

                } catch (JSONException e) {
                    Log.d("검색 오류 : ", e.getMessage());
                }

            }
        }
    }
}

class MessageAdapter extends ArrayAdapter<ChatMessage> { //메세지어댑터

    private Activity activity;
    private List<ChatMessage> messages;

    public MessageAdapter(Activity context, int resource, List<ChatMessage> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        // ChatMessage chatMessage = getItem(position);

        ChatMessage chatMessage = messages.get(position);
        if (chatMessage.isMine()) {
            layoutResource = R.layout.item_chat_left;
            // Log.d("챗",position+chatMessage.getContent().toString()+"왼");ㅇ
        } else {
            layoutResource = R.layout.item_chat_right;
            // Log.d("챗",position+chatMessage.getContent().toString()+"오");
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.msg.setText(chatMessage.getContent());
        holder.msg.setContentDescription(messages.get(position)+"");
//        convertView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // 여기서 이벤트를 막습니다.
//                return ;
//            }
//        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        ChatMessage chatMessage = messages.get(position);
        chatMessage.isMine();
        if(chatMessage.isMine()) return 0;
        else return 1;
    }
    private class ViewHolder {
        private TextView msg;
        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
        }
    }




}





