package com.td.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class loginWJActivity extends BaseActivity implements  View.OnClickListener{

    public static final String PREFERENCE_NAME = "SaveSetting1";
    //定义SharedPreferences的访问模式：全局读+全局写
    public static int MODE = MODE_PRIVATE;
    private EditText usernameEditText ;
    private EditText passwordEditText ;
    private Button loginButton ;
    private ProgressBar loadingProgressBar;
    private String TAG="loginWJActivity";
    public String IMEI;
    private String account;
    private String password;
    public boolean start=true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadingProgressBar.setVisibility(View.GONE);

            super.handleMessage(msg);
            HashMap info = (HashMap) msg.obj;
            if(msg.what==2){
                Intent intent = new Intent(loginWJActivity.this, MainActivity.class);
                intent.putExtra("username",usernameEditText.getText().toString().trim());
                intent.putExtra("password",passwordEditText.getText().toString().trim());
                intent.putExtra("guid",IMEI);
                startActivity(intent);
            } else if (msg.what == 1) {

                if("success".equals(info.get("infoType").toString())){

                    IMEI=String.valueOf(System.currentTimeMillis());

                    try {
                        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                        IMEI = tm.getDeviceId();
                        Log.i(TAG,"IMEI"+IMEI);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    IMEI=String.valueOf(System.currentTimeMillis());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String state=  WjNetVerify.stateControl(IMEI,"0");
                            while(!state.equals("1")){
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                state=  WjNetVerify.stateControl(IMEI,"0");
                            }
                            Message mm = new Message();
                            mm.what=2;
                            handler.sendMessage(mm);
                        }
                    }).start();


                }else{
                    Toast.makeText(loginWJActivity.this,info.get("message").toString(),Toast.LENGTH_SHORT).show();
                }
            }
           /* Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username",usernameEditText.getText().toString());
            startActivity(intent);*/
            //Toast.makeText(LoginActivity.this,"handler",Toast.LENGTH_SHORT);

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);


        loginButton.setOnClickListener(this);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.ding);
        mediaPlayer.start();
    }


    @Override
    public void onClick(View view) {


        loadingProgressBar.setVisibility(View.VISIBLE);loadingProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {

                WjNetVerify.setAccount(usernameEditText.getText().toString().trim());
                WjNetVerify.setPassword(passwordEditText.getText().toString().trim());
                HashMap map = WjNetVerify.login("1");
                Message message =handler.obtainMessage();
                message.obj=map;
                message.what=1;
                message.arg1=1;
                message.arg2=2;
                handler.sendMessage(message);

            }
        }).start();

    }




    @Override
    protected void onStart() {
        loadSharedPreferences();
        super.onStart();
    }

    @Override
    protected void onStop() {
        saveSharedPreferences();
        super.onStop();
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                PREFERENCE_NAME, MODE);
        account = sharedPreferences.getString("account", "");
        password = sharedPreferences.getString("password", "");

        usernameEditText.setText(account);
        passwordEditText.setText(password);

        //读取数据

    }

    private void saveSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                PREFERENCE_NAME, MODE);
        account = usernameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);

        editor.commit();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

    }


}
