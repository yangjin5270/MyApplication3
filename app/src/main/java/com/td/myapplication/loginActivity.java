package com.td.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

public class loginActivity extends BaseActivity {

    private WebView webView;
    private String TAG= "loginActivity";
    private String account;
    private String password;
    private int action=1;
    private String returnStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        webView = (WebView)findViewById(R.id.loginWebView);
        webView.setWebViewClient(webViewClient);
        Intent intent = getIntent();
        Toast.makeText(this,intent.getStringExtra("account"),Toast.LENGTH_SHORT).show();
        account = intent.getStringExtra("account");
        password = intent.getStringExtra("password");
        webView = (WebView)findViewById(R.id.loginWebView);
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings = webView .getSettings();


        //支持获取手势焦点，输入用户名、密码或其他
        webView.requestFocusFromTouch();

        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JsInterface(),"JsInterface");

        webView.loadUrl("http://www.88887912.com/wap/login.aspx");


    }
    private WebViewClient webViewClient = new WebViewClient(){

        String CookieStr;
        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            CookieStr = cookieManager.getCookie(url);
            Log.i(TAG, "Cookies = " + CookieStr);



            if(action==1){
                action++;

                String js1= "document.getElementById(\"userName\").value = \""+account+"\";"+
                        "document.getElementById(\"userPwd\").value = \""+password+"\";"+
                        "document.getElementById(\"btnLogin\").click();";

                webView.evaluateJavascript("javascript:" + js1, null);

            }else{
                //TODO 登录成功
                Log.i(TAG, "登录完成"+CookieStr);
                returnStr = CookieStr;
                Intent intent1 = new Intent();
                intent1.putExtra("cookies",returnStr);
                setResult(RESULT_OK,intent1);
                finish();
            }
            super.onPageFinished(view,url);

        }
    };


    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent();
        intent1.putExtra("cookies",returnStr);
        setResult(RESULT_OK,intent1);
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
