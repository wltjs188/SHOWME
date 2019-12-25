package com.example.ds.final_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SendMsgDialog  extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.dialog_send_msg;
    private DialogListener listener;
    private Context context;
    private EditText shareTo;
    private Button cancel;
    private Button ok;
    private Button dialog_btn_stt;
    private TextView text_guide;

    //STT
    private final int REQ_CODE_SHARE_MSG_SPEECH_INPUT = 101;


    private String name;

    //stt editText 설정
    public void setEditText(String to){
        shareTo.setText(to);
    }
    public void setEditTextHint(String hint){
        shareTo.setHint(hint);
    }
    public void setText_guide(String guide){
        text_guide.setText(guide);
    }
    public SendMsgDialog(Context context) {
        super(context);
        if(context instanceof Activity){
            setOwnerActivity((Activity) context);
        }
//        this.context = context;

    }

    public SendMsgDialog(Context context,String name){
        super(context);
        this.context = context;
        this.name = name;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        shareTo=(EditText)findViewById(R.id.shareTo);
        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.ok);
        dialog_btn_stt=(Button) findViewById(R.id.dialog_btn_stt);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        dialog_btn_stt.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_btn_stt:
                promptSpeechInput();

                break;
            case R.id.cancel:
                cancel();
                break;
            case R.id.ok:
                if(shareTo==null){
                    Toast.makeText(context,"입력해 주세요.",Toast.LENGTH_LONG).show();
                }else{
                    listener.onPositiveClicked(shareTo.getText().toString());
                    Log.i("문자 공유",shareTo.getText().toString());

                    dismiss();
                }break;

        }
    }



    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요");
        try {
            getOwnerActivity().startActivityForResult(intent,REQ_CODE_SHARE_MSG_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    context.getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void setDialogListener(DialogListener listener){
        this.listener=listener;

    }
    //STT 음성 입력

}
