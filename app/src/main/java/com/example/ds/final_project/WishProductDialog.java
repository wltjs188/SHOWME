package com.example.ds.final_project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WishProductDialog extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.wish_product_dialog;
    private DialogListener listener;
    private Context context;
    private EditText wishProductName;
    private Button cancel;
    private Button ok;



    private String name;

    public WishProductDialog(Context context) {
        super(context);
        this.context = context;
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
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                cancel();
                break;
            case R.id.ok:
                if(wishProductName==null){
                    Toast.makeText(context,"이름을 입력해주세요.",Toast.LENGTH_LONG).show();
                }else{
                    listener.onPositiveClicked(wishProductName.getText().toString());
                    Log.i("관심1",wishProductName.getText().toString());

                    dismiss();
                }break;

        }
    }
    public void setDialogListener(DialogListener listener){
        this.listener=listener;
    }
}
