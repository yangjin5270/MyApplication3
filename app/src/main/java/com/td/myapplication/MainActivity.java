package com.td.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends BaseActivity implements View.OnClickListener{



    private String TAG = "MainActivity";
    private Button button_start;
    private Button button_exit;
    private ToggleButton button_state;
    private String account;
    private String password;

    private final int loginRequstCode = 1;
    private HashMap<String,String> cookiesMap = new HashMap<>();

    private TextView logView;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage( Message msg){
            switch (msg.what){
                case QdMain.refSuccessMsg:
                    refreshLogView("刷新任务正常，共"+msg.arg1+"条任务\n");
                    break;
                case QdMain.actionStartMsg:
                    refreshLogView(msg.obj.toString()+"\n");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        button_start = (Button)findViewById(R.id.start);
        button_state=(ToggleButton)findViewById(R.id.toggleButton);
        button_start.setOnClickListener(this);
        button_exit =(Button)findViewById(R.id.exit);
        button_exit.setOnClickListener(this);


        logView=(TextView)findViewById(R.id.logTextView);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        for(int i=0;i<10;i++){
            refreshLogView("欢迎使用秒单王系列产品\n本app是淘单抢单\n");
        }


    }

    void refreshLogView(String msg) {

        logView.append(msg);
        logView.post(new Runnable() {
            @Override
            public void run() {
                int offset = logView.getLineCount() * logView.getLineHeight();
                //print(offset);
                //print(logView.getHeight());

                if (offset > logView.getHeight()) {
                    logView.scrollTo(0, offset - logView.getHeight());
                }
            }
        });

    }

     void print(Object msg){
        Log.i(TAG,String.valueOf(msg).toString());
     }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start:
                login();
                break;
            case R.id.exit:
                exit();
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case loginRequstCode:
                if(resultCode==RESULT_CANCELED){
                    return;
                }
                Toast.makeText(this, data.getStringExtra("cookies"), Toast.LENGTH_SHORT).show();
                Log.i(TAG,data.getStringExtra("cookies"));
                initState(data.getStringExtra("cookies"));
                break;
                default:
                    break;
        }

    }
    //ASP.NET_SessionId=ahhlwisztzchchcrrmkbatfe; __jsluid_h=d4c407af3c7185a19ddbd53a246c0b82; .ASPXAUTH=169EECD909E8C22FBCF0CB970B8344FE5DFF77F55DA7A4CA71B47FA1B277E9F656F797E7939D4B42272AB0521B9EFC3F19E5789060980485002EA557527837492650F9525F42351E0DC69A6AD13A66CB911EF351AD03111A0AE7890738E5625778467C6DFE0D26880D614E223D4D96723769632A3463200D646408C31B4CDEB25ADBC5AC63C453B5299B85103BDD245B033570117B01C1B851BF70A3B64D74C8B5CEEF1393416BAEED9EAC46348B7225
    private void initState(String cookies){
        String[] strs1 = cookies.split(";");
        for (String strs:strs1) {
            String[] strs2 = strs.split("=");
            cookiesMap.put(strs2[0],strs2[1]);
        }
        button_state.setChecked(true);
        button_start.setText("暂停");
        //refreshLogView(cookies);
        refreshLogView("\n"+account+"登录成功，休息一下，开始抢单");
        Thread mainThead = new Thread(new QdMain(handler,15,12,1000,0.9,cookiesMap));
        mainThead.start();
    }
    void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void login(){

        EditText editText = (EditText)findViewById(R.id.account_md) ;
        EditText editText1 = (EditText)findViewById(R.id.password_md) ;
        account = editText.getText().toString();
        password =  editText1.getText().toString();


        if(account==null||account.equals("")||password==null||password.equals("")){
            toast("平台账号或密码不能为空");
            return;
        }

        try {
            File f = new File("/sdcard/cookies");
            if (!f.exists()) {
                Log.i(TAG, "建立/sdcard/cookies文件夹 " + f.mkdir());
            }
            SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd");
            String cookiePath = sfd1.format(new Date());
            //printPro cookiePath

            String path = "/sdcard/cookies/"+account+cookiePath+".txt";
            File file = new File(path);
            if (file.exists()) {
                char[] chars = new char[1024];
                FileReader fileReader = new FileReader(file);
                StringBuffer stringBuffer = new StringBuffer();
                int i;
                while((i = fileReader.read(chars))!=-1){
                    stringBuffer.append(chars,0,i);
                }
                Log.i(TAG,stringBuffer.toString());
                print(stringBuffer.toString());
                String[] strs = stringBuffer.toString().split("qazwsxedc");
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long tim = sf.parse(strs[0]).getTime();
                if(tim-System.currentTimeMillis()>60*60*1000){
                    initState(strs[1]);
                    Toast.makeText(this,"读取到登录信息！",Toast.LENGTH_SHORT).show();;
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent loginIntent = new Intent(this,loginActivity.class);
        loginIntent.putExtra("account",account);
        loginIntent.putExtra("password",password);
        startActivityForResult(loginIntent,loginRequstCode);


    }

    private void exit(){
        this.finish();
    }

}
