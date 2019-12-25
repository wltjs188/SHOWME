package com.example.ds.final_project;
import com.bumptech.glide.Glide;
import com.example.ds.final_project.db.DTO.Product;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.sip.SipSession;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ds.final_project.db.DAO.InsertUser2;
import com.example.ds.final_project.db.DTO.User;
import com.example.ds.final_project.db.DAO.UpdateUser;

import com.example.ds.final_project.db.DTO.WishProduct;
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
import java.util.Arrays;
import java.util.Collection;
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

    //구글 SST 음성인식
    Intent sstIntent;
    SpeechRecognizer mRecognizer;

    //상품검색
    String query;
    String action;
    String speech;
    ChatMessage chatMessage2;
    String remenu="";

    //버튼 타입 정의
    final int BTN_TYPE_MENU=1;
    final int BTN_TYPE_CATEGORY=2;
    final int BTN_TYPE_STYLE_TOP=3;
    final int BTN_TYPE_STYLE_PANTS=4;
    final int BTN_TYPE_STYLE_SKIRT=5;
    final int BTN_TYPE_STYLE_DRESS=6;
    final int BTN_TYPE_STYLE_OUTER=7;
    final int BTN_TYPE_COLOR=8;
    final int BTN_TYPE_SHARE=9;
    final int BTN_TYPE_USERINFO=10;


    //챗봇 전송 리스너
    View.OnClickListener btnSendListener;

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
    private MessageAdapter adapter;//= new MessageAdapter(this, 0, chatMessages);
    Gson gson = new GsonBuilder().create();
    //사용자 정보
    HashMap<String,JsonElement> parameter=new HashMap<String,JsonElement>();
    private User user=null;
    private String uuid;
    private TextToSpeech tts;

    //stt
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int SHOP_ACTIVITY=200;
    private final int WISHLIST_ACTIVITY=300;

    ArrayList<Product> searched_products=new ArrayList<Product>(); //검색된 상품들, 버튼으로 띄울 애덜

    //검색 정보
    Product remember;
    String category = null;
    String style=null;
    String color = null;

    String smsg="";//공유할 메세지 내용
    String sproduct= null; //공유할 관심상품
    ArrayList<String> wishProductNames;

    //share
    String shareType;
    String mProduct;
    String mPerson;
    String mNumber;
    String kProduct;

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
//            makeMenuMsg();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        getSupportActionBar().setTitle("쇼우미");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼

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
//        listView.setEnabled(false);
        btnSend = findViewById(R.id.btn_chat_send);
        btnSTT=findViewById(R.id.btn_stt);
        editText = (EditText) findViewById(R.id.msg_type);

        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount() - 1);

        //사용자 정보 받아오기
//        uuid=getPreferences("uuid");
//        if(!(getPreferences("USER")==null||getPreferences("USER")=="")){
//            String strContact=getPreferences("USER");
//            user=gson.fromJson(strContact,User.class);
//            Log.d("uuid 정보",user.getName()+user.getAddress()+user.getPhoneNum());
//            makeMenuMsg();
//        }



        //set ListView adapter first
//        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
//        listView.setAdapter(adapter);
//        listView.setSelection(adapter.getCount() - 1);

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
//        tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
//            @Override
//            public void onStart(String utteranceId) {
//            }
//            @Override
//            public void onDone(String utteranceId) {
//                String input=editText.getText().toString();
//                if(user==null){
//                    //등록되지 않은 사용자
//                    aiRequest2.setQuery(input);
//                    Log.e("입력",input);
//                    new AITask().execute(aiRequest2);
//                }else{
//                    //등록된 사용자
//                    aiRequest.setQuery(input);
//                    Log.e("입력",input);
//                    new AITask().execute(aiRequest);
//                }
//            }
//            @Override
//            public void onError(String utteranceId) {
//            }
//        });

        //dialogflow
        final AIConfiguration config = new AIConfiguration("b8dda671eb584e3586aba41efdd554cf",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);
//        final AIConfiguration config2 = new AIConfiguration("9642984963944e239cd1381a0e174ff0",
//                AIConfiguration.SupportedLanguages.Korean,
//                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this,config);
        aiRequest = new AIRequest();

        //전송버튼
        btnSendListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input="";
