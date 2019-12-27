package com.example.ds.final_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class WishProductDialog extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.wish_product_dialog;
    private DialogListener listener;
    private Context context;
    private EditText wishProductName;
    private Button cancel;
    private Button ok;
    private Button dialog_btn_stt;
    private TextView dialog_tv;

    //STT
    private final int REQ_CODE_SPEECH_INPUT = 100;


    private String name;

    //stt editText 설정
    public void setEditText(String wishName){
        wishProductName.setText(wishName);
    }
    public void setEditTextHint(String wishName){
        wishProductName.setHint(wishName);
    }
    public WishProductDialog(Context context) {
        super(context);
        if(context instanceof Activity){
            setOwnerActivity((Activity) context);
        }
//        this.context = context;

    }
    public void setDialog_tv(String text){
        dialog_tv.setText(text);
    }
    public WishProductDialog(Context context,String name){
        super(context);
        this.context = context;
        this.name = name;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        wishProductName=(EditText)findViewById(R.id.wishProductName);
        cancel = (Button) findViewById(R.id.cancel);
        ok = (Button) findViewById(R.id.ok);
        dialog_btn_stt=(Button) findViewById(R.id.dialog_btn_stt);
        dialog_tv=(TextView)findViewById(R.id.dialog_tv);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        dialog_btn_stt.setOnClickListener(this);

        dialog_btn_stt.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_btn_stt:
//                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
//                i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());    //호출한 패키지
//                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
//                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");                     //사용자에게 보여 줄 글자
//                context.startActivityForResult();
//                context.startActivity(i);
                promptSpeechInput();
                
                break;
            case R.id.cancel:
                setEditText("");
                cancel();
                break;
            case R.id.ok:
                if(wishProductName==null){
                    Toast.makeText(context,"이름을 입력해주세요.",Toast.LENGTH_LONG).show();
                }else{
                    listener.onPositiveClicked(wishProductName.getText().toString());
                    Log.i("관심1",wishProductName.getText().toString());

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
            getOwnerActivity().startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context,
                    context.getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        getOwnerActivity().onActivityResult(requestCode, resultCode, data);
//        Activity ChatbotActivity = (Activity)getOwnerActivity()
//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT: {
//                if (resultCode == RESULT_OK && null != data) {
//
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    wishProductName.setText(result.get(0));
//                }
//                break;
//            }
//        }
//    }
//    public void onSTTClicked(){
//        promptSpeechInput();
//    }

    public void setDialogListener(DialogListener listener){
        this.listener=listener;

    }
    //STT 음성 입력

}
