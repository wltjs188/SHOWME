package com.example.ds.final_project;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import ai.api.model.Result;



public class searchActivity extends AppCompatActivity implements AIListener{

    //서버
    String IP_ADDRESS = "35.243.72.245";
    String TAG = "phptest";
    //키워드
    String keyword="";
    String sub;

    //상품검색
    AIService aiService;
    String query;
    String action;
    String speech;
    //사용자정보 해쉬맵
    Map<String,String> MyInfo = new HashMap<String,String>();
    //사용자정보수정 해쉬맵
    Map<String,String> MyInfoModi = new HashMap<String,String>();

    AIRequest aiRequest;
    AIDataService aiDataService;

    private ListView listView;
    private View btnSend;
    private EditText editText;
    boolean isMine;
    static private List<ChatMessage> chatMessages = new ArrayList<>();
    private ArrayAdapter<ChatMessage> adapter;

    //사용자 정보
    private String uuid;
    private String name;
    private String gender;
    private String height;
    private String top;
    private String bottom;
    private String foot;

    //
    String clothes[]={"Outer","Dress","Pants","Shoes","Skirt","Swimsuit","Top","Uderwear"};

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
        shopIntent=new Intent(getApplicationContext(),ShopActivity.class);



        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        editText = (EditText) findViewById(R.id.msg_type);
        uuid = getPreferences("uuid");
        name = getPreferences("name");
        gender = getPreferences("gender");
        height = getPreferences("height");
        top = getPreferences("top");
        bottom = getPreferences("bottom");
        foot = getPreferences("foot");

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
        listView.setAdapter(adapter);
        ChatMessage chatMessage;



        Log.d("받아온 사용자 정보",uuid+","+name+","+gender+","+height+","+top+","+bottom+","+foot);
        if(name==""){
           // Log.d("야",chatMessages.size()+"");
            if(chatMessages.size()==0){
                chatMessage = new ChatMessage("안녕하세요. 쇼움이입니다~ 쇼움이를 이용하시려면 사용자 정보를 입력하셔야합니다. 사용자 정보를 입력하시겠습니까?", true);
                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
            }else if(chatMessages.get(chatMessages.size()-1).isMine()==false){
                chatMessage = new ChatMessage("안녕하세요. 쇼움이입니다~ 쇼움이를 이용하시려면 사용자 정보를 입력하셔야합니다. 사용자 정보를 입력하시겠습니까?", true);
                chatMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
            }
        }

        if(chatMessages.size()==0){
            chatMessage = new ChatMessage("메뉴를 선택해주세요\n" +
                    "1. 상품검색\n" +
                    "2. 사용자 정보 수정\n" +
                    "3. 관심상품보기", true);
            chatMessages.add(chatMessage);
            adapter.notifyDataSetChanged();
        }else if(chatMessages.get(chatMessages.size()-1).isMine()==false){
            chatMessage = new ChatMessage("메뉴를 선택해주세요\n" +
                    "1. 상품검색\n" +
                    "2. 사용자 정보 수정\n" +
                    "3. 관심상품보기", true);
            chatMessages.add(chatMessage);
            adapter.notifyDataSetChanged();
        }

