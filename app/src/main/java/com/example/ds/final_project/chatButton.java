package com.example.ds.final_project;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.ds.final_project.ChatbotActivity;
import java.util.ArrayList;




public class chatButton {

    ArrayList<Button> list = new ArrayList<Button>();

    public chatButton(ArrayList<String> btnNames, LinearLayout layout, Context context){
        for(int i=0;i<btnNames.size();i++){
            Button btn = new Button(context);
            btn.setText(btnNames.get(i));
            btn.setWidth(400);
            btn.setHeight(400);
            layout.addView(btn);
            list.add(btn);
        }
    }

    public ArrayList<Button> getList() {
        return list;
    }
}
