package com.td.myapplication;

import android.os.Message;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

public class QdMain implements Runnable {

    public static final int refSuccessMsg =51;
    private String TAG="QdMain";
    private Handler handler;
    private int maxSleep;
    private int minSleep;
    private int pic;
    private HashMap cookies = new HashMap();

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    private boolean runFlag = true;
    public QdMain(Handler handler, int maxSleep, int minSleep, int pic, double brokerage,HashMap cookies) {
        this.handler = handler;
        this.maxSleep = maxSleep;
        this.minSleep = minSleep;
        this.pic = pic;
        this.brokerage = brokerage;
        this.cookies = cookies;

    }

    private double brokerage;
    @Override

    public void run() {
        Log.i(TAG,"mianThead start>>>>>>>>");
        Connection.Response response;
        Document document;
        Elements esGoodsPri;
        Map<String,String> headers = new HashMap<String, String>();
        headers = getFixHead();

        headers.put("Cache-Control","max-age=0");
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headers.put("Upgrade-Insecure-Requests","1");
        String url = "http://www.88887912.com/user/newtasklist.aspx";
        while(runFlag){
            try {
                response = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).method(Connection.Method.GET)
                        .headers(headers)
                        .cookies(cookies)
                        .timeout(30000).execute();
                String str = response.cookie("reftime");
                if(str!=null){
                    cookies.put("reftime",str);
                }
                str = response.cookie("refcount");
                if(str!=null){
                   cookies.put("refcount",str);
                }

                document = Jsoup.parse(response.body(),"UTF-8");
                Log.i(TAG,response.body());
                esGoodsPri = document.getElementsByAttributeValue("class","moamd2");//这里商品购买价格
                if(esGoodsPri.size()>0){
                    Message message = new Message();
                    message.what = refSuccessMsg;
                    message.arg1 = esGoodsPri.size();
                    handler.sendMessage(message);
                    runFlag = false;
                    Log.i(TAG,"exit thead");
                }
            } catch (IOException e) {
                Log.i(TAG,"平台网络访问超时");
                e.printStackTrace();
            }

        }

    }




    public HashMap getFixHead(){
        HashMap<String,String> headers = new HashMap<String, String>();
        headers.put("Host", "www.88887912.com");
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh,zh-HK;q=0.9,zh-CN;q=0.8,en;q=0.7,zh-TW;q=0.6");
        //TODO 用户代理需要加入多项随机
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        return headers;
    }




}
