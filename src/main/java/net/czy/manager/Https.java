package net.czy.manager;

import java.util.Map;

public class Https {
public native void getText(String params, HttpsDelegate delegate,int how, String classPath);
public native void postText(String params,String postField, HttpsDelegate delegate,int how, String classPath);
public native void download(String params,String path,String filename,String objectId,HttpsDelegate delegate);
public native void upload(String filepath, Map<String,String> postField, HttpsDelegate delegate,int how, String classPath);
public native void put(String url, String filepath, Map<String,String> postField, HttpsDelegate delegate,int how, String classPath);

public native boolean hasDownloadObject(String objectId,HttpsDelegate delegate);
public native void removeDownloadObject(String objectId);

public void getText_t(final String params,final HttpsDelegate delegate, final int how, final String classPath) {
new Thread(new Runnable() { 
@Override
public void run() {
getText(params, delegate, how,classPath);
}
}).start();
}

public void postText_t(final String params, final String postField, final HttpsDelegate delegate, final int how, final String classPath) {
new Thread(new Runnable() { 
@Override
public void run() {
postText(params, postField, delegate, how,classPath);
}
}).start();
}

public void upload_t(final String filepath, final Map<String,String> postField, final HttpsDelegate delegate, final int how, final String classPath) {
new Thread(new Runnable() { 
@Override
public void run() {
upload(filepath, postField, delegate, how,classPath);
}
}).start();
}

public void put_t(final String url, final String filepath, final Map<String,String> postField, final HttpsDelegate delegate, final int how, final String classPath) {
new Thread(new Runnable() { 
@Override
public void run() {
put(url, filepath, postField, delegate, how,classPath);
}
}).start();
}

}
