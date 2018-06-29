package net.czy.manager;

import com.xinyang.screenreader.BuildConfig;
import android.support.v4.content.FileProvider;
import android.os.Build;
import com.xinyang.screenreader.R;
import android.app.ProgressDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class InstallApk {
public static boolean needUpdate = false;
private ProgressDialog pBar = null;
private Context mContext = null;
private Handler handler = new Handler();
private String filename = "CZYScreenReader.apk";

public InstallApk(Context context) {
this.mContext = context;
}

/**
 * download and install
*/
public void installApk(final String url, final boolean isShowBar) {
if(isShowBar) {
filename = "CZYOther.apk";
pBar = new ProgressDialog( mContext );
pBar.setIcon(R.drawable.ic_stat_info);
pBar.setTitle("正在下载");
pBar.setMessage("请稍候…");
pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
pBar.setMax(100);
pBar.setProgress(0);
pBar.setIndeterminate(false);
pBar.setCancelable(false);
pBar.show();
}
// started the new thread
new Thread() {
@Override
public void run() {
HttpClient client = new DefaultHttpClient();
HttpGet get = new HttpGet(url);
 HttpResponse response;
 try {
 response = client.execute(get);
 HttpEntity entity = response.getEntity();
Long length = entity.getContentLength();
 InputStream is = entity.getContent();
FileOutputStream fileOutputStream = null;

if ( is != null) {
File file = new File(
Environment.getExternalStorageDirectory(),
 filename);
fileOutputStream = new FileOutputStream(file);

byte[] buf = new byte[1024];
Integer ch = -1;
Long count = 0l;
while ((ch = is.read(buf)) != -1) {
fileOutputStream.write(buf, 0, ch);
count += ch;

//显示进度条
if(isShowBar) {
Double pr = count.doubleValue() / length.doubleValue() * 100;
pBar.setProgress(pr.intValue());
}
} //while
 if (fileOutputStream != null) {
fileOutputStream.flush();
 fileOutputStream.close();
}
if(isShowBar) {
handler.post(new Runnable() {
@Override
public void run() {
pBar.cancel();
 }
 });
}
// open apk file
down();
}// is open
 } catch (final ClientProtocolException e) { 
 // TODO Auto-generated catch block
 e.printStackTrace();
if(isShowBar) {
handler.post(new Runnable() {
@Override
public void run() {
pBar.cancel();
 }
 });
}
 } catch (final IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
if(isShowBar) {
handler.post(new Runnable() {
@Override
public void run() {
pBar.cancel();
 }
 });
}
 }
 }
 }.start();
 }

/**
 * turnback main thread 
*/
private void down() {
handler.post(new Runnable() {
@Override
public void run() {
update();
 }
 });
 }

/**
 * started to install
*/
private void update() {
new Handler().postDelayed( new Runnable() {
@Override
public void run() {
InstallApk.needUpdate = true; // must to update
}
}, 1000*60*5);

Intent intent = new Intent();
String apkFile = Environment.getExternalStorageDirectory().getPath() + File.separator + filename;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
android.util.Log.i("czys", "installing");
Uri contentUri = FileProvider.getUriForFile(mContext, "com.xinyang.screenreader.fileprovider", new File(apkFile));
intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
} else {
intent.setAction(Intent.ACTION_VIEW); 
  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
intent.setDataAndType(Uri.fromFile(new File(apkFile)), "application/vnd.android.package-archive");
}
mContext.startActivity(intent);
}

public int checkControlAccessibility(Context context,String packageName,int versionCode) {
PackageManager packagemanager = context.getPackageManager();
Iterator<PackageInfo> iterator = packagemanager.getInstalledPackages(0).iterator();
PackageInfo packageinfo;
do
{
if(!iterator.hasNext()) {
return 0;
}
packageinfo = iterator.next();
} while( !packageinfo.packageName.equals(packageName));

int j;
j = packageinfo.versionCode;
return j >= versionCode ? 1 : -1;
    }

public class UpdateSoftwareHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
String json = (String) item.get("json");
if(json!=null) {
checkUpdate(json);
}
}
};

/**
 * download xml 
 * @param url
 * @return
*/
public void downJson() {
Https https = new Https();
https.getText_t( "CZYTalkback.json", new UpdateSoftwareHttpsDelegate(), -999999, "net/czy/manager/InstallApk$UpdateSoftwareHttpsDelegate");
 }

/**
 * check exist
 */
    public boolean fileIsExists(String strFile) {
        try {
            File f=new File(strFile);
            if(!f.exists()) {
                    return false;
            }
        }        catch (Exception e)        {
            return false;
        }
        return true;
    }
    
/**
 * check need to update
  * @param String url
 */
public void checkUpdate(String jsonStr) {
int version;
String url;
try {
JSONObject json = new JSONObject(jsonStr);
version = json.getInt("version");
url = json.getString("url");
} catch( JSONException e) {
e.printStackTrace();
return;
}
switch(checkControlAccessibility(mContext, "com.xinyang.screenreader", version)) {
case 1:// ok
break;
case -1: // need to update
try {
String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + filename;
if( fileIsExists(filePath) ) {
PackageManager packageManager = mContext.getPackageManager();
PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
if(version <= packageInfo.versionCode){
down();
return;
}
}
} catch(Exception e) {
}
installApk(url,false);
break;
default: // not installing
break;
}
}

}//end class
