package com.example.ds.final_project;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

    //서버
    String IP_ADDRESS = "18.191.10.193";
    String TAG = "phptest";

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

    //검색 정보
    String category = null;
    String color = null;
    String length = null;
    String size = null;
    String pattern = null;
    String fabric = null;

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

        wishIntent=new Intent(getApplicationContext(),WishListActivity.class);//나의관심상품
        shopIntent=new Intent(getApplicationContext(),ShopActivity.class); //상품검색


        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        btnSST=findViewById(R.id.btn_stt);
        editText = (EditText) findViewById(R.id.msg_type);

        user_uuid = getPreferences("uuid");
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

        //구글 sst 음성인식
        sstIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sstIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        sstIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);

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
                Toast.makeText(getApplicationContext(),"지금부터 말을 해주세요!",Toast.LENGTH_LONG).show();
                mRecognizer.startListening(sstIntent);
            }
        });

    }
    //SST 리스너
    private RecognitionListener listener = new RecognitionListener() {
        public void onRmsChanged(float rmsdB) { }
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            editText.setText(""+rs[0]);

        }
        public void onReadyForSpeech(Bundle params) {

        }
        public void onPartialResults(Bundle partialResults) {}
        public void onEvent(int eventType, Bundle params) {}
        public void onError(int error) {
            Toast.makeText(getApplicationContext(),"오류",Toast.LENGTH_LONG).show();
        }
        public void onEndOfSpeech() {}
        public void onBufferReceived(byte[] buffer) {}
        public void onBeginningOfSpeech() {
        }
    };


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
                "3. 관심상품보기", true);
        chatMessages.add(chatMessage);

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
        if(user_name==""){
            if(chatMessages.size()==0){
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
        }else{makeMenuMsg();
        }

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
                if(parameter.containsKey("Material")){
                    fabric = ""+parameter.get("Material");
                }
                System.out.println("카테고리 : "+category+"색상 : "+color+"기장 : "+length+"사이즈 : "+size+"패턴 : "+pattern+"재질 : "+fabric);

                if( category != null && color != null && length != null && size != null && pattern != null && fabric != null ) {
                    shopIntent.putExtra("category", category);
                    shopIntent.putExtra("color", color);
                    shopIntent.putExtra("length", length);
                    shopIntent.putExtra("size", size);
                    shopIntent.putExtra("pattern", pattern);
                    shopIntent.putExtra("fabric", fabric);

                    category = null; color = null; length = null; size = null; pattern = null; fabric = null;
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



    // 값 불러오기
    private String  getPreferences(String key){
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





