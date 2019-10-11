package com.example.ds.final_project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    ArrayList<ReviewData> reviewDataList;
    TextView allRating,keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        allRating=(TextView)findViewById(R.id.review_allRating);
        keyword=(TextView)findViewById(R.id.review_keword);

        this.InitializeReviewData();

        ListView listView = (ListView)findViewById(R.id.reviewListView);
        final ReviewAdapter reviewAdapter = new ReviewAdapter(this,reviewDataList);

        listView.setAdapter(reviewAdapter);

    }
    public void InitializeReviewData()
    {
        allRating.setText("평점 : 4.5"); //전체평점 넣으면됨
        keyword.setText("사이즈 : 정사이즈에요\n색상 : 화면대로에요.\n배송속도 : 빨라요"); //키워드 넣으면됨

        reviewDataList = new ArrayList<ReviewData>();
        reviewDataList.add(new ReviewData(1,"4.5","신축성이 너무 좋아요")); //순서,평점,리뷰 순으로 넣으면됨
        reviewDataList.add(new ReviewData(2,"3.0","핏이 예뻐요"));
        reviewDataList.add(new ReviewData(3,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(4,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(5,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(6,"4.2","색상이 맘에 들어요."));
        reviewDataList.add(new ReviewData(6,"4.2","색상이 맘에 들어요."));

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
