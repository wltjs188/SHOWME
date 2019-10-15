package com.example.ds.final_project;
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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ds.final_project.db.InsertUser;
import com.example.ds.final_project.db.UpdateUser;
import com.google.gson.JsonElement;
import android.widget.Button;

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
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;

import static android.speech.tts.TextToSpeech.ERROR;

public class searchActivity extends AppCompatActivity implements AIListener{
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
    ResponseMessage.ResponseSpeech responseMessageFirst;
    ResponseMessage.ResponseSpeech responseMessageSecond;

    private ListView listView;
    private View btnSend;
    private View btnSST;
    private EditText editText;
    //boolean isMine;
    static private List<ChatMessage> chatMessages = new ArrayList<>();
    private ArrayAdapter<ChatMessage> adapter;//= new MessageAdapter(this, 0, chatMessages);

    //사용자 정보
    HashMap<String,JsonElement> parameter=new HashMap<String,JsonElement>();
    private String user_uuid;
    private String user_name=null;
    private String user_phone=null;
    private String user_address=null;
    private TextToSpeech tts;

    //stt
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //검색 정보
    String category = null;
    String color = null;
    String length = null;
    String size = null;
    String pattern = null;
    //    String fabric = null;
    private String mJsonString;
    String fname= null; //공유할 사람 이름
    String fnumber=null; //공유할 사람 번호
    String smsg="이 상품 구매 부탁드립니다!!";//공유할 메세지 내용
    String sproduct= null; //공유할 관심상품
    ArrayList<String> wishProductNames;
//    private String gender;
//    private String height;
//    private String top;
//    private String bottom;
//    private String foot;

    Intent wishIntent,shopIntent;
    //챗봇 액션
    String ACTION="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("쇼움이");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) { makeRequest(); }
        btn_chat_send=(Button)findViewById(R.id.btn_chat_send);
        wishIntent=new Intent(getApplicationContext(),WishListActivity.class);//나의관심상품
        shopIntent=new Intent(getApplicationContext(),ShopActivity.class); //상품검색


        wishProductNames=new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        btnSST=findViewById(R.id.btn_stt);
        editText = (EditText) findViewById(R.id.msg_type);

        user_uuid = getPreferences("uuid");
        //user_uuid = "ffffffff-e523-2a50-576f-dd2f1aeb1b07";
        user_name = getPreferences("name");
        user_address = getPreferences("address");
        user_phone = getPreferences("phoneNum");
        Log.d("uuid 정보",user_name+user_address+user_phone);
//        gender = getPreferences("gender");
//        height = getPreferences("height");
//        top = getPreferences("top");
//        bottom = getPreferences("bottom");
//        foot = getPreferences("foot");


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);

                }
            }
        });

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount() - 1);
        ChatMessage chatMessage;



        //dialogflow
        final AIConfiguration config = new AIConfiguration("b8dda671eb584e3586aba41efdd554cf",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this,config);
        aiRequest = new AIRequest();


        //전송버튼
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(searchActivity.this, "텍스트를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
//                else if(editText.getText().toString().length() !=8 && chatMessages.get(chatMessages.size()-1).toString().contains("번호")){
//                    Toast.makeText(getApplicationContext(),"010을 제외한 8자리 번호를 입력해주세요.",Toast.LENGTH_LONG).show();
//                }
                else {
                    aiRequest.setQuery(editText.getText().toString());
                    Log.e("입력",editText.getText().toString());
                    new AITask().execute(aiRequest);

                }
            }
        });
        //SST 버튼
        btnSST.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }
    //stt
    //주소록에서 번호 가져오기
    String findNum(String fname){
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
        tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
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
            //전송
            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(number, null, msg, null, null);
            smsManager.sendTextMessage(number, null, msg, null, null);

            Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
            Log.d("메세지 오류",e.getMessage());
            e.printStackTrace();
        }
    }
    //관심상품 이름으로 검색하고 문자보냄
    private class GetProductToShare extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(searchActivity.this,
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
                showResultGetProductToShare();
                fname = null; sproduct = null; fnumber = null;
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
                    smsg+="\nhttp://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo="+productId+"\n옵션번호 : "+optionNum;
                }
                Log.d("메세지:",smsg+"\n번호:"+fnumber);
                sendMSG(fnumber,smsg);

            }else {
                //관심상품 sproduct 존재 안함
                Toast.makeText(this,sproduct+" 없어용",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            //관심상품 sproduct 존재 안함
            Log.d("showResult : ", e.getMessage());
            Log.d("showResult : ", mJsonString);
            Toast.makeText(this,sproduct+" 없어용",Toast.LENGTH_LONG).show();
        }

    }
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
    //stt
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
        ChatMessage chatMessage = new ChatMessage("메뉴를 선택해주세요\n" +
                "1. 상품검색\n" +
                "2. 사용자 정보 수정\n" +
                "3. 관심상품보기\n"+
                "4. 관심상품 공유하기", true);
        chatMessages.add(chatMessage);

        //TTS 챗봇 읽어주기
        tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
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
                final AIResponse response = aiDataService.request(aiRequest);
                return response;
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("채팅사이즈",""+chatMessages.size());
        if(chatMessages.size()==0){
            if(user_name==""){
                ChatMessage chatMessage = new ChatMessage("안녕하세요. 쇼움이입니다 쇼움이를 이용하시려면 사용자 정보를 입력하셔야합니다. 사용자 정보를 입력하시겠습니까?", true);
                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
                    }
                }, 1000);
            }
            else{
                makeMenuMsg();
            }
        }
