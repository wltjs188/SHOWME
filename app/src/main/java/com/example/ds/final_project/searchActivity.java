package com.example.ds.final_project;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;



public class searchActivity extends AppCompatActivity {
    //상품검색


    String parameterString;
    String query;
    String action;
    String answer;
    String speech;

    AIRequest aiRequest;
    AIDataService aiDataService;

    private ListView listView;
    private View btnSend;
    private EditText editText;
    boolean isMine;
    private List<ChatMessage> chatMessages;
    private ArrayAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("쇼움이");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼

        chatMessages = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        editText = (EditText) findViewById(R.id.msg_type);

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.item_chat_left, chatMessages);
        listView.setAdapter(adapter);
        //ChatMessage chatMessage = new ChatMessage("어떤 상품을 찾아 드릴까요?", true);
        //chatMessages.add(chatMessage);

        //dialogflow
        final AIConfiguration config = new AIConfiguration("c8675821bede492ea0f662d262675d29",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this,config);
        aiRequest = new AIRequest();


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
                    new AITask().execute(aiRequest);
                }
            }
        });


    }

    private class AITask extends AsyncTask<AIRequest, Void, AIResponse>{
        protected AIResponse doInBackground(AIRequest... requests) {
            final AIRequest request = requests[0];
            try {
                final AIResponse response = aiDataService.request(aiRequest);
                return response;
            } catch (AIServiceException e) {
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
        speech = result.getFulfillment().getSpeech();
        query=result.getResolvedQuery();
        action=result.getAction();
        ChatMessage chatMessage = new ChatMessage(query, isMine);
        chatMessages.add(chatMessage);
        adapter.notifyDataSetChanged();
        editText.setText("");
        isMine=true;
        chatMessage = new ChatMessage(speech, isMine);
        chatMessages.add(chatMessage);
        adapter.notifyDataSetChanged();

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
        int viewType = getItemViewType(position);

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

        //set message content
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