        //dialogflow
        final AIConfiguration config = new AIConfiguration("b8dda671eb584e3586aba41efdd554cf",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        aiDataService = new AIDataService(this,config);
        aiRequest = new AIRequest();

        //aiRequest.setEvent(new AIEvent("welcome"));
        //new AITask().execute(aiRequest);

        //전송버튼
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(searchActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    isMine=false;
                    aiRequest.setQuery(editText.getText().toString());
                    Log.e("입력",editText.getText().toString());
                    new AITask().execute(aiRequest);
                    //서버 입력
//                    InsertData task = new InsertData();
//                    task.execute("http://" + IP_ADDRESS + "/insert.php","김채윤","여성","158","44","44","230");
                }
            }
        });
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
    public void ButtonClicked(View view){
        aiService.startListening();

    }
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

    public void onResult(AIResponse response) {
        final Result result = response.getResult();
        String parameterString = "";
        ACTION=result.getAction();
        int i=0;
        if (result.getParameters() != null && !result.getParameters().isEmpty() && result.getParameters().size()==6&&ACTION=="user") {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                MyInfo.put(entry.getKey(),""+entry.getValue());
            }
            String name=(MyInfo.get("name")).replaceAll("\"","");
            String gender=(MyInfo.get("Gender_Info")).replaceAll("\"","");
            String height=(MyInfo.get("height")).replaceAll("\"","");
            String top=(MyInfo.get("top")).replaceAll("\"","");
            String bottom=(MyInfo.get("bottom")).replaceAll("\"","");
            String shoes=(MyInfo.get("shoes")).replaceAll("\"","");

            //사용자 정보 DB에 넣기
            InsertData task = new InsertData();
            task.execute("http://" + IP_ADDRESS + "/insert.php",uuid,name,gender,height,top,bottom,shoes);
        }
        UpdateData task = new UpdateData();

        //액션
        switch (ACTION) {
            case "name_modi":
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    Log.d("사용자정보수정","key"+entry.getKey()+"value:"+entry.getValue());
                    task.execute("http://" + IP_ADDRESS + "/update.php",uuid,"name",(""+entry.getValue()).replaceAll("\"",""));
                }
                break;
            case "gender_modi":
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    Log.d("사용자정보수정","key"+entry.getKey()+"value:"+entry.getValue());
                    task.execute("http://" + IP_ADDRESS + "/update.php",uuid,"gender",(""+entry.getValue()).replaceAll("\"",""));
                }
                break;
            case "height_modi":
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    MyInfoModi.put(entry.getKey(),""+entry.getValue());
                    Log.d("사용자정보수정","key"+entry.getKey()+"value:"+entry.getValue());
                    task.execute("http://" + IP_ADDRESS + "/update.php",uuid,"height",(""+entry.getValue()).replaceAll("\"",""));
                }
                break;
            case "top_modi":
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    Log.d("사용자정보수정","key"+entry.getKey()+"value:"+entry.getValue());
                    task.execute("http://" + IP_ADDRESS + "/update.php",uuid,"top",(""+entry.getValue()).replaceAll("\"",""));
                }
                break;
            case "bottom_modi":
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    Log.d("사용자정보수정","key"+entry.getKey()+"value:"+entry.getValue());
                    task.execute("http://" + IP_ADDRESS + "/update.php",uuid,"bottom",(""+entry.getValue()).replaceAll("\"",""));
                }
                break;
            case "shoes_modi":
                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                    Log.d("사용자정보수정","key"+entry.getKey()+"value:"+entry.getValue());
                    task.execute("http://" + IP_ADDRESS + "/update.php",uuid,"foot",(""+entry.getValue()).replaceAll("\"",""));
                }
                break;
            case "search" :
                if(result.getParameters() != null && !result.getParameters().isEmpty() && result.getParameters().size()==6) {
                    for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
//                    Log.d("겟값","key"+entry.getKey()+"value:"+entry.getValue());
                        String key=entry.getKey();
                        boolean inKey=false;
                        for(int j=0;i<clothes.length;i++){
                            inKey=inKey||(key.equals(clothes[j]));
                        }
                        if ((sub = "" + entry.getValue()) != null && inKey || key == "Color") {
                            keyword += sub;
                            Log.i("키워드", keyword);
                        }
                    }

                    keyword.replaceAll("\"", "");
                    shopIntent.putExtra("keyword", keyword);
                    startActivity(shopIntent);
                }
                break;

            default:
                break;

        }


        speech = result.getFulfillment().getSpeech();
        query=result.getResolvedQuery();
        action=result.getAction();
       // Log.e("액션",action);
        ChatMessage chatMessage;
        chatMessage = new ChatMessage(query, isMine);
        chatMessages.add(chatMessage);
        adapter.notifyDataSetChanged();
        editText.setText("");
        isMine=true;
        chatMessage = new ChatMessage(speech, isMine);
        chatMessages.add(chatMessage);
        adapter.notifyDataSetChanged();

        if(speech.toString().equals("관심상품 보기 로 이동합니다.")){
            startActivity(wishIntent);
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
    //사용자 정보 수정 서버
    class UpdateData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

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
            Log.d(TAG, "aaaaaaaaaaaa" + result);
        }
        @Override
        protected String doInBackground(String... params) {

            String uuid = (String)params[1];
            String infoName = (String)params[2];
            String value = (String)params[3];



            String serverURL = (String)params[0];
            String postParameters = "uuid=" + uuid + "&infoName=" + infoName + "&value=" + value;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

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
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "UpdateData: Error ", e);
                Log.d("에러",e.getMessage());
                return new String("Error: " + e.getMessage());
            }
        }
    }
    //서버 입력 클래스
    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

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
            Log.d(TAG, "POST response  - " + result);
        }
        @Override
        protected String doInBackground(String... params) {

            String uuid = (String)params[1];
            String name = (String)params[2];
            String gender = (String)params[3];
            String height = (String)params[4];
            String top = (String)params[5];
            String bottom = (String)params[6];
            String foot = (String)params[7];

            String serverURL = (String)params[0];
            String postParameters = "uuid=" + uuid + "&name=" + name + "&gender=" + gender+ "&height=" + height+ "&top=" + top+ "&bottom=" + bottom+ "&foot=" + foot;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

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
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
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
        ChatMessage chatMessage = getItem(position);
        if (chatMessage.isMine()) {
            layoutResource = R.layout.item_chat_left;
        } else {
            layoutResource = R.layout.item_chat_right;
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.msg.setText(chatMessage.getContent());

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
        return position % 2;
    }
    private class ViewHolder {
        private TextView msg;
        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
        }
    }
}