//                Log.i("버튼누름","버튼누름"+input);
                if (editText.getText().toString().trim().equals("") && v.getId()==R.id.btn_chat_send) {
                    tts.speak("텍스트를 입력해주세요.",TextToSpeech.QUEUE_FLUSH, null);
                }
//                else if(editText.getText().toString().length() !=8 && chatMessages.get(chatMessages.()-1).toString().contains("번호")){
//                    Toast.makeText(getApplicationContext(),"010을 제외한 8자리 번호를 입력해주세요.",Toast.LENGTH_LONG).show();
//                }
                else {
                    if(v.getId()==R.id.btn_chat_send) //텍스트로 전송할때
                        input = editText.getText().toString();
                    else {
                        input = ((Button) v).getText().toString(); //버튼으로 전송할때
                        Log.i("버튼누름","버튼누름"+input);
                    }
//                    tts.speak(editText.getText().toString()+"라고 말했습니다.",TextToSpeech.QUEUE_FLUSH, params);
                    if(user==null){
                        //등록되지 않은 사용자
                        aiRequest2.setQuery(input);
                        Log.d("자판입력",input);
                        new AITask().execute(aiRequest2);
                    }else{
                        //등록된 사용자
                        if (shareType != null) { //공유
                            ChatMessage chatMessage;
                            chatMessage = new ChatMessage(input, false);
                            if (shareType.equals("msg")) {  //문자공유
                                if (mProduct == null) {
                                // 없는 상품
//                                    chatMessages.add(chatMessage);
//                                    adapter.notifyDataSetChanged();
//                                    editText.setText("");
//                                input="문자상품다시";
                                    CheckShareProduct task=new CheckShareProduct();
                                    task.execute("CheckShareProduct",uuid,input);
                                }
                                else if (findNum(input) == null){//없는 사람
                                    chatMessages.add(chatMessage);
                                    adapter.notifyDataSetChanged();
                                    editText.setText("");
                                    input = "문자사람다시";
                                    aiRequest.setQuery(input);

                                    Log.e("입력", input);
                                    new AITask().execute(aiRequest);
                                }
                                else if (findNum(input).equals("번호")){//번호
                                    if(input.length()<8){
                                        chatMessages.add(chatMessage);
                                        adapter.notifyDataSetChanged();
                                        editText.setText("");
                                        input = "문자번호다시";
                                        aiRequest.setQuery(input);

                                        Log.e("입력2", input);
                                        new AITask().execute(aiRequest);
                                    }else {
                                        aiRequest.setQuery(input);

                                        Log.e("입력", input);
                                        new AITask().execute(aiRequest);
                                    }
                                }else {
                                    aiRequest.setQuery(input);

                                    Log.e("입력", input);
                                    new AITask().execute(aiRequest);

                                }
                            }
                        if (shareType.equals("kakao")) { //카톡 공유
                            if(mProduct==null){
                                // 없는 상품
//                                    chatMessages.add(chatMessage);
//                                    adapter.notifyDataSetChanged();
//                                    editText.setText("");
//                                input="카톡상품다시";
                                CheckShareProduct task=new CheckShareProduct();
                                task.execute("CheckShareProduct",uuid,input);
                            }
                        }
                    } else {//공유아니
                            aiRequest.setQuery(input);

                            Log.e("입력", input);
                            new AITask().execute(aiRequest);
                        }
                    }
                }
//                editText.requestFocus();

            }
        };
        btnSend.setOnClickListener(btnSendListener);

        uuid=getPreferences("uuid");
        if(!(getPreferences("USER")==null||getPreferences("USER")=="")){
            String strContact=getPreferences("USER");
            user=gson.fromJson(strContact,User.class);
            Log.d("uuid 정보",user.getName()+user.getAddress()+user.getPhoneNum());
            makeMenuMsg();
        }





