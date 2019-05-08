package com.example.ds.final_project;

import android.os.Handler;
import android.os.Message;

import java.util.List;

/**
 * Created by SAMSUNG on 2019-03-19.
 */

public class ProductSearchThread extends Thread {
    private ProductSearchService service;
    private Handler handler;
    public ProductSearchThread(ProductSearchService service, Handler handler){
        this.service = service;
        this.handler = handler;
    }
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        // service의 search메소드를 수행하고 결과를 핸들러를 통해 메인에게 전달

        List<Product> data = service.search();
        data=service.search_detail(data,"화이트"); //색전달
        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.obj = data;
        if (service.getPAGE_NUM() == 1) {
            msg.arg1 = 10;
        }
        else {
            msg.arg2 = 20;
        }
        handler.sendMessage(msg);

    }
}