package com.td.myapplication;

import com.alibaba.fastjson.JSON;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

class Ifeige {


    public static String userUrl ="http://u.ifeige.cn/api/message/send-user";//post user url
    public static String groupUrl="https://u.ifeige.cn/api/message/send";//post group url
    public static final String secret="662bc6d190b602485be74ba3b6f1ddd9";//key字符串
    public static final String template_id="5uZIvSm5GAdUR1X25HNpjuOp6jSiL88v4opn4a4GLa0";//模板字符串
    public static final String app_key="1b5ffc091c2224398d20f89d00a34654";//分组字符串
    public static final String titile="淘单消息中心";//消息标题


    public static boolean sendMessageUser(String uid,String msg,String account){
        HashMap<String,Object> infoMap = new HashMap<>();
        HashMap<String,Object> dataMap = new HashMap<>();
        HashMap<String,String> dataSubMap = new HashMap<>();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString();
        infoMap.put("secret",secret);
        infoMap.put("app_key",app_key);
        infoMap.put("template_id",template_id);

        infoMap.put("uid",uid);
        infoMap.put("url","");


        HashMap<String,String> dataSubMap1 = new HashMap<>();
        dataSubMap1.put("value",titile);
        dataSubMap1.put("color","#173177");
        dataMap.put("first",dataSubMap1);


        HashMap<String,String> dataSubMap2 = new HashMap<>();
        dataSubMap2.put("value",msg);
        dataSubMap2.put("color","#173177");
        dataMap.put("keyword1",dataSubMap2);

        HashMap<String,String> dataSubMap3 = new HashMap<>();
        dataSubMap3.put("value",account);
        dataSubMap3.put("color","#173177");
        dataMap.put("keyword2",dataSubMap3);

        HashMap<String,String> dataSubMap4 = new HashMap<>();
        dataSubMap4.put("value",time);
        dataSubMap4.put("color","#173177");
        dataMap.put("keyword3",dataSubMap4);

        HashMap<String,String> dataSubMap5 = new HashMap<>();
        dataSubMap5.put("value","");
        dataSubMap5.put("color","#173177");
        dataMap.put("remark",dataSubMap5);

        infoMap.put("data",dataMap);
        /* System.out.println(JSON.toJSONString(infoMap));
          System.out.println(testStr);
          System.out.println(JSON.toJSONString(infoMap).equals(testStr));*/

        try {

            Connection.Response re = Jsoup.connect(userUrl).header("Content-Type","application/json").ignoreContentType(true).ignoreHttpErrors(true).method(Connection.Method.POST).requestBody(JSON.toJSONString(infoMap)).execute();
            //System.out.println(re.body());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }



}
