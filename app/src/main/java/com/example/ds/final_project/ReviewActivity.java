package com.example.ds.final_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    String maxstit = "";
    int maxscore = 0;


    //11번가 기본 주소 - 상품id에 붙여서 파싱페이지로 만들것임
    private String testUrl = "http://www.11st.co.kr/product/SellerProductDetail.tmall?method=getSellerProductDetail&prdNo=";

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
        reviewDataList.add(new ReviewData(1,"4.5","신축성이 너무 좋아요")); //순서,평점,리뷰 순으로 넣으면됨
        reviewDataList.add(new ReviewData(2,"3.0","핏이 예뻐요"));
        reviewDataList.add(new ReviewData(3,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(4,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(5,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(6,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(6,"4.2","색상이 맘에 들어요."));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기 버튼 실행
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                ((MainActivity)MainActivity.CONTEXT).onResume();
                finish();
                return true;
            }
            case R.id.showoomi:
                Intent homeIntent=new Intent(this,MainActivity.class);
                startActivity(homeIntent);
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
                // 리뷰부분 접근
                Elements contents = doc.select("div.deal_rank_ct");
                System.out.println("전체 : " + contents.text() + "\n");

                for (Element e : contents) {
                    //평점 출력
                    String rank = e.getElementsByTag("strong").text();
                    txtAllRating += "평점 : " + rank + "\n";
                    System.out.println("평점"+txtAllRating);

                    // 평가항목 접근
                    Elements cts = e.select("div.ct");
                    for(Element j : cts){
                        //평가항목 출력
                        String ct = j.getElementsByTag("h5").text();

                        // 세부항목 접근
                        Elements stits = j.select("li");
                        for(Element i : stits){
                            //세부항목 출력
                            String stit = i.select("span.stit").text();
                            String score = i.select("span.num").text();
                            if(Integer.parseInt(score) > maxscore){
                                maxscore = Integer.parseInt(score);
                                maxstit = stit;
                            }
                        }txtKeyword += ct + ":" + maxstit + "("+ maxscore + ") \n";

                    }System.out.println("평가"+txtKeyword);
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
        rating.setText("평점 : "+reviewList.get(position).getRating());
        review.setText(reviewList.get(position).getReview());
        return view;
    }

}
