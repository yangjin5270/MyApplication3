package com.td.myapplication;

import android.os.Message;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.os.Handler;

public class QdMain implements Runnable {

    public static final int refSuccessMsg =51;
    public static final int actionStartMsg =52;
    public static String account;
    private String TAG="QdMain";
    private Handler handler;
    private int maxSleep;
    private int minSleep;
    private int pic;

    public void setSleepFlag(boolean sleepFlag) {
        this.sleepFlag = sleepFlag;
    }

    private boolean sleepFlag;
    public static HashMap cookies = new HashMap();



    public static HashMap<String,String> blacklist = new HashMap();
    public static HashMap<String,String>blacklistTemp = new HashMap<String,String>();


    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    private boolean runFlag = true;
    public QdMain(Handler handler, int maxSleep, int minSleep, int pic, double brokerage,HashMap cookies1) {
        this.handler = handler;
        this.maxSleep = maxSleep;
        this.minSleep = minSleep;
        this.pic = pic;
        this.brokerage = brokerage;
        cookies = cookies1;

    }

    private double brokerage;
    @Override
    public void run() {
        Log.i(TAG,"mianThead start>>>>>>>>");
        Connection.Response response;
        Document document;
        Elements esGoodsPri;
        Pattern pattern;
        Matcher matcher;
        Map<String,String> headers = new HashMap<String, String>();

        headers = getFixHead();

        headers.put("Cache-Control","max-age=0");
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headers.put("Upgrade-Insecure-Requests","1");
        String url = "http://www.88887912.com/user/newtasklist.aspx";
        sleepFlag = false;
        while(runFlag){
            while(sleepFlag){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
                //Log.i(TAG,response.body());
                esGoodsPri = document.getElementsByAttributeValue("class","moamd2");//这里商品购买价格
                if(esGoodsPri.size()>0){
                    Message message = new Message();
                    message.what = refSuccessMsg;
                    message.arg1 = esGoodsPri.size();
                    handler.sendMessage(message);
                    Elements esBrokerage = document.getElementsByAttributeValue("class","moamd3");//商品佣金
                    Elements postDatas = document.getElementsByAttributeValue("class","ljqdn");//mhmkdd
                    Elements taskId = document.getElementsByAttributeValue("class","mhmkdd");//


                    for (int i = 0; i < esGoodsPri.size(); i++) {

                        String strt1 =  taskId.get(i).getElementsByAttributeValue("href","#").get(0).text();
                        String str2  =  postDatas.get(i).attr("onclick");
                        pattern = Pattern.compile("[0-9]{7}");
                        matcher = pattern.matcher(str2);
                        if(matcher.find()){
                            str2 =matcher.group();
                        }
                        if(blacklist.size()>0){

                            if(blacklist.get(str2).equals(strt1)){
                                //print("查询记录有商家二次接单限制，不抢本单")
                                continue;
                            }
                        }

                        String strt = esGoodsPri.get(i).text();
                        pattern = Pattern.compile("[0-9]{1,4}\\.[0-9]{1,2}|[0-9]{1,5}");
                        matcher = pattern.matcher(strt);
                        String strtemp="";
                        if(matcher.find()){
                            strt = matcher.group();
                            strtemp = strt;
                        }

                        if( Double.valueOf(strt)>Double.valueOf(pic)){
                            //printPro("第（${i}）个商品价格为${strt}")
                            continue;
                        }

                        strt = esBrokerage.get(i).text();
                        pattern = Pattern.compile("[0-9]{1,4}\\.[0-9]{1,2}|[0-9]{1,5}");
                        matcher = pattern.matcher(strt);
                        if(matcher.find()){
                            strt = matcher.group();
                        }
                        //这里还要判读接单佣金

                        if(Double.valueOf(strt)<Double.valueOf(brokerage)){
                            continue;
                        }

                        blacklistTemp.put(str2,strt1);
                        new Thread(new QianDanThread(str2,handler)).start();
                        Message message1 = new Message();
                        message1.what=actionStartMsg;
                        message1.obj="开始抢["+str2+"]商品:价格"+strtemp+" 佣金:"+strt;
                        handler.sendMessage(message1);
                        //printPro()


                    }



                }
            } catch (IOException e) {
                Log.i(TAG,"平台网络访问超时");
                e.printStackTrace();
            }
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
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


    void init(){
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            String mapPath = sfd.format(new Date());
            String b64 = BaiduBase64.encode(account.getBytes());
            File file1 = new File("/sdcard/"+b64+"maps"+mapPath+".txt");
            Log.i(TAG,"载入商品过滤名单.....");
            if (file1.exists()) {
                //printPro('Cookes信息存在，获取失效时间中>>>.'+file1.getName())


                char[] chars = new char[1024];
                FileReader fileReader = new FileReader(file1);
                StringBuffer stringBuffer = new StringBuffer();
                int i;
                while((i = fileReader.read(chars))!=-1){
                    stringBuffer.append(chars,0,i);
                }
                Log.i(TAG,stringBuffer.toString());
                String strTemp = stringBuffer.toString();
                String[] strs = strTemp.split("=");
                //print strs.size()
                if(strs.length>1){
                    for (int j = 0; j < strs.length; i++) {
                        if((j+1)==strs.length){
                            break;
                        }
                        String[]strs1 = strs[i].split("\\|");
                        QdMain.blacklist.put(strs1[0],strs1[1]);
                    }
                }

            }else{
                    Log.i(TAG, "建立商品过滤文件"+file1.createNewFile());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


/*    class QianDanThread implements Runnable {

        private String TAG="QianDanThread";
        @Override
        public void run() {
            Log.i(TAG,"QianDanThread run");
        }
    }*/

}
