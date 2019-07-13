package com.td.myapplication;

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
}
