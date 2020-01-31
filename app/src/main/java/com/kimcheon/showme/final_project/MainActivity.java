package com.kimcheon.showme.final_project;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.kimcheon.showme.final_project.db.DTO.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;
import static android.speech.tts.TextToSpeech.ERROR;
public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST = 100;

    private Gson gson = new GsonBuilder().create();
    public static Context CONTEXT;

    //사용자 정보
    private User user;
    private String uuid; //스마트폰 고유번호
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String permission[] = new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS}; //폰상태(로그인UUID),오디오, sms, 연락처
        boolean check=false;
        for(int i=0;i<permission.length;i++){
            check = check || ContextCompat.checkSelfPermission(this,permission[i]) != PackageManager.PERMISSION_GRANTED;
        }
        if(check == true){ //접근 권한 없을때
            showPermission();
        }
        else{ //접근 권한 있을때
            Intent intent = new Intent(this,ChatbotActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        CONTEXT=this;

        if(!(getPreferences("USER")==null||getPreferences("USER")=="")){
            String strContact=getPreferences("USER");
            user=gson.fromJson(strContact,User.class);
//            Log.d("uuid 정보",user.getName()+user.getAddress()+user.getPhoneNum());
        }else{
            uuid = GetDevicesUUID(getBaseContext());

            savePreferences("uuid",uuid);
        }

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);

                }
            }
        });

        //키해시 구하기
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.ds.final_project", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
    // 접근 권한 안내
    public void showPermission(){
        String msg =
                "1. 전화 권한 (필수)\n" +
                "-사용자 정보를 확인하기 위해 필요합니다.\n" +
                "2. 마이크 권한 (필수)\n" +
                "-음성 검색 기능 사용 시 필요합니다.\n" +
//                "3. SMS 권한 (필수)\n" +
//                "- 공유 서비스 사용 시 문자 발송을 위해 필요합니다.\n" +
                "3. 연락처 권한 (필수)\n" +
                "- 공유 서비스 사용 시 연락처 정보를 확인하기 위해 필요합니다.\n";
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("[쇼우미 사용을 위해 필요한 접근 권한 안내]")
                .setMessage(msg)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermission();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    //접근 권한 확인하기
    public void checkPermission(){
        String permission[] = new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS}; //폰상태(로그인UUID),오디오, sms, 연락처
        ActivityCompat.requestPermissions(this,permission, PERMISSIONS_REQUEST);
    }

    //접근 권한 승인 결과
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST){
            if (grantResults[0] == 0 && grantResults[1] == 0 && grantResults[2] == 0 ){ //권한이 승낙된 경우
                Intent intent = new Intent(this,ChatbotActivity.class);
                startActivity(intent);
                finish();
            }
            else{ //권한이 거절된 경우
                String msg="";
                if(grantResults[0] != 0)
                    msg=msg+"사용자 정보 확인을 위해 전화 접근 권한이 필요합니다. \n전화 권한을 허가해주세요.\n";
                if(grantResults[1] != 0)
                    msg=msg+"음성 검색을 이용하기 위해 마이크 접근 권한이 필요합니다. \n마이크 권한을 허가해주세요.\n";
//                if (grantResults[2] !=0)
//                    msg=msg+"공유 서비스를 위해 문자 발송을 할 수 있습니다. \nSMS 권한을 허가해주세요.\n";
                if (grantResults[2] !=0)
                    msg=msg+"공유 서비스를 위해 연락처 정보를 확인할 수 있습니다. \n연락처 권한을 허가해주세요.\n";

                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage(msg)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermission();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }

    // 값 저장하기
    private void savePreferences(String key, String s){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }
    // 값 불러오기
    public String  getPreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

    // 값 삭제하기
    public void removePreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
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


}
