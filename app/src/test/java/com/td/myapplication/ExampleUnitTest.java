package com.td.myapplication;


import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.net.URLEncoder;

import static org.junit.Assert.*;

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
    public void tets(){
        String js="<script>var x=\"@Aug@@@@reverse@@g@568@1565010795@14@@location@captcha@1@substr@@e@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@@@cookie@@search@@@chars@false@@@@match@2@__jsl_clearance@@charCodeAt@var@RegExp@@div@try@yvUJWylEZGzELA8Idrwdz1wXY@@@split@1500@join@@pathname@@fromCharCode@@@8@createElement@@@15@for@addEventListener@GMT@@new@@DOMContentLoaded@d@JgSe0upZ@@attachEvent@a@36@https@Mon@document@@@@@@3@Array@Path@@@href@19@@String@0xFF@@@@length@challenge@if@13@setTimeout@@replace@@0@function@innerHTML@3D@else@charAt@@toString@eval@@@firstChild@parseInt@while@catch@onreadystatechange@return@@@window@toLowerCase@@Expires@05@@0xEDB88320@f\".replace(/@*$/,\"\").split(\"@\"),y=\"1g 1a=4f(){4a('d.3k=d.26+d.13.4c(/[\\\\?|&]e-47/,\\\\'\\\\')',23);39.11='1d=a.9|4e|'+(4f(){1g 1a=[4e],3d,5a,40='',16='32%%j',5i=4f(5a){2g(1g 3d=4e;3d<2b;3d++)5a=(5a&f)?(5h^(5a>>>f)):(5a>>>f);58 5a};55(40=1a.24().4c(2k 1h('\\\\\\\\31+','8'),4f(31){58 16.4j(31)}).22(',').24('')+'1l%4h'){5a=-f;2g(3d=4e;3d<40.46;3d++)5a=(5a>>>2b)^5i((5a^40.1f(3d))&42);48(([3f]+[1c+1c]+[((+!![])<<(+!![]))]+[~~[]]+[1c+1c]+[([-~(+!![])]+~~''>>-~(+!![]))]+[((+!![])+(+!![])<<(+!![])+(+!![]))]+[1c+1c]+[((+!![])<<(+!![]))]+[-~(+!![])+(+!![])+(-~[]|(+!![])+(+!![]))])==(5a^(-f))>>>4e)58 40;3d=4e;55(++1a[3d]===16.46){1a[3d++]=4e;48(3d===1a.46)1a[3d]=-f}}})()+';5e=38, 5f-2-3l b:49:2f 2i;3h=/;'};48((4f(){1k{58 !!5b.2h;}56(i){58 17;}})()){39.2h('30',1a,17)}4i{39.34('57',1a)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script>";
        String js1 = new String(Base64.encodeBase64(js.getBytes()));
        System.out.println(js1);

        System.out.println(URLEncoder.encode(js));


    }
}