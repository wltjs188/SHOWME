package com.example.ds.final_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    ArrayList<ReviewData> reviewDataList;
    TextView allRating,keyword;
    private String productId=" ";
    private String productUrl = "";
    private String txtAllRating ="";
    private String txtKeyword ="";
    String stit = "";
    String score = "";
    int num = 0;


    //11번가 기본 주소 - 상품id에 붙여서 파싱페이지로 만들것임
    private String testUrl = "https://store.musinsa.com/app/product/detail/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기 버튼
        getSupportActionBar().setTitle("리뷰보기");
        Intent intent = getIntent();
        productId=intent.getStringExtra("product");
        productUrl = testUrl+productId;

        allRating=(TextView)findViewById(R.id.review_allRating);
        keyword=(TextView)findViewById(R.id.review_keyword);

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();

        this.InitializeReviewData();

        ListView listView = (ListView)findViewById(R.id.reviewListView);
        final ReviewAdapter reviewAdapter = new ReviewAdapter(this,reviewDataList);

        listView.setAdapter(reviewAdapter);
    }
    public void InitializeReviewData()
    {
        //allRating.setText(txtAllRating); //전체평점 넣으면됨
        //keyword.setText(txtKeyword); //키워드 넣으면됨

        reviewDataList = new ArrayList<ReviewData>();
//        reviewDataList.add(new ReviewData(1,"4.5","신축성이 너무 좋아요")); //순서,평점,리뷰 순으로 넣으면됨
//        reviewDataList.add(new ReviewData(2,"3.0","핏이 예뻐요"));
//        reviewDataList.add(new ReviewData(3,"4.2","색상이 맘에 들어요."));
//        reviewDataList.add(new ReviewData(4,"4.2","색상이 맘에 들어요."));
//        reviewDataList.add(new ReviewData(5,"4.2","색상이 맘에 들어요."));
//        reviewDataList.add(new ReviewData(6,"4.2","색상이 맘에 들어요."));
//        reviewDataList.add(new ReviewData(6,"4.2","색상이 맘에 들어요."));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기 버튼 실행
        Intent homeIntent=new Intent(this,ChatbotActivity.class);
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
//                startActivity(homeIntent);
                finish();
                return true;
            }
            case R.id.showme:
                startActivity(homeIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog = new ProgressDialog(ReviewActivity.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시만 기다려주세요");

            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //파싱페이지 넣어주기
                Document doc = Jsoup.connect(productUrl).get();
                System.out.println("주소"+productUrl);
                // 키워드부분 접근
                Elements contents = doc.select("div.wrap-estimate-avg");
                System.out.println("전체 : " + contents.text() + "\n");

                for (Element e : contents) {
                    //평점 출력
                    String rank = e.select("span.rate").text();
                    txtAllRating += "평점 : " + rank + "\n";
                    //System.out.println("평점"+txtAllRating);

                    // 평가항목 접근
                    Elements cts = e.select("div.lv-contents");

                    for(Element j : cts){
                        //평가항목 출력
                        String ct = j.select("div.tit").text();

                        // 세부항목 접근
                        Elements stits = j.select("li.on");
                        for(Element i : stits){
                            //세부항목 출력
                            stit = i.select("div.label").text();
                            //System.out.println("세부항목"+stit);
                            score = i.select("div.per").text();
                        }txtKeyword += ct + ":" + stit + "("+ score + ") \n";

                    }//System.out.println("평가"+txtKeyword);
                }

                // 리뷰부분 접근
                Elements reviewbox = doc.select("div#style_estimate_list");
                Elements reviewContent = reviewbox.select("div.nslist_post");

                for (Element r : reviewContent) {
                    num++;
                    //리뷰 제목
                    String rt = r.select("div.tit").text();
                    //리뷰 내용
                    String rank = r.select("span.content-review").text();

                    ReviewData reviewD = new ReviewData(num,rt,rank);
                    //System.out.println("번호:"+reviewD.getNum()+"제목:"+reviewD.getTitle()+"내용"+reviewD.getReview());
                    Log.d("reviewData",reviewD+"");
                    //add가 안됨..!!
                    reviewDataList.add(reviewD);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            allRating.setText(txtAllRating);
            keyword.setText(txtKeyword);
            progressDialog.dismiss();
        }
    }
}

class ReviewAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ReviewData> reviewList;

    public ReviewAdapter(Context context, ArrayList<ReviewData> review) {
        mContext = context;
        this.reviewList = review;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ReviewData getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.review_item, null);

        TextView num = (TextView)view.findViewById(R.id.num);
        TextView rating = (TextView)view.findViewById(R.id.rating);
        TextView review = (TextView)view.findViewById(R.id.review);

        num.setText(reviewList.get(position).getNum()+"번");
        rating.setText("평점 : "+reviewList.get(position).getTitle());
        review.setText(reviewList.get(position).getReview());
        return view;
    }

}