//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editText.getText().toString().trim().equals("")) {
//                    tts.speak("텍스트를 입력해주세요.",TextToSpeech.QUEUE_FLUSH, null);
//                }
////                else if(editText.getText().toString().length() !=8 && chatMessages.get(chatMessages.size()-1).toString().contains("번호")){
////                    Toast.makeText(getApplicationContext(),"010을 제외한 8자리 번호를 입력해주세요.",Toast.LENGTH_LONG).show();
////                }
//                else {
//
////                    tts.speak(editText.getText().toString()+"라고 말했습니다.",TextToSpeech.QUEUE_FLUSH, params);
//                    if(user==null){
//                        //등록되지 않은 사용자
//                        aiRequest2.setQuery(editText.getText().toString());
//                        Log.e("입력",editText.getText().toString());
//                        new AITask().execute(aiRequest2);
//                    }else{
//                        //등록된 사용자
//                        aiRequest.setQuery(editText.getText().toString());
//                        Log.e("입력",editText.getText().toString());
//                        new AITask().execute(aiRequest);
//                    }
//                }
////                editText.requestFocus();
//            }
//        });
        //STT 버튼
        btnSTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
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
        else if(isStringDouble(fname))
            return  "번호";
        else
            return null;
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
                makeMenuMsg();
                break;
            }
        }
    }

    //메뉴 메세지
    protected void makeMenuMsg(){

        //멘트
        ChatMessage chatMessage = new ChatMessage(user.getName()+"님 안녕하세요?\n아래 버튼 메뉴를 선택해주세요\n", true);
        chatMessages.add(chatMessage);

        //메뉴 버튼
        chatMessage2 = new ChatMessage("버튼",true);
        chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
        adapter.setButton(btnSendListener); //버튼이름 설정
        chatMessages.add(chatMessage2);

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

        ChatMessage chatMessage = new ChatMessage(name+"님께 공유했습니다.\n메뉴를 선택해주세요\n", true);
        chatMessages.add(chatMessage);

        chatMessage2 = new ChatMessage("버튼",true);
        chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
        adapter.setButton(btnSendListener); //버튼이름 설정
        chatMessages.add(chatMessage2);

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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
        SearchProduct searchtask;
        Log.i("액션",ACTION);
        String so=null;
        //챗봇 액션 처리

        chatMessage2= new ChatMessage("",true);//버튼 메세지
        Log.i("액션",ACTION);

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
                    chatMessage2 = new ChatMessage("버튼",true);
                    chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                    adapter.setButton(btnSendListener); //버튼이름 설정
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
                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정
//                remenu=getRemenu(result);
                break;
            case "ACTION_M_PHONE"://사용자정보수정 : 핸드폰번호
                parameter=getParameter(result);
                phoneNum=""+parameter.get("user_phone");
                phoneNum=(phoneNum.replaceAll("\"","")).replaceAll("-","");
                user.setPhoneNum(phoneNum);

                strContact = gson.toJson(user, User.class);
                savePreferences("USER",strContact);
                task1 = new UpdateUser(); //사용자정보 수정
                Log.d("check",parameter.toString());
                Log.d("check",phoneNum);
                Log.d("check",user.getPhoneNum());
                task1.execute("UpdateUser",user.getId(),"phoneNum",user.getPhoneNum());
                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정
//                remenu=getRemenu(result);
                break;
            case "ACTION_M_ADDRESS"://사용자정보수정 : 주소

                parameter=getParameter(result);
                String maddress="";
                if(parameter.containsKey("state")){ //도
                    maddress = "" + parameter.get("state");
                }
                Log.d("city",parameter.get("city").toString());
                maddress = maddress + parameter.get("city"); //시
                if(parameter.containsKey("county")){ //구,군
                    maddress=maddress+parameter.get("county");
                }
                if(parameter.containsKey("county1")){ //면,읍,리
                    maddress=maddress+parameter.get("county1");
                }
                if(parameter.containsKey("village")){//동
                    maddress=maddress+parameter.get("village");
                }
                if(parameter.containsKey("address")){ //상세주소,도로명주소
                    maddress=maddress+parameter.get("address");
                }
                maddress=maddress.replaceAll("\"","");
//                maddress=maddress.replaceAll("\[","");
//                //주소 시,구,동 받아오기
//                address = "" + parameter.get("city")+parameter.get("county");
//                if(parameter.containsKey("county1")){
//                    maddress=address+parameter.get("county1");
//                }
//                address=address+parameter.get("village");
//                address=address.replaceAll("\"","");
                user.setAddress(maddress);
                strContact = gson.toJson(user, User.class);
                savePreferences("USER",strContact);
                task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("UpdateUser",user.getId(),"address",user.getAddress());
//                remenu=getRemenu(result);
                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정
                result.getContexts().clear();

                break;
            case "Product_Category": //카테고리
                parameter=getParameter(result);
                category = parameter.get("Category").toString().replaceAll("\"","");
                int btn_type=0;
                chatMessage2 = new ChatMessage("버튼",true);
                switch (category){
                    case "상의":
                        btn_type=BTN_TYPE_STYLE_TOP;
                        break;
                    case "바지":
                        btn_type=BTN_TYPE_STYLE_PANTS;
                        break;
                    case "스커트":
                        btn_type=BTN_TYPE_STYLE_SKIRT;
                        break;
                    case "원피스":
                        btn_type=BTN_TYPE_STYLE_DRESS;
                        break;
                    case "아우터":
                        btn_type=BTN_TYPE_STYLE_OUTER;
                        break;
                }
                chatMessage2.setButton(btn_type); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정

                so=category;
                remember.setCategory(category);
                remember.setStyle(null);
                remember.setColor(null);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);

                //카테고리만 검색
                searchtask = new SearchProduct();
                searchtask.execute("SearchOne2", category);

                break;
            case "Search_Style.Search_Style-no": //카테고리만 입력
                so=category;
                remember.setCategory(category);
                remember.setStyle(null);
                remember.setColor(null);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);
                searchtask = new SearchProduct();
                searchtask.execute("SearchOne2", category);


                category = null; style=null; color = null;
                Log.d("yoon search","카테고리로 검색: "+so);

                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼이름 설정


                break;

            case "Product_Style": //스타일
                parameter=getParameter(result);
                style = parameter.get("Style").toString().replaceAll("\"","");
                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_COLOR); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정

                so=category+", "+style;
                remember.setCategory(category);
                remember.setStyle(style);
                remember.setColor(null);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);

                searchtask = new SearchProduct();
                searchtask.execute("SearchTwo2", category,style);

                break;
            case "Search_Style.Search_Color.Search_Color-no": //카테고리, 스타일로 검색
                so=category+", "+style;
                remember.setCategory(category);
                remember.setStyle(style);
                remember.setColor(null);
                strContact = gson.toJson(remember, Product.class);
                savePreferences("remember",strContact);

                searchtask = new SearchProduct();
                searchtask.execute("SearchTwo2", category,style);

                category = null; style=null; color = null;
                Log.d("yoon search","카테고리, 스타일로 검색: "+so);

