package com.td.myapplication;

import android.content.Intent;
import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsPageActivity extends BaseActivity {

    private WebView webView;
    private String TAG= "JsPageActivity";
    private String account;
    private String password;
    private int action=1;
    private String returnStr="";
    private String cookietime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jspage);
        webView = (WebView)findViewById(R.id.jsWebView);
        webView.setWebViewClient(webViewClient);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        password = intent.getStringExtra("password");

        WebSettings webSettings = webView .getSettings();


        //支持获取手势焦点，输入用户名、密码或其他
        webView.requestFocusFromTouch();

        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JsInterface(),"JsInterface");

        webView.loadUrl("https://www.baidu.com");


    }
    private WebViewClient webViewClient = new WebViewClient(){

        String CookieStr;
        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            CookieStr = cookieManager.getCookie(url);
            Log.i(TAG, "Cookies = " + CookieStr);




                action++;
                String js2 = "var x=\"51@@toLowerCase@String@createElement@@@0xFF@return@@captcha@@charAt@document@2@@else@@innerHTML@__jsl_clearance@eval@@for@KX@d@D@@Path@@try@@length@GMT@19@0@@@@36@toString@@challenge@pathname@@@window@@05@reverse@RegExp@if@https@e@@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@U@setTimeout@@@0xEDB88320@charCodeAt@parseInt@@cookie@@search@catch@@8@DOMContentLoaded@a@@@@function@@firstChild@@@3@f@headless@g@@split@@while@@@8AaAb@Sat@@@attachEvent@@div@join@replace@addEventListener@false@@@Array@fromCharCode@@onreadystatechange@substr@1564807883@23@@Expires@match@JgSe0upZ@location@Aug@href@1@d65@chars@1500@@new@8TC89@var@k1@03@\".replace(/@*$/,\"\").split(\"@\"),y=\"a5 63=64(){4a('97.99=97.37+97.57.83(/[\\\\?|&]b-36/,\\\\'\\\\')',a1);12.55='18=91.69|2b|'+(64(){a5 39=[64(63){9 63},64(39){9 39},64(63){1b(a5 39=2b;39<63.28;39++){63[39]=53(63[39]).34(33)};9 63.82('')}],63=[((+!+[])+[]+[[]][2b])+[(-~[]+[(((+!+[])<<(+!+[])))*[((+!+[])<<(+!+[]))]]>>-~[])],((+!+[])+[]+[[]][2b])+[((+!+[])<<(+!+[]))],((+!+[])+[]+[[]][2b]),[(-~[]+[(((+!+[])<<(+!+[])))*[((+!+[])<<(+!+[]))]]>>-~[])],[-~[-~-~!!3a.6b]+[-~(+!{})-~(+!{})]*(-~[-~-~!!3a.6b])],((+!+[])+[]+[[]][2b])+((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),((+[])+[]+[[]][2b]),[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],((+!+[])+[]+[[]][2b])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),[((+!+[])<<(+!+[]))],(5a+[]),((+!+[])+[]+[[]][2b])+((+[])+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b]),((+!+[])+[]+[[]][2b])+((+!+[])+[]+[[]][2b])];1b(a5 80=2b;80<63.28;80++){63[80]=39[[9a,2b,9a,2b,9a,13,9a,13,2b,9a,2b,9a,13,9a,2b,9a,2b,9a][80]](['22','20','a6','49',[((+!+[])+[]+[[]][2b])+(-~(+!{})-~(+!{})+(-~!!3a.6b+[-~(+!{})-~(+!{})]>>-~(+!{})-~(+!{}))+[]+[[]][2b])],((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[]),[((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b])+((+!+[])+[]+[[]][2b])],((+[])+[]+[[]][2b]),((+[])+[]+[[]][2b]),'9b','%','22',[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]],[((+!+[])+[]+[[]][2b])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b])],((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][2b]),'a4','77'][63[80]])};9 63.82('')})()+';94=78, a7-98-2a 40:1:92 29;24=/;'};43((64(){26{9 !!3a.84;}58(45){9 85;}})()){12.84('5b',63,85)}15{12.7b('8b',63)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script>";
                String js1= "document.getElementById(\"userName\").value = \""+account+"\";"+
                        "document.getElementById(\"userPwd\").value = \""+password+"\";"+
                        "document.getElementById(\"btnLogin\").click();";

                webView.evaluateJavascript("javascript:" + js2, null);

            CookieStr = cookieManager.getCookie(url);
            Log.i(TAG, "Cookies = " + CookieStr);
            super.onPageFinished(view,url);

        }
    };


    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent();
        intent1.putExtra("cookies",returnStr);
        intent1.putExtra("cookietime",cookietime);
        setResult(RESULT_CANCELED,intent1);
        super.onBackPressed();
    }


    @Override
    protected void onStop() {

        super.onStop();

    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
    class JsInterface{

        @JavascriptInterface
        public void getHtmlSource(String html,String charactersets){
            Log.i(TAG,"getHtmlSource=="+html);
            Log.i(TAG,"getHtmlSource=="+charactersets);
        }
    }

}

