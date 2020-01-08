package com.example.ds.final_project;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.ds.final_project.ChatbotActivity;
import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;


public class chatButton {

    ArrayList<Button> list = new ArrayList<Button>();
    ArrayList<String> btnNames = new ArrayList<String>();
    Drawable roundDrawable;

//    Drawable roundDrawable;
    public chatButton(ArrayList<String> btnNames, LinearLayout layout, Context context, View.OnClickListener listener){
        this.btnNames = btnNames;
//        roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
        for(int i=0;i<btnNames.size();i++){
            Button btn = new Button(context);
            btn.setText(btnNames.get(i));
            btn.setBackgroundResource(R.drawable.btn_chat);
            btn.setTextSize(16);

            //레이아웃파라미터 생성
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            pm.setMargins(2,0,2,0);

            Log.d("dd",getBtnNames().toString());
            Log.d("dd",btnNames.get(i));
            switch(btnNames.get(i)){
                case "상품 검색하기":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.product_search);
                    pm.setMargins(1,0,1,0);
                    break;
                case "이전 검색 다시보기":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.previous);
                    pm.setMargins(1,0,1,0);
                    break;
                case "관심 상품 보기":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.interested_product);
                    pm.setMargins(1,0,1,0);
                    break;
                case "관심 상품 공유하기":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.share);
                    pm.setMargins(1,0,1,0);
                    break;
                case "사용자 정보 수정":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.user_modification);
                    pm.setMargins(1,0,1,0);
                    break;
                case "상의":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.top);
                    pm.setMargins(1,0,1,0);
                    break;
                case "바지":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.pants);
                    pm.setMargins(1,0,1,0);
                    break;
                case "스커트":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.skirt);
                    pm.setMargins(1,0,1,0);
                    break;
                case "원피스":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.dress);
                    pm.setMargins(1,0,1,0);
                    break;
                case "아우터":
                    btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    btn.setBackgroundResource(R.drawable.outer);
                    pm.setMargins(1,0,1,0);
                    break;
                    //색상버튼
                case "검은색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                    btn.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "회색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorGray), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "흰색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "빨간색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorRed), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "주황색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorOrange), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "노란색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "초록색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorGreen), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "파란색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorBlue), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "남색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorNavy), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "보라색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorPurple), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "분홍색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorPink), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "민트색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorMint), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "베이지색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorBeige), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "하늘색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorSkyblue), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "갈색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorBrown), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
                case "버건디색":
                    roundDrawable = ContextCompat.getDrawable(context,R.drawable.btn_chat);
                    roundDrawable.setColorFilter(ContextCompat.getColor(context,R.color.colorBurgundy), PorterDuff.Mode.SRC_ATOP);
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btn.setBackgroundDrawable(roundDrawable);
                    } else {
                        btn.setBackground(roundDrawable);
                    }
                    break;
            }
            btn.setLayoutParams(pm);
            btn.setWidth(400);
            btn.setHeight(400);
            btn.setOnClickListener(listener);
            layout.addView(btn);
            list.add(btn);
        }
    }
    public ArrayList<String> getBtnNames(){return btnNames;}
    public ArrayList<Button> getList() {
        return list;
    }
}
