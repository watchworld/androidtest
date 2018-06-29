package net.czy.manager;

import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

public abstract class HttpsDelegate {

private Handler myHandler = new Handler() {
@Override
public void handleMessage(Message msg) {
super.handleMessage(msg);
switch (msg.what) {   
case 123:
callbackFunction_m(msg.arg1, msg.arg2, (Map<String,Object>)msg.obj);
break;
case 124:
onFailed_m(msg.arg1);
break;
case 125:
callbackFunctionList_m(msg.arg1, msg.arg2, (List<Map<String,Object>>)msg.obj);
break;
}
          }   
     };  

public void callbackFunction(int how, int resultCode, Map<String,Object> item) {
Message message = new Message();
message.what = 123;
message.arg1 = how;
message.arg2 = resultCode;
message.obj = item;
myHandler.sendMessage(message);   
}

public void callbackFunctionList(int how, int resultCode, List<Map<String,Object>> l) {
Message message = new Message();
message.what = 125;
message.arg1 = how;
message.arg2 = resultCode;
message.obj = l;
myHandler.sendMessage(message);   
}

public void onFailed(int errorCode) {
Message message = new Message();
message.what = 124;
message.arg1 = errorCode;
myHandler.sendMessage(message);   
}

public void callbackFunction_m(int how, int resultCode, Map<String,Object> item){}
public void callbackFunctionList_m(int how, int resultCode, List<Map<String,Object>> l){}
public void onFailed_m(int errorCode){}
}
