package com.td.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUtil {

    public static boolean  verifyCookies(long cookies_time) {

        if(cookies_time>0){
            cookies_time =  cookies_time - System.currentTimeMillis();

            if (cookies_time < 1200000) {
                //printPro('登录cookies过期！启动重启登录1')
                return true;
            }
        }
        return false;
    }

    public static int  randomRange(int min,int max){
        return (int)((max-min+1)*Math.random()+min);
    }

    public static String getTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sf.format(new Date());    }
}
