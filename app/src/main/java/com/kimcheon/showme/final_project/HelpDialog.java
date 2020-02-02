package com.kimcheon.showme.final_project;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class HelpDialog extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.dialog_help;
    private DialogListener listener;
    private Context context;
    private EditText wishProductName;
    private Button btn_close;
    private LinearLayout layout_contents;


    private String name;

    //stt editText 설정
    public void setEditText(String wishName){
        wishProductName.setText(wishName);
    }
    public HelpDialog(Context context) {
        super(context);
        if(context instanceof Activity){
            setOwnerActivity((Activity) context);
        }

    }
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(

            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    public void addHelpContents(String[] contents){
        layout_contents.removeAllViews();
        if (contents.length <= 0||contents==null) {
            //
        } else {

            //레이아웃파라미터 생성
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            pm.setMargins(2,0,2,0);

            for (String content : contents) {
                TextView txt_content = new TextView(this.getContext());

                txt_content.setText(content);
                txt_content.setPadding(10,20,10,10);


                pm.setMargins(1,0,1,0);
                txt_content.setLayoutParams(pm);

                txt_content.setLayoutParams(params);


                // view add
                layout_contents.addView(txt_content);

            }
        }
    }
    public HelpDialog(Context context, String name){
        super(context);
        this.context = context;
        this.name = name;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        wishProductName=(EditText)findViewById(R.id.wishProductName);
        btn_close = findViewById(R.id.btn_close);
        layout_contents = findViewById(R.id.layout_contents);
        btn_close.setOnClickListener(this);
        btn_close.setBackgroundResource(R.drawable.btn_yellow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_btn_stt:
                
                break;
            case R.id.cancel:
                setEditText("");
                cancel();
                break;
            case R.id.btn_close:
                dismiss();
                break;

        }
    }
    public void setDialogListener(DialogListener listener){
        this.listener=listener;

    }

}
