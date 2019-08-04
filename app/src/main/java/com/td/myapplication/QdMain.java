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
    public static final int reLoginMsg=553;
    public static final int jsMsg=555;
    public static String account;
    private String TAG="QdMain";
    private Handler handler;
    private int maxSleep;
    private int minSleep;
    private int pic;
    private long tim;
    public void setMaxSleep(int maxSleep) {
        this.maxSleep = maxSleep;
    }

    public void setMinSleep(int minSleep) {
        this.minSleep = minSleep;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setBrokerage(double brokerage) {
        this.brokerage = brokerage;
    }

    private String uid;
    public static int state;

    public static boolean theadState = false;



    public static HashMap cookies = new HashMap();



    public static HashMap<String,String> blacklist = new HashMap();
    public static HashMap<String,String>blacklistTemp = new HashMap<String,String>();




    public static boolean runFlag = true;
    public static  boolean sleepFlag= true;
    public QdMain(Handler handler, String account,int maxSleep, int minSleep, int pic, double brokerage,String uid,HashMap cookies1,long tim1) {
        this.account=account;
        this.handler = handler;
        this.maxSleep = maxSleep;
        this.minSleep = minSleep;
        this.pic = pic;
        this.brokerage = brokerage;
        this.uid= uid;
        cookies = cookies1;
        this.tim = tim1;
        init();
        Message message = new Message();
        message.what=actionStartMsg;
        message.obj="载入商品过滤文件...";
        handler.sendMessage(message);

    }

    private double brokerage;

    void pause(){
        boolean sendf = true;
        while(sleepFlag){
            if(sendf){
                Message message2 = new Message();
                message2.what=actionStartMsg;
                message2.obj="抢单暂定，随时可启动\n";
                handler.sendMessage(message2);
                sendf = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
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
            //Log.i(TAG,"main loop");

            pause();
            switch (state){
                case 998:
                    处理最多接任();
                    break;
                case 999:
                    处理限制接单();
                    break;
                case 9:
                    处理创宇盾();
                    break;
                case 59:
                    处理待付款();
                    break;
                case 69:
                    处理未收货();
                    break;
                case 97:
                    处理已做完();
                    break;
                default:
                    break;
            }

            try {
                response = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).method(Connection.Method.GET)
                        .headers(headers)
                        .cookies(cookies)
                        .timeout(30000).execute();
                String str = response.cookie("reftime");
                Log.i(TAG,response.body());
                if(response.body().toString().contains("<script>")){
                    Message message = new Message();
                    message.what = jsMsg;
                    handler.sendMessage(message);
                }
                if(str!=null){
                    cookies.put("reftime",str);
                }
                str = response.cookie("refcount");
                if(str!=null){
                   cookies.put("refcount",str);
                }

                document = Jsoup.parse(response.body(),"UTF-8");

                esGoodsPri = document.getElementsByAttributeValue("class","moamd2");//这里商品购买价格
                Message message = new Message();
                message.what = refSuccessMsg;
                message.arg1 = esGoodsPri.size();
                handler.sendMessage(message);
                if(esGoodsPri.size()>0){
                    state = -1;
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

                            if(blacklist.get(str2)!=null&&blacklist.get(str2).equals(strt1)){
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
                        ArrayList list = new ArrayList();
                        list.add(strtemp);
                        list.add(strt);
                        new Thread(new QianDanThread(list,str2,handler)).start();
                        Message message1 = new Message();
                        message1.what=actionStartMsg;
                        message1.obj="开始抢["+str2+"]商品:价格"+strtemp+" 佣金:"+strt;
                        handler.sendMessage(message1);
                        //printPro()

                    }

                }
            } catch (Exception e) {
                Log.i(TAG,"平台网络访问超时");
                e.printStackTrace();
            }
            try {
                if(tim-System.currentTimeMillis()<20*60*1000){
                    Message message3 = new Message();
                    message3.what=reLoginMsg;
                    handler.sendMessage(message3);
                    sleepFlag = true;
                }
                int tim = MyUtil.randomRange(minSleep*1000,maxSleep*1000);
                Message message1 = new Message();
                message1.what=actionStartMsg;
                message1.obj="刷新随机间隔"+tim+"毫秒";
                handler.sendMessage(message1);
                Thread.sleep(tim);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pause();

        }

    }

    void  处理最多接任(){
        Message message1 = new Message();
        message1.what=actionStartMsg;
        message1.obj="账号：您已超过今天最多接任务数量，请明天再接！";
        handler.sendMessage(message1);
        Ifeige.sendMessageUser(uid,"抢单：账号：您已超过今天最多接任务数量，请明天再接！",account);
        //Message.sendMessageWX("",GV.sendMessageWXName)
        while(true){
            //toast("今日已做完所有订单")
            //toastPro("您已超过今天最多接任务数量");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void  处理已做完(){
        Message message1 = new Message();
        message1.what=actionStartMsg;
        message1.obj="今日已做完所有订单！";
        handler.sendMessage(message1);
        Ifeige.sendMessageUser(uid,"抢单：今日已做完所有订单！",account);

        while(true){
            //toast("今日已做完所有订单")
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    void  处理未收货(){

        Message message1 = new Message();
        message1.what=actionStartMsg;
        message1.obj="你有超过7天未收货，请收货后，在抢单！";
        handler.sendMessage(message1);
        Ifeige.sendMessageUser(uid,"抢单：你有超过7天未收货，请收货后，在抢单！",account);

        while(true){
            //toast("今日已做完所有订单")
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    void 处理限制接单(){

        Message message1 = new Message();
        message1.what=actionStartMsg;
        message1.obj="你被限制接单！";
        handler.sendMessage(message1);
        Ifeige.sendMessageUser(uid,"抢单：你被限制接单！",account);

        while(true){
            //toast("今日已做完所有订单")
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    void 处理创宇盾(){
        Message message1 = new Message();
        message1.what=actionStartMsg;
        message1.obj="处理创宇盾！暂定15秒";
        handler.sendMessage(message1);

        for (int i = 0; i < 15; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    void 处理待付款(){
        Map<String, String> headers = new HashMap<String, String>();

        Connection.Response re=null;

        headers.put("Host", "www.88887912.com");
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh,zh-HK;q=0.9,zh-CN;q=0.8,en;q=0.7,zh-TW;q=0.6");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headers.put("Referer", "http://www.88887912.com/user/MyRecTaskList.aspx");
        while(true){
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                re = Jsoup.connect("http://www.88887912.com/user/MyRecTaskList.aspx?state=6")
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)//忽略http错误
                        .method(Connection.Method.GET)
                        .headers(headers)
                        .cookies(cookies)
                        .timeout(20000)
                        .execute();

                String body = re.body();
                Document d = Jsoup.parse(body);
                Elements e = d.select("body > div.mid > div.hjfjd > ul.unjdnnd > li:nth-child(2) > a > div > span");
                if(Integer.valueOf(e.get(0).text())<3){
                    Message message1 = new Message();
                    message1.what=actionStartMsg;
                    message1.obj="代付款任务小于3个，开始抢单";
                    handler.sendMessage(message1);
                    break;
                }else{
                    Message message1 = new Message();
                    message1.what=actionStartMsg;
                    message1.obj="代付款任务大于等于3个，做单后自动开始抢单";
                    handler.sendMessage(message1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // printPro re.body()//这里要做异常处理

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
                    for (int j = 0; j < strs.length;j ++) {
                        if((j+1)==strs.length){
                            break;
                        }
                        String[]strs1 = strs[j].split("\\|");
                        QdMain.blacklist.put(strs1[0],strs1[1]);
                    }
                }

            }else{
                    Log.i(TAG, "建立商品过滤文件"+file1.createNewFile());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "初始化数据完成");
    }


/*    class QianDanThread implements Runnable {

        private String TAG="QianDanThread";
        @Override
        public void run() {
            Log.i(TAG,"QianDanThread run");
        }
    }*/

}
