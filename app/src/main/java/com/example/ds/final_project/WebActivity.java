package com.example.ds.final_project;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebActivity extends AppCompatActivity {

    private String htmlPageUrl = "http://deal.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=143698074&cls=4&trTypeCd=104"; //파싱할 홈페이지의 URL주소
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat="";
    private ImageView imageViewHtmlDocument;
    public Bitmap bitmap;

    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        imageViewHtmlDocument = (ImageView) findViewById(R.id.imageView);
        textviewHtmlDocument = (TextView)findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기

        Button htmlTitleButton = (Button)findViewById(R.id.button);
        htmlTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println( (cnt+1) +"번째 파싱");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
                cnt++;
            }
        });


    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Document doc = Jsoup.connect(htmlPageUrl).get();

                String itemname = "";
                String itemqty = "";
                String iteminfo = "";
                String itemprice = "";

                Elements images = doc.select("li.photo");
                for(Element e : images){
                    for(Element k : e.children()){
                        Elements a = k.getElementsByTag("a");
                        itemname = a.attr("data-dtloptnm");
                        itemqty = a.attr("data-stckqty");
                        for(Element j : k.children()){
                            Elements p = j.getElementsByTag("p");
                            itemprice = p.attr("strong");
                            if(itemqty.equals("0")){
                                iteminfo = "품절";
                            }
                            else {
                                iteminfo = "O";
                            }
                        }
                    }
                    htmlContentInStringFormat += itemname+":"+itemqty +"개("+iteminfo+")"+itemprice+"원 \n";
                    System.out.println("-------------------------------------------------------------");
                }

                //테스트1
                /*Elements titles= doc.select("strong.title");

                System.out.println("-------------------------------------------------------------");
                for(Element e: titles){
                    System.out.println("title: " + e.text());
                    htmlContentInStringFormat += e.text().trim() + "\n\n";
                }

                //테스트2
                titles= doc.select("span.prdc_info");

                System.out.println("-------------------------------------------------------------");
                for(Element e: titles){
                    System.out.println("title: " + e.text());
                    htmlContentInStringFormat += e.text().trim() + "\n";
                }

                //테스트3
                Elements images = doc.select("div.photo_wrap");
                for(Element e : images){
                    for(Element k : e.children()){
                        Elements i = k.getElementsByTag("img");
                        for(Element j : i){
                            String src = j.attr("src");
                            htmlContentInStringFormat += src + "\n";
                            System.out.println("-------------------------------------------------------------");
                        }
                    }
                }*/

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentInStringFormat);
        }
    }
}