//                startActivity(shopIntent);


                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼이름 설정
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

                searchtask = new SearchProduct();
                searchtask.execute("SearchThree2", category,style,color);

                category = null; style=null; color = null;

//                startActivity(shopIntent);

//                chatMessage = new ChatMessage(user.getName()+"님 안녕하세요?\n아래 버튼 메뉴를 선택해주세요\n", true);
//                chatMessages.add(chatMessage);
//
//                chatMessage2 = new ChatMessage("버튼",true);
//                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
//                adapter.setButton(btnSendListener); //버튼이름 설정
                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼이름 설정

                break;


            case "ACTION_MENU" :
                parameter=getParameter(result);
                Log.d("check",parameter.toString());
                if(parameter.containsKey("Wish_Item")){ //관심상품이동
                    startActivityForResult(wishIntent,WISHLIST_ACTIVITY);

                }
                else if(parameter.containsKey("pre_search")) {
                    
                    //이전 검색
                    if (remember == null) {
                        //이전 검색 못해
                    } else {
//                        if(remember.getStyle()==null){
//                            searchtask = new SearchProduct();
//                            searchtask.execute("SearchOne", remember.getCategory());
//                        }else if(remember.getColor()==null){
//                            searchtask = new SearchProduct();
//                            searchtask.execute("SearchTwo", remember.getCategory(), remember.getStyle());
//                        }else{
//                            searchtask = new SearchProduct();
//                            searchtask.execute("SearchThree", remember.getCategory(),remember.getStyle(),remember.getColor());
//                        }
                        startActivity(shopIntent);
                    }
                    chatMessage2 = new ChatMessage("버튼",true);
                    chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                    adapter.setButton(btnSendListener); //버튼이름 설정
                }
                else if(parameter.containsKey("Search")){ //메뉴선택 검색 선택시 카테고리 버튼 보여주기
                    chatMessage2 = new ChatMessage("버튼",true);
                    chatMessage2.setButton(BTN_TYPE_CATEGORY); //버튼으로 설정
                    adapter.setButton(btnSendListener); //버튼리스터 설정
                }
                else if(parameter.containsKey("Share")){
                    chatMessage2 = new ChatMessage("버튼",true);
                    chatMessage2.setButton(BTN_TYPE_SHARE); //버튼으로 설정
                    adapter.setButton(btnSendListener); //버튼리스터 설정
                }
                else if(parameter.containsKey("User")){
                    chatMessage2 = new ChatMessage("버튼",true);
                    chatMessage2.setButton(BTN_TYPE_USERINFO); //버튼으로 설정
                    adapter.setButton(btnSendListener); //버튼리스터 설정
                }
                break;
            case "Share_m":
                shareType="msg";
                break;
            case "Share_k":
                shareType="kakao";
                break;
            case "Share_m-product": //메세지로 공유할 상품
                parameter=getParameter(result);

                mProduct=parameter.get("any").toString().replaceAll("\"","");
                Log.d("share mProduct",mProduct);
                break;
            case "Share_m-person": //메세지 공유할 사람 또는 번호
                parameter=getParameter(result);
                String param=parameter.get("any").toString().replaceAll("\"","");
                if(!isStringDouble(param)){ //이름이면
                    mPerson=param;
                    mNumber=findNum(mPerson);
                    Log.d("뭘까_이름",mPerson);
                }else{ //번호면
                    mNumber="010-"+param.substring(param.length()-8, param.length()-4)+"-"+param.substring(param.length()-4, param.length());
                    Log.d("뭘까_번호",mNumber);
                }
                Log.d("share msg",param);

                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정

                //문자 공유 시작

                sendMSG(mNumber,"이 상품 구매 부탁드립니다!!");
                sendMSG(mNumber,smsg);
                sendMSG(mNumber,"주소: "+user.getAddress());
                //공유 끝나면 변수 초기화
                mPerson=null;
                mProduct=null;
                mNumber=null;
                shareType=null;
                break;

            case "Share_k-product": //카카오 공유할 상품
                parameter=getParameter(result);
                kProduct=parameter.get("any").toString().replaceAll("\"","");
                Log.d("share mProduct",kProduct);

                chatMessage2 = new ChatMessage("버튼",true);
                chatMessage2.setButton(BTN_TYPE_MENU); //버튼으로 설정
                adapter.setButton(btnSendListener); //버튼리스터 설정

                //카톡 공유 시작
                ShareKakao();
                kProduct=null;
                shareType=null;


                break;

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
        if(!query.contains("다시")) {
            chatMessages.add(chatMessage);
            for (int i = 0; i < chatMessages.size(); i++) {
                Log.i("메세지순서", chatMessages.get(i).getContent());
            }
            adapter.notifyDataSetChanged();
            editText.setText("");
        }


