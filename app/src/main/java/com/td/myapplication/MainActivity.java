package com.td.myapplication;


import androidx.appcompat.app.ActionBar;



import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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

import java.io.FileReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private long tim;
    private EditText edit_account;
    private EditText edit_password;
    private EditText edit_dianzhi;
    private EditText edit_yongjin;
    private EditText edit_bianhao;
    private EditText edit_min_ref;
    private EditText edit_max_ref;

    private String account;
    private String password;
    private String wjAccount;
    private String wjPassword;
    private String wjGuid;
    private String dianzhi;
    private String yongjin;
    private String bianhao;
    private String min_ref;
    private String max_ref;
    public static final int wjTheadFlag = 99;
    private long backtime = 0;
    private final int loginRequstCode = 1;
    private HashMap<String,String> cookiesMap = new HashMap<>();
    private QdMain qdmian;
    private TextView logView;
    private TextView logSuccessTextView;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage( Message msg){
            switch (msg.what){
                case QdMain.refSuccessMsg:
                    refreshLogView(MyUtil.getTime()+"->"+"刷新任务正常，共"+msg.arg1+"条任务，有符合条件单立即抢单\n--------------------------------\n");
                    break;
                case QdMain.actionStartMsg:
                    refreshLogView(MyUtil.getTime()+"->"+msg.obj.toString()+"\n--------------------------------\n");
                    break;
                case QianDanThread.stateMsg:
                    qianDanProcess(msg.obj.toString(),msg.arg1,msg.arg2);
                    break;
                case QianDanThread.stateSuccessMsg:
                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ding);
                    mediaPlayer.start();
                    qianDanSuccessProcess(msg.obj,msg.arg1,msg.arg2);
                    break;
                case MainActivity.wjTheadFlag:
                    new Thread(new WjThead(wjAccount,wjPassword,wjGuid,this)).start();
                    break;
                case QdMain.reLoginMsg:
                    Toast.makeText(MainActivity.this,"重新登录平台",Toast.LENGTH_SHORT).show();
                    login();
                    break;

            }
        }
    };

    public void qianDanProcess(String str,int arg1,int arg2){
        refreshLogView(MyUtil.getTime()+"->"+str +"\n--------------------------------\n");

    }

    public void qianDanSuccessProcess(Object object,int arg1,int arg2){
        ArrayList list = (ArrayList)object;
        refreshLogSuccessView(MyUtil.getTime()+"->["+list.get(2) +"]" +
                "价格："+list.get(0)+"佣金"+list.get(1)+"\n--------------------------------\n");

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

        logSuccessTextView = (TextView)findViewById(R.id.logSuccessTextView);
        logSuccessTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        logView=(TextView)findViewById(R.id.logTextView);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        for(int i=0;i<20;i++){
            refreshLogView("欢迎使用秒单王系列产品\n本yapp是淘单抢单\n");
        }
        for(int i=0;i<20;i++){
            refreshLogSuccessView("本文本显示抢单成功详细\n");
        }

        Intent inte = getIntent();
        wjAccount = inte.getStringExtra("username");
        wjPassword = inte.getStringExtra("password");
        wjGuid = inte.getStringExtra("guid");
        Message m = new Message();
        m.what= wjTheadFlag;
        handler.sendMessage(m);

        /*Log.i(TAG,inte.getStringExtra("username")+inte.getStringExtra("password"));
        new Thread(new WjThead(inte.getStringExtra("username"),
                inte.getStringExtra("password"),inte.getStringExtra("guid"),handler)).start();*/



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
        account = edit_account.getText().toString().trim();
        password = edit_password.getText().toString().trim();;
        dianzhi = edit_dianzhi.getText().toString().trim();;
        yongjin = edit_yongjin.getText().toString().trim();;
        bianhao = edit_bianhao.getText().toString().trim();;
        min_ref = edit_min_ref.getText().toString().trim();;
        max_ref = edit_max_ref.getText().toString().trim();;
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


    void refreshLogSuccessView(String msg) {

        logSuccessTextView.append(msg);
        logSuccessTextView.post(new Runnable() {
            @Override
            public void run() {
                int offset = logSuccessTextView.getLineCount() * logSuccessTextView.getLineHeight();

                if (offset > logSuccessTextView.getHeight()) {
                    logSuccessTextView.scrollTo(0, offset - logSuccessTextView.getHeight());
                }
            }
        });

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
                if(button_start.getText().toString().trim().equals("暂停")){
                    QdMain.sleepFlag= true;
                    refreshLogView("\n暂定中......请勿其他操作。\n");
                    button_start.setText("启动");
                }else{
                    account = edit_account.getText().toString().trim();
                    password = edit_password.getText().toString().trim();
                    dianzhi = edit_dianzhi.getText().toString().trim();
                    yongjin = edit_yongjin.getText().toString().trim();
                    bianhao = edit_bianhao.getText().toString().trim();
                    min_ref = edit_min_ref.getText().toString().trim();
                    max_ref = edit_max_ref.getText().toString().trim();
                    refreshLogView("刷新间隔："+min_ref+"-"+max_ref+"秒,垫支："+dianzhi+"佣金："+yongjin+"发送编号:"+bianhao+"\n-----------------------\n");
                    if(qdmian!=null){
                        qdmian.setBrokerage(Double.valueOf(yongjin));
                        qdmian.setMaxSleep(Integer.valueOf(max_ref));
                        qdmian.setMinSleep(Integer.valueOf(min_ref));
                        qdmian.setPic(Integer.valueOf(dianzhi));
                        qdmian.setUid(bianhao);
                    }
                    QdMain.sleepFlag= false;
                    QdMain.runFlag = true;
                    login();
                }

                break;
            case R.id.exit:

                //<script>var x="51@@toLowerCase@String@createElement@@@0xFF@return@@captcha@@charAt@document@2@@else@@innerHTML@__jsl_clearance@eval@@for@KX@d@D@@Path@@try@@length@GMT@19@0@@@@36@toString@@challenge@pathname@@@window@@05@reverse@RegExp@if@https@e@@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@U@setTimeout@@@0xEDB88320@charCodeAt@parseInt@@cookie@@search@catch@@8@DOMContentLoaded@a@@@@function@@firstChild@@@3@f@headless@g@@split@@while@@@8AaAb@Sat@@@attachEvent@@div@join@replace@addEventListener@false@@@Array@fromCharCode@@onreadystatechange@substr@1564807883@23@@Expires@match@JgSe0upZ@location@Aug@href@1@d65@chars@1500@@new@8TC89@var@k1@03@".replace(/@*$/,"").split("@"),y="a5 63=64(){4a('97.99=97.37+97.57.83(/[\\?|&]b-36/,\\'\\')',a1);12.55='18=91.69|2b|'+(64(){a5 39=[64(63){9 63},64(39){9 39},64(63){1b(a5 39=2b;39<63.28;39++){63[39]=53(63[39]).34(33)};9 63.82('')}],63=[((+!+[])+[]+[[]][2b])+[(-~[]+[(((+!+[])<<(+!+[])))*[((+!+[])<<(+!+[]))]]>>-~[])],((+!+[])+[]+[[]][2b])+[((+!+[])<<(+!+[]))],((+!+[])+[]+[[]][2b]),[(-~[]+[(((+!+[])<<(+!+[])))*[((+!+[])<<(+!+[]))]]>>-~[])],[-~[-~-~!!3a.6b]+[-~(+!{})-~(+!{})]*(-~[-~-~!!3a.6b])],((+!+[])+[]+[[]][2b])+((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),((+[])+[]+[[]][2b]),[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],((+!+[])+[]+[[]][2b])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),[((+!+[])<<(+!+[]))],(5a+[]),((+!+[])+[]+[[]][2b])+((+[])+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+((+!+[])+[]+[[]][2b])];1b(a5 80=2b;80<63.28;80++){63[80]=39[[9a,2b,9a,2b,9a,13,9a,13,2b,9a,2b,9a,13,9a,2b,9a,2b,9a][80]](['22','20','a6','49',[((+!+[])+[]+[[]][2b])+(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b])],((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),[((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b])+((+!+[])+[]+[[]][2b])],((+[])+[]+[[]][2b]),((+[])+[]+[[]][2b]),'9b','%','22',[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],[((+!+[])+[]+[[]][2b])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b])],((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),'a4','77'][63[80]])};9 63.82('')})()+';94=78, a7-98-2a 40:1:92 29;24=/;'};43((64(){26{9 !!3a.84;}58(45){9 85;}})()){12.84('5b',63,85)}15{12.7b('8b',63)}",f=function(x,y){var a=0,b=0,c=0;x=x.split("");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\b\w+\b/g, function(y){return x[f(y,z)-1]||("_"+y)}));break}catch(_){}</script>

                String s = "var x=\"51@@toLowerCase@String@createElement@@@0xFF@return@@captcha@@charAt@document@2@@else@@innerHTML@__jsl_clearance@eval@@for@KX@d@D@@Path@@try@@length@GMT@19@0@@@@36@toString@@challenge@pathname@@@window@@05@reverse@RegExp@if@https@e@@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@U@setTimeout@@@0xEDB88320@charCodeAt@parseInt@@cookie@@search@catch@@8@DOMContentLoaded@a@@@@function@@firstChild@@@3@f@headless@g@@split@@while@@@8AaAb@Sat@@@attachEvent@@div@join@replace@addEventListener@false@@@Array@fromCharCode@@onreadystatechange@substr@1564807883@23@@Expires@match@JgSe0upZ@location@Aug@href@1@d65@chars@1500@@new@8TC89@var@k1@03@\".replace(/@*$/,\"\").split(\"@\"),y=\"a5 63=64(){4a('97.99=97.37+97.57.83(/[\\\\?|&]b-36/,\\\\'\\\\')',a1);12.55='18=91.69|2b|'+(64(){a5 39=[64(63){9 63},64(39){9 39},64(63){1b(a5 39=2b;39<63.28;39++){63[39]=53(63[39]).34(33)};9 63.82('')}],63=[((+!+[])+[]+[[]][2b])+[(-~[]+[(((+!+[])<<(+!+[])))*[((+!+[])<<(+!+[]))]]>>-~[])],((+!+[])+[]+[[]][2b])+[((+!+[])<<(+!+[]))],((+!+[])+[]+[[]][2b]),[(-~[]+[(((+!+[])<<(+!+[])))*[((+!+[])<<(+!+[]))]]>>-~[])],[-~[-~-~!!3a.6b]+[-~(+!{})-~(+!{})]*(-~[-~-~!!3a.6b])],((+!+[])+[]+[[]][2b])+((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),((+[])+[]+[[]][2b]),[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],((+!+[])+[]+[[]][2b])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),[((+!+[])<<(+!+[]))],(5a+[]),((+!+[])+[]+[[]][2b])+((+[])+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+((+!+[])+[]+[[]][2b])];1b(a5 80=2b;80<63.28;80++){63[80]=39[[9a,2b,9a,2b,9a,13,9a,13,2b,9a,2b,9a,13,9a,2b,9a,2b,9a][80]](['22','20','a6','49',[((+!+[])+[]+[[]][2b])+(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b])],((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),[((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b])+((+!+[])+[]+[[]][2b])],((+[])+[]+[[]][2b]),((+[])+[]+[[]][2b]),'9b','%','22',[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],[((+!+[])+[]+[[]][2b])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b])],((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),'a4','77'][63[80]])};9 63.82('')})()+';94=78, a7-98-2a 40:1:92 29;24=/;'};43((64(){26{9 !!3a.84;}58(45){9 85;}})()){12.84('5b',63,85)}15{12.7b('8b',63)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script>";

                Intent intent  =  new Intent(this,JsPageActivity.class);
                startActivity(intent);
                //exit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if((System.currentTimeMillis()-backtime)<2000){
           exit();
        }else{
            backtime = System.currentTimeMillis();
            Toast.makeText(this,"再按一次退出程序！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case loginRequstCode:
                if(resultCode==RESULT_CANCELED){
                    return;
                }
                //Toast.makeText(this, data.getStringExtra("cookies"), Toast.LENGTH_SHORT).show();
                Log.i(TAG,data.getStringExtra("cookies"));
                initState(data.getStringExtra("cookies"));
                SimpleDateFormat sfd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    tim = sfd1.parse(data.getStringExtra("cookietime")).getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                default:
                    break;
        }

    }
    //ASP.NET_SessionId=ahhlwisztzchchcrrmkbatfe; __jsluid_h=d4c407af3c7185a19ddbd53a246c0b82; .ASPXAUTH=169EECD909E8C22FBCF0CB970B8344FE5DFF77F55DA7A4CA71B47FA1B277E9F656F797E7939D4B42272AB0521B9EFC3F19E5789060980485002EA557527837492650F9525F42351E0DC69A6AD13A66CB911EF351AD03111A0AE7890738E5625778467C6DFE0D26880D614E223D4D96723769632A3463200D646408C31B4CDEB25ADBC5AC63C453B5299B85103BDD245B033570117B01C1B851BF70A3B64D74C8B5CEEF1393416BAEED9EAC46348B7225
    private void initState(String cookies){
        Log.i(TAG,"cookies="+cookies);
        String[] strs1 = cookies.split(";");
        for (String strs:strs1) {
            String[] strs2 = strs.split("=");
            cookiesMap.put(strs2[0],strs2[1]);
        }
        button_state.setChecked(true);
        button_start.setText("暂停");
        //refreshLogView(cookies);
        refreshLogView("\n"+account+"登录成功，休息一下，开始抢单");
        if(!QdMain.theadState){
            QdMain.theadState = true;
            qdmian = new QdMain(handler,account,Integer.valueOf(max_ref),Integer.valueOf(min_ref),Integer.valueOf(dianzhi),Double.valueOf(yongjin),bianhao,cookiesMap,tim);
            Thread mainThead = new Thread(qdmian);
            mainThead.start();
        }

    }
    void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void login(){

        EditText editText = (EditText)findViewById(R.id.account_md) ;
        EditText editText1 = (EditText)findViewById(R.id.password_md) ;
        account = editText.getText().toString().trim();;
        password =  editText1.getText().toString().trim();;
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
                tim = sf.parse(strs[0]).getTime();
                if(tim-System.currentTimeMillis()>21*60*1000){
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
        QdMain.runFlag = false;
        ActivityCollector.finishAll();
    }

}
