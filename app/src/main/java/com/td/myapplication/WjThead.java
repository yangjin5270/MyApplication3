package com.td.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WjThead implements Runnable {

    private String account;
    private String password;
    private String guid;
    private Handler handler;
    public WjThead(String account, String password,String guid,Handler handler) {
        this.account = account;
        this.password = password;
        this.guid = guid;
        WjNetVerify.setAccount(account);
        WjNetVerify.setPassword(password);

    }

    @Override
    public void run() {
        Log.i("WjThead","WjThead开始运行");
        while(true){


            try {
                String restr = WjNetVerify.stateControl(guid,"1");
                //printPro(restr)

                if(restr.equals("0")){
                    QdMain.runFlag = false;
                    Message message2 = new Message();
                    message2.what=51;
                    message2.obj="账号超过多开数，脚本停止";
                    handler.sendMessage(message2);
                    break;
                }
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Log.i("WjThead","异常以后处理");
                e.printStackTrace();
            }
        }

    }


}