//            ChatMessage chatMessage3 = new ChatMessage(true, true, searched_products);
//            chatMessages.add(chatMessage3);


//        chatMessage = new ChatMessage(speech, true);
//
        Log.d("대답",speech);
//        tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
//        tts.speak(speech,TextToSpeech.QUEUE_FLUSH, null);


        if(!speech.equals("")){
//            chatMessages.add(chatMessage);
            if(!(chatMessage2.getContent().equals(""))){
                handler.postDelayed(new Runnable() {
//                    pDialog.dismiss();
                    @Override
                    public void run() {
                        ChatMessage chatMessage = new ChatMessage(speech, true);
                        chatMessages.add(chatMessage);
                        chatMessages.add(chatMessage2);
                        adapter.notifyDataSetChanged();
                    }
                }, 500);
//                chatMessages.add(chatMessage2);
            }
            else{
                chatMessage = new ChatMessage(speech, true);
                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
            }
            tts.speak(speech,TextToSpeech.QUEUE_FLUSH, null);
        }
//        if(remenu!=""){
//            chatMessage = new ChatMessage(remenu, true);
//            chatMessages.add(chatMessage);
//            adapter.notifyDataSetChanged();
//            tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
//            remenu="";
//        }
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
        Intent homeIntent=new Intent(this,ChatbotActivity.class);
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
                        .setMessage("")
                        .setPositiveButton("닫기", null)
                        .setCancelable(true)
                        .show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
    //상품 검색 class
    private class SearchProduct extends AsyncTask<String, Void,String> {
        String LoadData;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ChatbotActivity.this);
            pDialog.setMessage("검색중입니다..");
            pDialog.setCancelable(false);
