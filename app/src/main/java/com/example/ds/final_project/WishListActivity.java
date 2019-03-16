package com.example.ds.final_project;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class WishListActivity extends AppCompatActivity {
    //나의관심상품
    Intent itemInfoIntent; //상품정보
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//뒤로가기버튼
        getSupportActionBar().setTitle("관심상품");
        itemInfoIntent = new Intent(getApplicationContext(),ItemInfoActivity.class);
        int img[] = { //이미지배열
            R.drawable.test_img1,R.drawable.test_img2,R.drawable.test_img3,R.drawable.test_img4,R.drawable.test_img5,R.drawable.test_img6
        };

        // 그리드뷰 어댑터 생성
        final MyAdapter adapter = new MyAdapter (
                getApplicationContext(),
                R.layout.wish_item,       // GridView 항목의 레이아웃 wish_item.xml
                img);
        GridView gv = (GridView)findViewById(R.id.gridView1);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                int img=adapter.getImg(position);
                itemInfoIntent.putExtra("img",img);
                startActivity(itemInfoIntent);
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) { //뒤로가기버튼 실행
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

//그리드뷰 어댑터
class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    int img[];
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, int[] img) {
        this.context = context;
        this.layout = layout;
        this.img = img;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return img.length;
    }

    public int getImg(int position){
        return img[position];
    }

    @Override
    public Object getItem(int position) {
        return img[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = inf.inflate(layout, null);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
        iv.setImageResource(img[position]);

        return convertView;
    }
}

