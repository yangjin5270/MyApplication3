package com.td.myapplication;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.td.myapplication.BaiduBase64;
import com.td.myapplication.QdMain;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class QianDanThread implements Runnable{

    String id="";
    ArrayList list = new ArrayList();
    private String TAG="QianDanThread";
    public static final  int stateMsg=311;
    public static final int stateSuccessMsg=312;
    HashMap<String,String> headers = new HashMap<String, String>();
    private Handler handler;

    QianDanThread(ArrayList list1,String idi,Handler handle){
        this.id = idi;
        this.handler = handle;
        this.list = list1;

    }






    @Override
    public void run() {

            //Log.i(TAG,"run Thead>>>>>>>>>>>>>>>>>>>")
            try {
                String bodyStr  = "taskid="+this.id+"&action=jiedan";
                //Log.i(TAG,bodyStr)
                headers.put("Host","www.88887912.com");
                headers.put("Connection","keep-alive");
                headers.put("Accept","application/json, text/javascript, */*; q=0.01");

                headers.put("User-Agent",UserAgentList.getUA());//"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
                headers.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
                headers.put("Accept-Encoding","gzip, deflate");
                headers.put("Accept-Language","zh,zh-HK;q=0.9,zh-CN;q=0.8,en;q=0.7,zh-TW;q=0.6");
                headers.put("X-Requested-With","XMLHttpRequest");//X-Requested-With: XMLHttpRequest
                headers.put("Origin","http://88887912.com");
                headers.put("Referer","http://88887912.com/user/newtasklist.aspx");
                headers.put("Content-Length",String.valueOf(bodyStr.length()));


                Connection.Response re = Jsoup.connect("http://88887912.com/user/newtasklist.aspx")
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)//忽略http错误
                        .method(Connection.Method.POST)
                        .requestBody(bodyStr)
                        .headers(headers)
                        .cookies(QdMain.cookies)
                        .timeout(20000)
                        .execute();

                String str = re.body();
                //Log.i(TAG,str)您已超过今天最多接任务数量，请明天再接

                if(str.contains("超过今天最多接任")){
                    Log.i(TAG,this.id+"抢单失败原因："+str);

                    if(QdMain.state<998){
                        QdMain.state = 998;
                    }
                    sendMessage(str);

                   ;
                }else if(str.contains("被限制接单")){
                    Log.i(TAG,this.id+"抢单失败原因："+str);

                    if(QdMain.state<999){
                        QdMain.state = 999;
                    }
                    sendMessage(str);

                }
                else if(str.contains("创宇云安全")){
                    Log.i(TAG,this.id+"抢单失败原因：：线程发现创宇盾");

                    if(QdMain.state<9){
                        QdMain.state = 9;
                    }
                    sendMessage("抢单失败原因：线程发现创宇盾");

                } else if(str.contains("超过7天")){
                    Log.i(TAG,this.id+"抢单失败原因：：有货超过7天");

                    if(QdMain.state<69){
                        QdMain.state = 69;
                    }
                    sendMessage(str);

                }else if(str.contains("商家限制二次接单")){
                    Log.i(TAG,this.id+"抢单失败原因：：商家限制二次接单");
                    QdMain.blacklist.put(id,QdMain.blacklistTemp.get(id));
                    if(QdMain.state<3){
                        QdMain.state = 3;
                    }
                    writeId(id,QdMain.blacklistTemp.get(id));
                    sendMessage(str);

                }else if (str.contains("黑名单")){
                    Log.i(TAG,this.id+"抢单失败原因：：你已被该商家拉入黑名单");
                    QdMain.blacklist.put(id,QdMain.blacklistTemp.get(id));
                    if(QdMain.state<3){
                        QdMain.state = 3;
                    }
                    writeId(id,QdMain.blacklistTemp.get(id));
                    sendMessage(str);

                } else if(str.contains("本任务已被接完")){
                    Log.i(TAG,this.id+"抢单失败原因：：本任务已被接完");

                    if(QdMain.state<4){
                        QdMain.state = 4;
                    }
                    sendMessage(str);


                }else if(str.contains("接单成功")){
                    Log.i(TAG,this.id+"抢单成功");

                    if(QdMain.state<1){
                        QdMain.state = 1;
                    }
                    Message message = new Message();
                    message.what = stateSuccessMsg;
                    message.arg1 = QdMain.state;
                    message.arg2 = 1;
                    list.add(this.id);
                    message.obj = list;
                    handler.sendMessage(message);

                    //new Thread(new PlayMusic(this.script)).start();
                    //script.sleep(5000);
                    //GV.playFlag = true

                }else if(str.contains("待付款任务已超过")){
                    Log.i(TAG,this.id+"抢单失败原因：待付款任务已超过");
                    //script.print QdMain.state
                    if(QdMain.state<59){
                        QdMain.state = 59;
                    }
                    sendMessage(str);
                    //script.print QdMain.state
                }else if (str.contains("任务上限")){
                    Log.i(TAG,this.id+"抢单失败原因：任务达到账号上限");
                    //script.print QdMain.state
                    if(QdMain.state<97){
                        QdMain.state = 97;
                    }
                    sendMessage(str);

                }else{
                    Log.i(TAG,str);
                    QdMain.state = 0;
                    sendMessage(str);
                }
            } catch (Exception e) {
                QdMain.state = 8484;
                //Log.i(TAG,"线程"+e);
            }

            //GV.threadNumCount++;
    }
    public void sendMessage(String str){
        Message message = new Message();
        message.what = stateMsg;
        message.arg1 = QdMain.state;
        message.obj = this.id+"抢单失败原因："+str;
        handler.sendMessage(message);
    }
    public void writeId(String id ,String id1){
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
        String mapPath = sfd.format(new Date());
        String b64 = BaiduBase64.encode(QdMain.account.getBytes());
        File file1 = new File("/sdcard/"+b64+"maps"+mapPath+".txt");
        if (file1.exists()) {
            //printPro('Cookes信息存在，获取失效时间中>>>.'+file1.getName())
            /*String strTemp = file1.getText()

            String[] strs = strTemp.split("=")
            for (int i = 0; i < strs.size(); i++) {
                String[]strs1 = strs[i].split("|")
                GV.blacklist.put(strs1[0],strs1[1])
            }*/
            try {
                FileOutputStream out = new FileOutputStream(file1,true);
                PrintStream p = new PrintStream(out);
                p.append(id+"|"+id1+"=");
                p.flush();
                out.flush();
                p.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
