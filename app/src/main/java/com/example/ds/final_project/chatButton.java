package com.example.ds.final_project;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.ds.final_project.ChatbotActivity;
import java.util.ArrayList;




public class chatButton {

    ArrayList<Button> list = new ArrayList<Button>();
    ArrayList<String> btnNames = new ArrayList<String>();

    public chatButton(ArrayList<String> btnNames, LinearLayout layout, Context context, View.OnClickListener listener){
        this.btnNames = btnNames;
        for(int i=0;i<btnNames.size();i++){
            Button btn = new Button(context);
            btn.setText(btnNames.get(i));
            if(btnNames.get(i).equals("상품 검색하기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            }
            else if(btnNames.get(i).equals("이전 검색 다시보기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            }
            else if(btnNames.get(i).equals("관심 상품 보기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            }
            else if(btnNames.get(i).equals("관심 상품 공유하기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            }
            else if(btnNames.get(i).equals("사용자 정보 수정")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            }
            btn.setTextSize(18);
            btn.setWidth(400);
            btn.setHeight(400);
            btn.setOnClickListener(listener);
            btn.setBackgroundResource(R.drawable.btn_chat);
            layout.addView(btn);
            list.add(btn);
        }
    }
    public ArrayList<String> getBtnNames(){return btnNames;}
    public ArrayList<Button> getList() {
        return list;
    }
}
