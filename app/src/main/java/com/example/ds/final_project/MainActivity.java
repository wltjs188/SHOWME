package com.example.ds.final_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //메인화면
    Intent searchIntent,wishIntent,infoIntent,webIntent; //쇼핑시작,나의관심상품,정보수정
    private String uuid=""; //스마트폰 고유번호
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("쇼움이");
        searchIntent=new Intent(getApplicationContext(),searchActivity.class);//쇼핑시작
        wishIntent=new Intent(getApplicationContext(),WishListActivity.class);//나의관심상품
        infoIntent=new Intent(getApplicationContext(),MyInfoActivity.class);//나의정보수정
        webIntent=new Intent(getApplicationContext(),WebActivity.class);//나의정보수정
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permission2 != PackageManager.PERMISSION_GRANTED) { makeRequest(); }

        uuid = GetDevicesUUID(getBaseContext());
        savePreferences(uuid);

    }
    // 값 저장하기
    private void savePreferences(String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uuid", s);
        editor.commit();
    }
    //스마트폰 고유번호 가져오기
    private String GetDevicesUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        String uuid=" ";
        try{
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
            uuid = deviceId;
            Log.d("uuid0",uuid);
        }
        catch(SecurityException e){ }
        return uuid;
    }
    public void onSearchClicked(View view) { //쇼핑시작
        startActivity(searchIntent);
    } //쇼핑시작버튼
    public void onWishClicked(View view) { //나의관심상품
        startActivity(wishIntent);
    } //관심상품버튼
    public void onInfoClicked(View view) { //나의정보수정
        startActivityForResult(infoIntent,1);
    } //나의정보수정버튼
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            Toast.makeText(getApplicationContext(), "정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
        }
    }
    public void onWebClicked(View view) { startActivity(webIntent); }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                101);
    }
}