//        else  if(chatMessages.get(chatMessages.size()-1).getContent().contains("관심")||chatMessages.get(chatMessages.size()-1).getContent().contains("검색")){
//            makeMenuMsg();
//        }


    }



    public void onResult(AIResponse response) {
        final Result result = response.getResult();


        ACTION=result.getAction();


        Log.i("액션",ACTION);
        Log.i("RESULT",""+result);
        //챗봇 액션 처리
        switch (ACTION){
            case "ACTION_USER"://사용자등록 : 이름받아오기
                parameter=getParameter(result);
                //이름
                if(parameter.containsKey("user_name")){
                    user_name=""+parameter.get("user_name");
                    user_name=user_name.substring(9);
                    user_name=user_name.substring(0,user_name.length()-2);
                }
                //핸드폰 번호
                if(parameter.containsKey("user_phone")){
                    user_phone=""+parameter.get("user_phone");
                    user_phone=(user_phone.replaceAll(" ","")).replaceAll("-","");

                }
                //주소 시,구,동
                if(parameter.containsKey("city")) {
                    if(parameter.containsKey("state")){ //도
                        user_address = "" + parameter.get("state");
                    }
                    user_address = user_address + parameter.get("city"); //시
                    if(parameter.containsKey("county")){ //구,군
                        user_address=user_address+parameter.get("county");
                    }
                    if(parameter.containsKey("county1")){ //면,읍,리
                        user_address=user_address+parameter.get("county1");
                    }
                    if(parameter.containsKey("village")){//동
                        user_address=user_address+parameter.get("village");
                    }
                    if(parameter.containsKey("address")){ //상세주소,도로명주소
                        user_address=user_address+parameter.get("address");
                    }
                    user_address=user_address.replaceAll("\"","");
                }
                //사용자 정보 DB에 넣기
                if( !user_name.equals("") && !user_phone.equals("") && !user_address.equals("") ) {
                    InsertUser task = new InsertUser();
                    //Log.d("test","이름");
                    System.out.println("이름 : "+user_name+"번호 : "+user_phone+"주소 : "+user_address);
                    task.execute("http://" + IP_ADDRESS + "/insertUser.php",user_uuid,user_name,user_address,user_phone);
                    Log.i("액션USER",ACTION);
                    remenu=getRemenu(result);
                    result.getContexts().clear();
                }
                break;
            case "ACTION_M_NAME"://사용자정보수정 : 이름
                parameter=getParameter(result);
                user_name = ""+parameter.get("user_name");
                UpdateUser task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("http://" + IP_ADDRESS + "/updateUser.php",user_uuid,"name",user_name);
                remenu=getRemenu(result);
                result.getContexts().clear();
                break;
            case "ACTION_M_PHONE"://사용자정보수정 : 핸드폰번호
                parameter=getParameter(result);
                task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("http://" + IP_ADDRESS + "/updateUser.php",user_uuid,"phoneNum",user_phone);
                remenu=getRemenu(result);
                result.getContexts().clear();
                break;
            case "ACTION_M_ADDRESS"://사용자정보수정 : 주소
                parameter=getParameter(result);
                //주소 시,구,동 받아오기
                user_address = "" + parameter.get("city")+parameter.get("county");
                if(parameter.containsKey("county1")){
                    user_address=user_address+parameter.get("county1");
                }
                user_address=user_address+parameter.get("village");
                user_address=user_address.replaceAll("\"","");
                task1 = new UpdateUser(); //사용자정보 수정
                task1.execute("http://" + IP_ADDRESS + "/updateUser.php",user_uuid,"address",user_address);
                remenu=getRemenu(result);
                result.getContexts().clear();

                break;
            case "ACTION_SEARCH": //상품검색 :
                parameter=getParameter(result);
                //검색조건(카테고리,색상,기장,사이즈,패턴,재질) 받아오기
                //카테고리
                if(parameter.containsKey("Top")){
                    category = ""+parameter.get("Top");
                }
                else if(parameter.containsKey("Dress")){
                    category = ""+parameter.get("Dress");
                }
                else if(parameter.containsKey("Outer")){
                    category = ""+parameter.get("Outer");
                }
                else if(parameter.containsKey("Pants")){
                    category = ""+parameter.get("Pants");
                }
                else if(parameter.containsKey("Shoes")){
                    category = ""+parameter.get("Shoes");
                }
                else if(parameter.containsKey("Skirt")){
                    category = ""+parameter.get("Skirt");
                }
                else if(parameter.containsKey("Swimsuit")){
                    category = ""+parameter.get("Swimsuit");
                }
                //색상
                if(parameter.containsKey("Color")){
                    color = ""+parameter.get("Color");
                }
                //기장, 바지기장
                if(parameter.containsKey("Length")){
                    length = ""+parameter.get("Length");
                }
                if(parameter.containsKey("PantsLength")){
                    length = ""+parameter.get("PantsLength");
                }
                //사이즈
                if(parameter.containsKey("Size")){
                    size = ""+parameter.get("Size");
                }
                if(parameter.containsKey("ShoesSize")){
                    size = ""+parameter.get("ShoesSize");
                }
                //패턴
                if(parameter.containsKey("Pattern")){
                    pattern = ""+parameter.get("Pattern");
                }
                //재질
//                if(parameter.containsKey("Material")){
//                    fabric = ""+parameter.get("Material");
//                }
                System.out.println("카테고리 : "+category+"색상 : "+color+"기장 : "+length+"사이즈 : "+size+"패턴 : "+pattern);

                if( category != null && color != null && length != null && size != null && pattern != null) {
                    shopIntent.putExtra("category", category);
                    shopIntent.putExtra("color", color);
                    shopIntent.putExtra("length", length);
                    shopIntent.putExtra("size", size);
                    shopIntent.putExtra("pattern", pattern);
//                    shopIntent.putExtra("fabric", fabric);

                    category = null; color = null; length = null; size = null; pattern = null;
                    result.getContexts().clear();
                    startActivity(shopIntent);
                }

                break;

            case "ACTION_MENU" :
                parameter=getParameter(result);
                if(parameter.containsKey("Wish_Item")){ //관심상품이동
                    startActivity(wishIntent);
                    result.getContexts().clear();
                }
                break;
            case "Share_Message"://공유하기
                // 관심상품에 뭐가 있는지 토스트 메세지로 알려줌
                if(sproduct==null) {
//                    if(wishProductNames==null) {
                    GetWishProductName task = new GetWishProductName();
                    task.execute("http://" + IP_ADDRESS + "/getWishProductName.php", user_uuid);
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
                }
//                Toast.makeText(this.getApplicationContext(),"관심상품에 ~~~가 있습니다.",Toast.LENGTH_LONG).show();
                parameter=getParameter(result);
                //공유할 사람
                if(parameter.containsKey("SharePerson")){
                    fname = parameter.get("SharePerson").toString();
                    fname = fname.substring(8,fname.length()-1);

                    fnumber = findNum(fname); // 공유자 이름으로 번호 찾기

                    if(!fnumber.equals("그런 사람 없어")){
                        // 연락처 조회 된 경우, 공유 실행
                        GetProductToShare task2 = new GetProductToShare();
                        task2.execute( "http://" + IP_ADDRESS+"/getProductToShare.php",user_uuid,sproduct);
                    }
                    else{
                        makeChatNoPerson(fname);
                        //Toast.makeText(getApplicationContext(), fname+"번호없음", Toast.LENGTH_LONG).show();
                    }

                }
                //공유할 상품
                if(parameter.containsKey("ShareProduct")){
                    sproduct = parameter.get("ShareProduct").toString().replace('\"',' ').trim();
                }
                Log.d("명","공유할사람:"+fname+"공유할상품"+sproduct);

                //2가지 다 입력되었다면,
//               if( fname != null && sproduct != null){
//
//                   fnumber = findNum(fname); // 공유자 이름으로 번호 찾기
////                   Log.d("먕","uuid"+user_uuid+" 번호"+fnumber+" 이름"+fname+findNum("강정현"));
//                   Log.d("메세지",fnumber);
//                   if(!fnumber.equals("그런 사람 없어")){
//                       // 연락처 조회 된 경우, 공유 실행
//                       GetProductToShare task2 = new GetProductToShare();
//                       task2.execute( "http://" + IP_ADDRESS+"/getProductToShare.php",user_uuid,sproduct);
//                   }
//                   else{
//                       Toast.makeText(getApplicationContext(), fname+"번호없음", Toast.LENGTH_LONG).show();
//                   }
//                   fname = null; sproduct = null; fnumber = null;
//                   result.getContexts().clear();
//
//                }
                break;
        }

        query=result.getResolvedQuery();
        if(ACTION.equals("ACTION_USER") || ACTION.equals("ACTION_M_NAME") || ACTION.equals("ACTION_M_PHONE") || ACTION.equals("ACTION_M_ADDRESS") ){
            responseMessageSecond = (ResponseMessage.ResponseSpeech)result.getFulfillment().getMessages().get(0);
            speech=responseMessageSecond.getSpeech().get(0);
        }
        else {
            speech = result.getFulfillment().getSpeech();
        }



        ChatMessage chatMessage;
        chatMessage = new ChatMessage(query, false);
        chatMessages.add(chatMessage);
        adapter.notifyDataSetChanged();
        editText.setText("");
        chatMessage = new ChatMessage(speech, true);
        chatMessages.add(chatMessage);
        adapter.notifyDataSetChanged();
        tts.speak(chatMessage.toString(),TextToSpeech.QUEUE_FLUSH, null);
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

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(searchActivity.this,
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
        String TAG_JSON="WishProductName";
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
                Toast.makeText(searchActivity.this, "관심상품에 "+m+"가 있습니다.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(searchActivity.this,"등록된 관심상품이 없습니다.",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.d("showResult : ", e.getMessage());
            Log.d("phptest: ",mJsonString);
            Toast.makeText(searchActivity.this,"등록된 관심상품이 없습니다.",Toast.LENGTH_LONG).show();
        }

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



    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기 버튼 실행
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                ((MainActivity)MainActivity.CONTEXT).onResume();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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





