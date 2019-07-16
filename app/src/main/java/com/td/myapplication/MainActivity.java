package com.td.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static final String PREFERENCE_NAME = "SaveSetting";
    //定义SharedPreferences的访问模式：全局读+全局写
    public static int MODE = MODE_PRIVATE;

    private String TAG = "MainActivity";
    private Button button_start;
    private Button button_exit;
    private ToggleButton button_state;

    private EditText edit_account;
    private EditText edit_password;
    private EditText edit_dianzhi;
    private EditText edit_yongjin;
    private EditText edit_bianhao;
    private EditText edit_min_ref;
    private EditText edit_max_ref;

    private String account;
    private String password;
    private String dianzhi;
    private String yongjin;
    private String bianhao;
    private String min_ref;
    private String max_ref;

    private final int loginRequstCode = 1;
    private HashMap<String,String> cookiesMap = new HashMap<>();

    private TextView logView;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage( Message msg){
            switch (msg.what){
                case QdMain.refSuccessMsg:
                    refreshLogView(MyUtil.getTime()+"->"+"刷新任务正常，共"+msg.arg1+"条任务\n--------------------------------\n");
                    break;
                case QdMain.actionStartMsg:
                    refreshLogView(MyUtil.getTime()+"->"+msg.obj.toString()+"\n--------------------------------\n");
                    break;
                case QianDanThread.stateMsg:
                    qianDanProcess(msg.obj.toString(),msg.arg1,msg.arg2);
                    break;
            }
        }
    };

    public void qianDanProcess(String str,int arg1,int arg2){
        refreshLogView(MyUtil.getTime()+"->"+str +"\n--------------------------------\n");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        edit_account=(EditText)findViewById(R.id.account_md);
        edit_password=(EditText)findViewById(R.id.password_md);
        edit_dianzhi=(EditText)findViewById(R.id.dianzhi);
        edit_yongjin=(EditText)findViewById(R.id.yongjin);
        edit_bianhao=(EditText)findViewById(R.id.bianhao);
        edit_min_ref=(EditText)findViewById(R.id.min_ref);
        edit_max_ref=(EditText)findViewById(R.id.max_ref);

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
        dianzhi = sharedPreferences.getString("dianzhi", "50");
        yongjin = sharedPreferences.getString("yongjin", "1.5");
        bianhao = sharedPreferences.getString("bianhao", "2056");
        min_ref = sharedPreferences.getString("min_ref", "11");
        max_ref = sharedPreferences.getString("max_ref", "15");
        edit_account.setText(account);
        edit_password.setText(password);
        edit_bianhao.setText(bianhao);
        edit_dianzhi.setText(dianzhi);
        edit_yongjin.setText(yongjin);
        edit_min_ref.setText(min_ref);
        edit_max_ref.setText(max_ref);
        //读取数据

    }

    private void saveSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                PREFERENCE_NAME, MODE);
        account = edit_account.getText().toString();
        password = edit_password.getText().toString();
        dianzhi = edit_dianzhi.getText().toString();
        yongjin = edit_yongjin.getText().toString();
        bianhao = edit_bianhao.getText().toString();
        min_ref = edit_min_ref.getText().toString();
        max_ref = edit_max_ref.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);
        editor.putString("dianzhi", dianzhi);
        editor.putString("yongjin", yongjin);
        editor.putString("bianhao", bianhao);
        editor.putString("min_ref", min_ref);
        editor.putString("max_ref", max_ref);
        editor.commit();
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
        Thread mainThead = new Thread(new QdMain(handler,account,15,12,30,0.9,cookiesMap));
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
        saveSharedPreferences();

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
