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
            btn.setBackgroundResource(R.drawable.btn_chat);
            btn.setTextSize(16);

            //레이아웃파라미터 생성
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            pm.setMargins(1,0,1,0);

            btn.setLayoutParams(pm);

            Log.d("dd",getBtnNames().toString());
            if(btnNames.get(i).equals("상품 검색하기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                btn.setBackgroundResource(R.drawable.product_search);
            }
            else if(btnNames.get(i).equals("이전 검색 다시보기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                btn.setBackgroundResource(R.drawable.previous);
            }
            else if(btnNames.get(i).equals("관심 상품 보기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                btn.setBackgroundResource(R.drawable.interested_product);
            }
            else if(btnNames.get(i).equals("관심 상품 공유하기")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                btn.setBackgroundResource(R.drawable.share);
            }
            else if(btnNames.get(i).equals("사용자 정보 수정")){
                btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                btn.setBackgroundResource(R.drawable.user_modification);
            }
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
