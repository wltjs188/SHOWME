package com.example.ds.final_project;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;

/**
 * Created by SAMSUNG on 2019-03-19.
 */

public class ProductSearchThread extends Thread {
    private ProductSearchService service;
    private Handler handler;
    private String Color;

    public ProductSearchThread(ProductSearchService service, Handler handler){
        this.service = service;
        this.handler = handler;
    }
    public void setColor(String Color){
        this.Color=Color;
    }
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        // service의 search메소드를 수행하고 결과를 핸들러를 통해 메인에게 전달

        List<Product> productList = service.search();
        List<Option> optionList=service.search_detail(productList,Color); //색전달

        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.obj = optionList;
        if (service.getPAGE_NUM() == 1) {
            msg.arg1 = 10;
        }
        else {
            msg.arg2 = 20;
        }
        handler.sendMessage(msg);
    }
}