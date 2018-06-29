package com.bdtexample;

import java.util.Map;
import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public final class BDTranslate {
private static BDTranslateListener listener = null;
public static boolean opening = false;

public static void setTranslateListener(BDTranslateListener ln) {
listener = ln;
}

public static void translate(String text) {
Https https = new Https();
https.postText_t( "", text, new TranslateHttpsDelegate(), -1013, "com/bdtexample/BDTranslate$TranslateHttpsDelegate");
  }

public static class TranslateHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
String result = (String) item.get("json");
if(result != null ) {
String str = "";
String typestr = "";
try {
JSONTokener jsonParser = new JSONTokener(result); // JSONObject 
JSONObject person = (JSONObject) jsonParser.nextValue();
              if(person==null || person.getInt("status")!=0 ) { // return error message
str = "翻译错误";
} else {
typestr = person.getString("type");
if( typestr.equals("1") ) { //单词翻译
jsonParser = new JSONTokener(person.getString("result"));
JSONObject jobj = (JSONObject) jsonParser.nextValue();
if(jobj==null) {
              return;
}
str="";
JSONArray oarr = jobj.getJSONArray("content");
if(oarr!=null){
for(int i=0;i<oarr.length();i++) {
JSONArray mean = oarr.getJSONObject(i).getJSONArray("mean");
if(mean!=null) {
for(int j=0;j<mean.length();j++) {
JSONObject cont = mean.getJSONObject(j).getJSONObject("cont");
if(cont!=null) {
Iterator it = cont.keys();
if(it.hasNext()) {
str = it.next().toString();
}
}
}//for
} // is mean
                }//for
} // is oarr
} else if(typestr.equals("2")) {//全文翻译
JSONArray data = person.getJSONArray("data");
if(data!=null) {
if(data.length()>=1) {
str = data.getJSONObject(0).getString("dst");
}//if
}
} else {
str="";
}
}
            } catch (JSONException ex) { 
            // 异常处理代码 
if(listener!=null) {
listener.error("翻译错误");
}
            } catch (ClassCastException ex) { 
            // 异常处理代码 
if(listener!=null) {
listener.error("翻译错误");
}
            } catch (Exception ex) { 
            // 异常处理代码 
if(listener!=null) {
listener.error("翻译错误");
}
            } finally{
if(listener!=null) {
if(str.length()==0) {
listener.error("翻译解析错误");
} else {
listener.success(str);
}
}
            }
}
}
@Override
public void onFailed_m(int errorCode) {
if(listener!=null) {
listener.error("。时时翻译失败");
}
}	
}

public interface BDTranslateListener {
public void success(String text);
public void error(String message);
}

}