//            pDialog.show();
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
                Log.i("검색스타일",style);
            }else if(params.length==4){
                style = (String) params[2];
                color = (String) params[3];
                Log.i("검색스타일",style);
                Log.i("검색색상",color);
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
                if(project.equals("SearchOne2")){
                    postParameters.add(new BasicNameValuePair("category", category));
                }else if(project.equals("SearchTwo2")){
                    postParameters.add(new BasicNameValuePair("category", category));
                    postParameters.add(new BasicNameValuePair("style", style));
                }else if(project.equals("SearchThree2")){
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
                    Log.i("chat가져온 데이터", LoadData);
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
                        Log.d("chatBotActivity검색"," 실패");
                        ChatMessage chatMessage3 = new ChatMessage("검색 결과가 없습니다.",true);
                        chatMessages.add(chatMessage3);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Log.i("chatBotActivity검색","성공"+result);
                        ArrayList<Product> searched_products=new ArrayList<Product>(); //검색된 상품들, 버튼으로 띄울 애덜
                        int pNum=0;
                        if(jArray.length()>=5) pNum=5;
                        else pNum=jArray.length();
                        for (int i = 0; i < pNum; i++) {
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
                            String realcolor=row.getString("REALCOLOR");
                            String sizeTable=row.getString("SIZE_TABLE");

                            Product product=new Product();
                            product.setName(name);
                            product.setId(id);
                            product.setCategory(category);
                            product.setColor(color);
                            product.setSize(size);
                            product.setImage(image);
                            product.setStyle(style);
                            product.setPrice(price);
                            product.setReal_color(realcolor);
                            product.setSize_table(sizeTable);
                            searched_products.add(product);

                            Log.i("chat가져온 데이터ㄹㄹ", product.toString());
                        }

                        ChatMessage chatMessage3 = new ChatMessage(true, true, searched_products);
                        chatMessages.add(chatMessage3);
                        Log.i("상품개수",""+pNum);
//                        chatMessage3.getImage().get(0);
//                        for(int i=0;i<chatMessage3.getImage().size();i++) {
//                            Log.i("김지선1", chatMessage3.getImage().get(i));
//                        }
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    Log.d("검색 오류 : ", e.getMessage());
                }

            }
        }


    }

    //관심상품 등록 확인 클래스
    private class CheckShareProduct extends AsyncTask<String, Void,String> {
        String LoadData;
        String input;
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
            input=alias;
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
                postParameters.add(new BasicNameValuePair("alias", alias));

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
                Log.d("wishList","아님 ");
//                check=1;
                ChatMessage chatMessage;
                chatMessage = new ChatMessage(input, false);

                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
                editText.setText("");
                if(shareType.equals("msg")){
                    input="문자상품다시";
                }else{
                    input="카톡상품다시";
                }


            }
            else {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    // json객체.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("getData");
//                    items = new ArrayList<Product>();
                    if(jArray.length()==0){
                        Log.d("wishList","아님");
                        ChatMessage chatMessage;
                        chatMessage = new ChatMessage(input, false);

                        chatMessages.add(chatMessage);
                        adapter.notifyDataSetChanged();
                        editText.setText("");
                        if(shareType.equals("msg")){
                            input="문자상품다시";
                        }else{
                            input="카톡상품다시";
                        }
                    }else {
                        Log.d("wishList","맞음");
                        JSONObject item = jArray.getJSONObject(0);
                        String productId = item.getString("ID");
                        smsg="https://store.musinsa.com/app/product/detail/"+productId;
                        Log.d("smsg",smsg);
//                        ChatMessage chatMessage;
//                        chatMessage = new ChatMessage(input, false);
//
//                        chatMessages.add(chatMessage);
//                        adapter.notifyDataSetChanged();
//                        editText.setText("");

                    }
                    aiRequest.setQuery(input);
                    Log.e("입력", input);
                    new AITask().execute(aiRequest);
                } catch (JSONException e) {
                    Log.d("error : ", e.getMessage());
                }
            }
        }
    }
}

class MessageAdapter extends ArrayAdapter<ChatMessage> { //메세지어댑터

