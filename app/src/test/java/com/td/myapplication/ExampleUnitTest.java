package com.td.myapplication;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.*;
import org.apache.commons.codec.Encoder;

import java.io.IOException;
import java.util.HashMap;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void base(){
        HashMap headers = new HashMap();
        String ba ="<script>var x=\"@@substr@9syZEz5R2dydAazCfSW@firstChild@search@@Mon@@@@Array@g@fromCharCode@@0xFF@__jsl_clearance@while@document@1564967741@@@@charAt@@Aug@@e@toString@41@@pathname@RegExp@@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@length@setTimeout@@@false@@3D@addEventListener@window@@0@@location@innerHTML@try@@@@catch@charCodeAt@return@else@2F2VyD8@@36@@0xEDB88320@@@15@JgSe0upZ@join@captcha@717@@new@parseInt@@@chars@@createElement@replace@@f@Expires@@@@1500@@@div@19@challenge@for@05@href@@headless@@@@Path@@@eval@onreadystatechange@function@a@GMT@match@@1@cookie@@var@https@02@@@toLowerCase@if@String@reverse@split@8@@DOMContentLoaded@attachEvent@d@\".replace(/@*$/,\"\").split(\"@\"),y=\"53 24=4h(){1g('25.46=25.1a+25.6.3d(/[\\\\?|&]33-43/,\\\\'\\\\')',3k);j.51='h=k.34|23|'+(4h(){53 24=[23],57,4d,5i='',3a='31%%1d',3f=4h(4d){44(53 57=23;57<5d;57++)4d=(4d&50)?(2j^(4d>>>50)):(4d>>>50);2d 4d};i(5i=24.32().3d(36 1b('\\\\\\\\5h+','d'),4h(5h){2d 3a.12(5h)}).5c(',').32('')+'4%2f%1l'){4d=-50;44(57=23;57<5i.1f;57++)4d=(4d>>>5d)^3f((4d^5i.2c(57))&g);59((((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][23])+((-~(+!{})-~(+!{})<<-~(+!{}))+[]+[])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][23])+((+[])+[]+[[]][23])+[((+!+[])<<(+!+[]))]+(5d+[])+((((+!+[])<<(+!+[]))^-~(+!{}))+[]+[[]][23])+[(-~[]+[((+!+[])<<(+!+[]))])/[((+!+[])<<(+!+[]))]]+((+[])+[]+[[]][23])+[-~[-~-~!!21.48]+[-~(+!{})-~(+!{})]*(-~[-~-~!!21.48])])==(4d^(-50))>>>23)2d 5i;57=23;i(++24[57]===3a.1f){24[57++]=23;59(57===24.1f)24[57]=-50}}})()+';3g=8, 45-14-42 55:30:18 4j;4c=/;'};59((4h(){27{2d !!21.20;}2b(16){2d 1j;}})()){j.20('5f',24,1j)}2e{j.5g('4g',24)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script>";
        //System.out.println(BaiduBase64.encode(ba.getBytes()));
        String result = Base64.encodeBase64String(ba.getBytes());
        headers.put("Content-Type","application/x-www-form-urlencoded");
        System.out.println(result);
        String bodyStr="jsStr="+result;
        try {
            Connection.Response re = Jsoup.connect("http://47.92.24.210:40600/js-j2v8/dealWithJs/getData")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)//忽略http错误
                    .method(Connection.Method.POST)
                    .requestBody(bodyStr)
                    .headers(headers)
                    .timeout(20000)
                    .execute();
            System.out.println(re.body());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}