package com.xinyang.screenreader;

import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import android.widget.Toast;
import android.os.Build;
import android.Manifest;  
import android.content.pm.PackageManager;  
import android.support.v4.app.ActivityCompat;  
import android.support.v4.content.ContextCompat;  
import android.net.Uri;  
import android.app.AlertDialog;
import android.content.DialogInterface;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.Map;
import net.czy.manager.HttpsDelegate;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.czy.tools.Compared;
import com.czyusercenter.data.User;
import com.google.android.marvin.talkback8.TalkBackService;
import com.tencent.stat.StatService;

import cn.woblog.testthirdpartyfunction.activity.QQActivity;
import cn.woblog.testthirdpartyfunction.activity.SinaActivity;
import cn.woblog.testthirdpartyfunction.activity.WxActivity;

public class MainActivity extends FragmentActivity {
private MyFragment myFragment = null;
private ApkFragment apkFragment = null;
private SettingsFragment settingsFragment = null;
private ToolsFragment toolsFragment = null;
private FragmentManager fragmentManager;//fragment管理器
private boolean mCallback = false;
private String mCallbackAction;
private String readyCallPhone = null;

private SinaActivity sinaActivity = new SinaActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
setContentView(R.layout.activity_main);

Compared compared = new Compared();
if(compared.compare(this)<=0){
        TalkBackService.getInstance().suspendTalkBack();
            finish();
return;
}

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionType.REQUEST_CODE_ASK_ALL);
            }  
}

 fragmentManager = getSupportFragmentManager();//获取fragment管理器

RadioGroup tab = (RadioGroup) findViewById(R.id.radio_group_tab);
tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                   @Override
public void onCheckedChanged(RadioGroup r, int id) {
switch(r.getCheckedRadioButtonId()) {
case R.id.radio_button_apk:
changeFragment(0);
break;
 case R.id.radio_button_tools:
 changeFragment(1);
 break;
case R.id.radio_button_settings:
changeFragment(2);
settingsFragment.setUserVisibleHint(true);
break;
case R.id.radio_button_my:
changeFragment(3);
break;
}
}
});

changeFragment(2);

Button btnAgentContacts = (Button) findViewById(R.id.btnAgentContacts);
btnAgentContacts.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
settingsFragment.getKFList(new ContactsHttpsDelegate(), "com/xinyang/screenreader/MainActivity$ContactsHttpsDelegate");
}
});

Button btnShared = (Button) findViewById(R.id.btnShared);
btnShared.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
User user = new User();
if(user.is()) {
AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this );
builder.setIcon( android.R.drawable.ic_dialog_info );
builder.setItems(new String[]{ getString(R.string.shared_qq), getString(R.string.shared_qq_), getString(R.string.shared_wb), getString(R.string.shared_wx), getString(R.string.shared_wx_)}, 
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, final int which) {
Https https = new Https();
final SharedHttpsDelegate hd = new SharedHttpsDelegate();
hd.setWhich(which);
https.getText_t( "", hd, 3004, "com/xinyang/screenreader/MainActivity$SharedHttpsDelegate");
}
});
builder.setNegativeButton("取消", null);
builder.show();
} else {
Toast.makeText(MainActivity.this, "目前必须登录后才能分享。", Toast.LENGTH_SHORT).show();  
}
}
});

sinaActivity.onCreate(this, savedInstanceState);

  }//end onCreate

@Override
protected void onResume() {
	super.onResume();
	StatService.onResume(this);
}

@Override
protected void onPause() {
	super.onPause();
	StatService.onPause(this);
}

@Override
protected void onStart() {
super.onStart();
// if(mCallback) {
//callApis();
//}
}

/**
 * 隐藏所有fragment
 */
private void hideFragment( FragmentTransaction fragmentTransaction) {
if( apkFragment != null ) {
fragmentTransaction.hide(apkFragment);
}
if( toolsFragment != null ) {
fragmentTransaction.hide(toolsFragment);
}
if( settingsFragment != null ) {
fragmentTransaction.hide(settingsFragment);
}
if( myFragment != null ) {
fragmentTransaction.hide(myFragment);
}
}

/**
 * 根据情况显示fragment
 * @param sw 哪种情况
 * @return
*/
public void changeFragment(int sw) {
FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
hideFragment(fragmentTransaction);
switch(sw) {
case 0://apk市场
if(apkFragment==null) {
apkFragment = new ApkFragment();
fragmentTransaction.add( R.id.main_fragment, apkFragment);
}else{
fragmentTransaction.show( apkFragment );
}
break;
case 1://实用工具
if(toolsFragment==null) {
toolsFragment = new ToolsFragment();
fragmentTransaction.add( R.id.main_fragment, toolsFragment);
}else{
fragmentTransaction.show( toolsFragment );
}
break;
case 2://所有设置
if(settingsFragment==null) {
settingsFragment = new SettingsFragment();
fragmentTransaction.add( R.id.main_fragment, settingsFragment);
}else{
fragmentTransaction.show( settingsFragment );
}
break;
case 3://我的
if(myFragment==null) {
myFragment = new MyFragment();
fragmentTransaction.add( R.id.main_fragment, myFragment);
}else{
fragmentTransaction.show( myFragment );
}
break;
}
fragmentTransaction.commitAllowingStateLoss();
} 

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
super.onActivityResult(requestCode,resultCode,data);
/*
switch( requestCode ) {
case 1: //登录成功返回
if( data != null ) {
Bundle bundle = data.getExtras();
if(bundle!=null) {
if(bundle.getBoolean("close")){
finish();
}
}
}
break;
default:
break;
}
*/
}