    //챗봇 메뉴 버튼
    ArrayList<String> btnNamesMenu= new ArrayList<String>(
            Arrays.asList("상품 검색하기","이전 검색 다시보기","관심 상품 보기","관심 상품 공유하기","사용자 정보 수정")
    );
    ArrayList<String> btnNamesCategory= new ArrayList<String>(
            Arrays.asList("상의","바지","스커트","원피스","아우터")
    );
    ArrayList<String> btnNamesStyleTop= new ArrayList<String>(
            Arrays.asList("상관 없음","반팔 티셔츠","긴팔 티셔츠","민소매 티셔츠","셔츠/블라우스","피케/카라 티셔츠","맨투맨/스웨트셔츠","후드 스웨트셔츠/후드집업","니트/스웨터/카디건","베스트")
    );
    ArrayList<String> btnNamesStylePants= new ArrayList<String>(
            Arrays.asList("상관 없음","데님 팬츠","코튼 팬츠","수트 팬츠/슬랙스","트레이닝/조거 팬츠","숏 팬츠","레깅스")
    );
    ArrayList<String> btnNamesStyleSkirt= new ArrayList<String>(
            Arrays.asList("상관 없음","미니 스커트","롱 스커트")
    );
    ArrayList<String> btnNamesStyleDress= new ArrayList<String>(
            Arrays.asList("상관 없음","미니 원피스","맥시 원피스","점프수트","관심 상품 공유하기","사용자 정보 수정")
    );
    ArrayList<String> btnNamesStyleOuter= new ArrayList<String>(
            Arrays.asList("상관 없음","항공 점퍼","레더/라이더스 재킷","트러커 재킷","수트/블레이저 재킷","나일론/코치/아노락 재킷",
                    "스타디움 재킷","환절기 코트","겨울 싱글 코트","롱 패딩/롱 헤비 아우터","숏 패딩/숏 헤비 아우터")
    );
    ArrayList<String> btnNamesColor= new ArrayList<String>(
            Arrays.asList("상관 없음","그레이","그린","네이비","레드","민트","베이지","브라운","블랙","블루","소라","아이보리","옐로우","오렌지","차콜","카키","퍼플","핑크")
    );
    ArrayList<String> btnNamesShare= new ArrayList<String>(
            Arrays.asList("문자","카카오톡")
    );
    ArrayList<String> btnNamesUserInfo= new ArrayList<String>(
            Arrays.asList("이름","핸드폰 번호","배송지 주소")
    );

    private Activity activity;
    private List<ChatMessage> messages;
    public ArrayList<String> btnNames = new ArrayList<String>(); //버튼 이름들
    public chatButton button; //생성된 버튼들
    public View.OnClickListener Listener;
    Intent productInfoIntent = new Intent(getContext(), ProductInfo.class); //상품 정보 인텐트
    Intent shopIntent=new Intent(getContext(),ShopActivity.class); //상품 더보기 인텐트


