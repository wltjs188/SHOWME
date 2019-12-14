package com.example.ds.final_project;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.ds.final_project.ChatbotActivity;
import java.util.ArrayList;




public class chatButton {

    ArrayList<Button> list = new ArrayList<Button>();

    public chatButton(ArrayList<String> btnNames, LinearLayout layout, Context context, View.OnClickListener listener){
        for(int i=0;i<btnNames.size();i++){
            Button btn = new Button(context);
            btn.setText(btnNames.get(i));
            btn.setWidth(400);
            btn.setHeight(400);
            btn.setOnClickListener(listener);
            layout.addView(btn);
            list.add(btn);
        }
    }

    public ArrayList<Button> getList() {
        return list;
    }
}