/**
 * 
*/
/*
public void callApis() {
User user = new User();

if(mCallback) {
if(user.is()) {
Intent in = new Intent(mCallbackAction);
Bundle bun = new Bundle();
bun.putBoolean("login", true);
bun.putInt("id", user.getId());
bun.putInt("groups", user.getGroups());
bun.putInt("age", user.getAge());
bun.putBoolean("sex", user.getSex());
bun.putString("phone", user.getPhone());
bun.putString("nickname", user.getNickname());
bun.putString("email", user.getEmail());
bun.putString("name", user.getName());
bun.putString("md", user.getYY());
bun.putString("token", user.getToken());
in.putExtras(bun);
sendBroadcast( in );
}
mCallback = false;
mCallbackAction = null;
finish();
return;
}


Intent intent = getIntent();
if(intent!=null) {
Bundle bundle = intent.getExtras();
if(bundle!=null && bundle.getInt("what") == 1 ) {
if(!user.is()) {
mCallback = true;
mCallbackAction = bundle.getString("USER_ACTION");
intent = new Intent( this, LoginActivity.class);
startActivityForResult(intent, 1);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
return;
}else{
Intent in = new Intent(bundle.getString("USER_ACTION"));
Bundle bun = new Bundle();
bun.putBoolean("login", true);
bun.putInt("id", user.getId());
bun.putInt("groups", user.getGroups());
bun.putInt("age", user.getAge());
bun.putBoolean("sex", user.getSex());
bun.putString("phone", user.getPhone());
bun.putString("nickname", user.getNickname());
bun.putString("email", user.getEmail());
bun.putString("name", user.getName());
bun.putString("md", user.getYY());
bun.putString("token", user.getToken());
in.putExtras(bun);
sendBroadcast( in );
finish();
return;
}
        }
}

if(!user.is()) {
intent = new Intent( this, LoginActivity.class);
startActivityForResult(intent, 1);
}
} //end callApis
*/

public class ContactsHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
if(how == 3000 ) {
try {
JSONArray arr = new JSONArray((String)item.get("json"));
String[] items = new String[arr.length()];
final String[] phones = new String[arr.length()];
for(int i=0; i< arr.length(); i++) {
JSONObject obj = arr.getJSONObject(i);
phones[i] = obj.getString("phone");
items[i] = obj.getString("name") + "(" +obj.getString("phone") + ")";
}
AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this );
builder.setIcon( android.R.drawable.ic_dialog_info );
builder.setTitle("请选择客服。");
builder.setItems(items, 
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, final int which) {
if(which>=0&&which<phones.length) {
requestPermission( phones[which] );
}
}
});
builder.setNegativeButton("取消", null);
builder.show();
} catch(Exception e) {
}
}
}
@Override
public void onFailed_m(int errorCode) {
Toast.makeText(MainActivity.this, "获取客服列表失败，请检查网络。", Toast.LENGTH_SHORT).show();  
}
}

    /** 
     * required to call phone 
     */  
private void requestPermission(String phone) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
readyCallPhone = phone;
ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, RequestPermissionType.REQUEST_CODE_ASK_CALL_PHONE);
return;
            }  
            else              {                  callPhone(phone);
            }  
        }  
        else  
        {  
            callPhone(phone);
        }  
    }  

/** 
 * Created by wzj on 2017/2/19. 
 */  
public interface RequestPermissionType {
int REQUEST_CODE_ASK_CALL_PHONE = 100;
int REQUEST_CODE_ASK_ALL = 101;
}  

    /** 
     * call phone
     */  
private void callPhone(String phone) {
        Intent intent = new Intent();  
        intent.setAction(Intent.ACTION_CALL);  
        intent.setData(Uri.parse("tel:"+phone));
        startActivity(intent);  
    }  

    /** 
     * 注册权限申请回调 
     * @param requestCode 申请码 
     * @param permissions 申请的权限 
     * @param grantResults 结果 
     */  
    @Override  
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)  
        {  
case RequestPermissionType.REQUEST_CODE_ASK_CALL_PHONE:  
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)  
                {  
callPhone( readyCallPhone );
                }  
                else  
                {  
                    Toast.makeText(MainActivity.this, "请打开允许拨号的权限。", Toast.LENGTH_SHORT).show();  
                }  
                break;  
case RequestPermissionType.REQUEST_CODE_ASK_ALL: // all checked
for(int i=0;i<grantResults.length;i++) {
if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
Toast.makeText(this, "请通过心阳世界所需要的权限，拒绝后将无法使用心阳世界。", Toast.LENGTH_SHORT).show();  
finish();
break;
}
} // for
break;
            default:  
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);  
break;
        }  
    }  

public class SharedHttpsDelegate extends HttpsDelegate {
private int sw = 0; // qq

public void setWhich(int sw) {
this.sw = sw;
}

@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
String json = (String) item.get("json");
if( json != null ) {
try {
JSONObject obj = new JSONObject(json);
if(null != obj && obj.has("url") ) {
String url = obj.getString("url") + "&channel=";
switch(sw) {
case 0: // qq
url += "3";
QQActivity.shareOnlyImageOnQZone(MainActivity.this, url);
break;
case 1: // qq zoom
url += "4";
QQActivity.shareOnlyImageOnQQ(MainActivity.this, url);
break;
case 2: // wb
url += "5";
sinaActivity.testShareImage(MainActivity.this, url);
break;
case 3:
url += "1";
WxActivity.share2Wx(MainActivity.this, true, url);
break;
case 4: // wx
url += "2";
WxActivity.share2Wx(MainActivity.this, false, url);
break;
} // switch
}
					}catch(Exception e){
}
}
}
@Override
public void onFailed_m(int errorCode) {
                    Toast.makeText(MainActivity.this, "获取分享资源失败，请检查网络。", Toast.LENGTH_SHORT).show();  
}
}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
sinaActivity.onNowIntent(intent);
    }

}//end class