    public MessageAdapter(Activity context, int resource, List<ChatMessage> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }
    public void setButton(View.OnClickListener Listener){
        this.Listener = Listener;
    }
    public ArrayList<Button> getButton(){ //만들어진 버튼 객체 리스트 반환
        return button.getList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        // ChatMessage chatMessage = getItem(position);

        ChatMessage chatMessage = messages.get(position);
        int viewType = getItemViewType(position);
        if(viewType==0){
            layoutResource = R.layout.item_chat_left;
            Log.d("챗",layoutResource+":"+position+chatMessage.getContent().toString()+"왼");
        }
        else if(viewType==1){
            layoutResource = R.layout.item_chat_right;
             Log.d("챗",layoutResource+":"+position+chatMessage.getContent().toString()+"오");
        }
        else if(viewType==12){
            layoutResource = R.layout.item_chat_product;
            Log.d("챗",layoutResource+":"+position+chatMessage.getContent().toString()+"상품");
        }
        else{
            layoutResource = R.layout.item_chat_button;
            Log.d("챗",layoutResource+":"+position+"버튼");
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }
        else if(layoutResource==R.layout.item_chat_button){
            convertView = inflater.inflate(layoutResource, parent, false);
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.scrollViewLayout);
            switch (viewType){ //버튼 종류 정해주기
                case 2:
                    btnNames=btnNamesMenu;
                    break;
                case 3:
                    btnNames=btnNamesCategory;
                    break;
                case 4:
                    btnNames=btnNamesStyleTop;
                    break;
                case 5:
                    btnNames=btnNamesStylePants;
                    break;
                case 6:
                    btnNames=btnNamesStyleSkirt;
                    break;
                case 7:
                    btnNames=btnNamesStyleDress;
                    break;
                case 8:
                    btnNames=btnNamesStyleOuter;
                    break;
                case 9:
                    btnNames=btnNamesColor;
                    break;
                case 10:
                    btnNames=btnNamesShare;
                    break;
                case 11:
                    btnNames = btnNamesUserInfo;
                    break;
            }
            button = new chatButton(btnNames,linearLayout,activity,Listener);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if(layoutResource==R.layout.item_chat_button){}
        else if(layoutResource==R.layout.item_chat_right || layoutResource ==R.layout.item_chat_left){
            holder.msg.setText(chatMessage.getContent());
            holder.msg.setContentDescription(messages.get(position) + "");
        }
        else if (layoutResource==R.layout.item_chat_product){
            ArrayList<Product> p = chatMessage.getProducts();
            for(int i=0;i<p.size();i++){
                holder.imageViews.get(i).setVisibility(View.VISIBLE);
                Log.i("김지선이미지"+position,p.get(i).getImage());
                String image = p.get(i).getImage();
                String pName = p.get(i).getName();
                String pId = ""+p.get(i).getId();
                String pInofo = p.get(i).toString();
                String pSize = p.get(i).getSize();
                String pSizeTable=p.get(i).getSize_table();

                Glide.with(MessageAdapter.super.getContext()).load(image).into(holder.imageViews.get(i));
                holder.imageViews.get(i).setContentDescription("상품명:"+p.get(i).getName()+"\n가격:"+p.get(i).getPrice()+"원");
                holder.imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productInfoIntent.putExtra("image",image);
                        productInfoIntent.putExtra("productId",pId);
                        productInfoIntent.putExtra("info",pInofo);
                        productInfoIntent.putExtra("size",pSize);
                        productInfoIntent.putExtra("sizeTable",pSizeTable);
                        activity.startActivity(productInfoIntent);
                    }
                });
                holder.product_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivity(shopIntent);
                    }
                });
            }
            if(p.size()<5){
                for(int i=p.size();i<5;i++){
                    holder.imageViews.get(i).setVisibility(View.GONE);
                }
            }
        }


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
        return 13;
    }
    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        ChatMessage chatMessage = messages.get(position);
        if(chatMessage.isMine() && !chatMessage.isButton() &&!chatMessage.isProduct()) return 0; //챗 왼쪽 타입
        else if(!chatMessage.isMine() && !chatMessage.isButton() &&!chatMessage.isProduct()) return 1;  //챗 오른쪽 타입
        else if(chatMessage.isButtonType()==1) return 2; //챗 버튼 메뉴 타입
        else if(chatMessage.isButtonType()==2) return 3; //챗 버튼 카테고리 타입
        else if(chatMessage.isButtonType()==3) return 4; //챗 버튼 상의스타일 타입
        else if(chatMessage.isButtonType()==4) return 5; //챗 버튼 바지스타일 타입
        else if(chatMessage.isButtonType()==5) return 6; //챗 버튼 치마스타일 타입
        else if(chatMessage.isButtonType()==6) return 7; //챗 버튼 드레스스타일 타입
        else if(chatMessage.isButtonType()==7) return 8; //챗 버튼 아우터스타일타입
        else if(chatMessage.isButtonType()==8) return 9; //챗 버튼 색상 타입
        else if(chatMessage.isButtonType()==9) return 10; //챗 버튼 공유 타입
        else if(chatMessage.isButtonType()==10) return 11; //챗 버튼 사용자정보 수정 타입
        else return 12; //상품 이미지 타입
    }
    private class ViewHolder {
        private TextView msg;
        private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
        private Button product_more;
        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
            imageViews.add((ImageView) v.findViewById(R.id.product_img1));
            imageViews.add((ImageView) v.findViewById(R.id.product_img2));
            imageViews.add((ImageView) v.findViewById(R.id.product_img3));
            imageViews.add((ImageView) v.findViewById(R.id.product_img4));
            imageViews.add((ImageView) v.findViewById(R.id.product_img5));
            product_more=(Button)v.findViewById(R.id.product_more);

        }
    }
}






