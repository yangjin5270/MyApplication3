package com.td.myapplication;


import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class WjNetVerify {



    public static final String wjUrl="http://web-dx.3600d.net:83/w536646/api.php?";
    public static final String projectName="10002";//软件编号

    public static final String version="1.0.1";
    public static String machineCode="";
    public static String gameAccount="";
    public static String account="";
    public static String password="";




    public static void setMachineCode(String mc){
        machineCode = mc;
    }

    public static void setGameAccount(String ga){
        gameAccount = ga;
    }

    public static void setAccount(String ac) {
        account = ac;
    }

    public static void setPassword(String pa) {
        password = MD5Util.MD5Encode(pa,"utf-8");
    }

    /*
    登录参数说明
    参数1：软件的编号，如：0001,0002,0003.....

    参数2：用户账号

    参数3：MD5一次加密的用户密码（卡密直接登录时可留空）

    参数4：软件版本，软件的版本号，用于判断软件强制更新时使用

    参数5：机器码，用于控制用户异地登录

    参数6：游戏号，用户卡密直接登录，并且需要绑定游戏号时才需要填写

   参数7：返回信息值，1=剩余秒数，2=剩余点数，3=允许几开,4=游戏号，5=上次登陆ip，6=邮箱，7=上次登录机器码，8=上次登录时间，9=用户权限，10=验证码(用于取回附加字符的必须参数)，11=到期时间，12=用户备注，13=注册时间

    */

    public static HashMap login(String returnType){
        Connection.Response re;
        HashMap<String,String> infoMap = new HashMap();
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append(wjUrl);
        loginUrl.append("name=zdy_login");
        loginUrl.append("&c1=");loginUrl.append(projectName);
        loginUrl.append("&c2=");loginUrl.append(account);
        loginUrl.append("&c3=");loginUrl.append(password);
        loginUrl.append("&c4=");loginUrl.append(version);
        loginUrl.append("&c7=");loginUrl.append(returnType);

        try {
            re = Jsoup.connect(loginUrl.toString()).ignoreHttpErrors(true).ignoreContentType(true).method(Connection.Method.GET).timeout(30000).execute();
            String returnStr = re.body();
            //System.out.println(returnStr);
            //用户已过期
            if(returnStr.contains("密码错误")||returnStr.contains("账号不存在")||returnStr.contains("用户已过期")){
                infoMap.put("infoType","error");
                infoMap.put("message",returnStr);
                return infoMap;
            }
            String[] strs = returnStr.split("<\\|>");
            if(strs.length>1&&strs[1].equals("1")){
                infoMap.put("infoType","success");
                infoMap.put("message",strs[0]);
                return infoMap;
            }else{
                infoMap.put("infoType","error");
                infoMap.put("message",returnStr);
                return infoMap;
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        infoMap.put("infoType","error");
        infoMap.put("message","未知错误");
        return infoMap;
    }

    /*
    中文名称：在线状态控制

        接口作用：当客户端以远程API接口[login]登录时，通过使用此接口来控制用户的登录数量，并且检测用户的在线、到期、封号等状态。

        参数数量：6

        接口原型：zx([软件编号],[用户账号],[用户密码MD5],[在线标识],[需要删除标识],[操作类型])<返回执行结果>

           参数1：软件编号，软件的编号，如：10001,10002,10003。。。

           参数2：用户账号，可以是用户注册的账号，也可以是卡密直接登陆

           参数3：用户密码，注册的账号的密码的MD5（小写），卡密直接登录的可以忽略此参数

           参数4：在线标识，全球唯一标志（GUID），由字母数字组成，最大16位长度，要求此标志在所有设备所有时间里只能出现一次，永不出现重复。程序启动时生成，在结束运行前不变。

           参数5：需要删除的在线标识，当用户软件意外退出导致的在线标识没有被删除 ，为了防止顶掉正常的在线标识，

                  所以需要把意外退出的标识删掉（当参数6为0时，将此标识写到本地文件，当参数6为2时将本地文件删除，

                  下次参数6为0时，检测本地文件是否存在在线标识记录，如果有记录的话就把这个本地在线标识一起提交，

                  这样就会增加新在线标识，删除旧标识，保证软件在线数量的准确性，防止在线的软件被踢掉）

           参数6：操作类型，

                 0= 增加在线标识，调用Login接口登录成功后，使用此功能增加软件的在线标识，如果在线标识数量超出用户< /FONT>

                  允许登录软件的数量， 则会删掉激活时间最早的在线标识（参数6的值为1时，将会刷新在线标识的激活时间到当前时间）

                 1= 检测在线标识，软件登录成功后，软件正常运行的情况下，循环调用此功能来检测软件是否合法和 < /FONT>

                   用户账号的封停、到期、被删除等状态。

                 2= 删除在线标识，软件退出的时候执行此操作，删除在线标识，表示软件已经退出了，为相同账号登录其他软件时空出< /FONT>

                  允许登录的数量 。

                 3= 查询用户在线标识数量，这个功能一般用不到，显示账号登录软件的大概数量。< /FONT>

        执行结果 返回值：

         0 = 在线标识不存在，表示用户超出允许登录数量在线标识被删除了，或者用户登录成功后没有成功添加在线标识

         1 = 增加标识成功、在线标识正常、删除标识成功（对应参数6的值分别为 0 1 2 时操作成功的值）

         2 = 当参数6为0，增加新在线标识的时候，如果标识存在了，就会返回这个值

         -1 = 账号不存在

         -2 = 账号已经过期

         -3 = 账号被封

         -4 = 密码错误

         -100 = 未知错误，如：数据库故障、系统故障、文件损坏等问题造成的错误

         参数6为3时，返回账号在线标识的数量（不保证100%准确，因为有软件意外退出没删除在线标识的情况）
    * */
    public static String stateControl(String guid,String controlType){
        Connection.Response re;
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append(wjUrl);
        loginUrl.append("name=zdy_zx");
        loginUrl.append("&c1=");loginUrl.append(projectName);
        loginUrl.append("&c2=");loginUrl.append(account);
        loginUrl.append("&c3=");loginUrl.append(password);
        if(controlType.equals("2")){
            loginUrl.append("&c4=");loginUrl.append(guid);
            loginUrl.append("&c5=");loginUrl.append(guid);
        }else{
            loginUrl.append("&c4=");loginUrl.append(guid);
        }


        loginUrl.append("&c6=");loginUrl.append(controlType);
        try {
            re = Jsoup.connect(loginUrl.toString()).ignoreHttpErrors(true).ignoreContentType(true).method(Connection.Method.GET).timeout(30000).execute();

            String returnStr = re.body();
            //System.out.println(returnStr);
            //用户已过期
            return returnStr;

        } catch (IOException e) {
            e.printStackTrace();

        }

        return "";
    }
}
