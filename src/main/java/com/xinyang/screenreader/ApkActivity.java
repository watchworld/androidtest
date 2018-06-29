package com.xinyang.screenreader;

import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.czy.manager.InstallApk;

public class ApkActivity extends Activity {
private List<Map<String,Object> > items;
private SimpleAdapter adapter;
private String url;
private String packageName;
private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.apk_activity);

items = new ArrayList<Map<String,Object> >();

Intent intent = getIntent();
Bundle bundle = intent.getExtras();

String strSize = "未知";
try {
int size = Integer.parseInt(bundle.getString("size"));
if(size >= 1024*1024) {
BigDecimal bg = new BigDecimal((Double.valueOf(size)/(1024*1024))).setScale(2, RoundingMode.UP);
strSize = bg.doubleValue() + "MB";
} else if(size >= 1024 ) {
BigDecimal bg = new BigDecimal((Double.valueOf(size)/1024)).setScale(2, RoundingMode.UP);
strSize = bg.doubleValue() + "KB";
} else {
strSize = size + "B";
}
} catch(Exception e) {
}

packageName = bundle.getString("package");
url = bundle.getString("url");
versionCode = Integer.parseInt(bundle.getString("code"));

Map<String,Object> it = new HashMap<String,Object>();
it.put("text", "软件名： " + bundle.getString("name"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "版本： " + bundle.getString("version"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "开发者： " + bundle.getString("developer"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "使用教程： " + bundle.getString("tutorial"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "下载金币： " + bundle.getString("amount"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "大小： " + strSize);
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "下载次数： " + bundle.getString("down"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "软件介绍： " + bundle.getString("describes"));
items.add(it);
it = new HashMap<String,Object>();
it.put("text", "上传日期： " + bundle.getString("created_at"));
items.add(it);
it = new HashMap<String,Object>();

InstallApk installApk = new InstallApk(this);
switch( installApk.checkControlAccessibility(this, packageName, versionCode)  ) {
case 0:
it.put("text", "点击下载安装");
break;
case 1:
it.put("text", "点击打开");
break;
case -1:
it.put("text", "点击更新升级");
break;
}
items.add(it);

ListView listView =  (ListView) findViewById( R.id.listView);
adapter = new SimpleAdapter(this, items, R.layout.apk_info_list_item, new String[]{"text"}, new int[]{R.id.text_view_name});
listView.setAdapter(adapter);

listView.setOnItemClickListener( new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> ada, View v, int position, long id) {
if( position == items.size()-1 ) {
InstallApk installApk = new InstallApk(ApkActivity.this);
switch(installApk.checkControlAccessibility(ApkActivity.this, packageName, versionCode)) {
case 1:
Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
if(intent != null) {
startActivity(intent);
}
break;
case 0:
case -1:
installApk.installApk(url, true);
break;
}
}
}
});


Button btnBack = (Button) findViewById(R.id.back_button);
btnBack.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
finish();
}
});

}

@Override
protected void onResume() {
	super.onResume();
if(adapter!=null && items!=null&&items.size()>0) {
Map<String,Object> it = items.get(items.size()-1);
InstallApk installApk = new InstallApk(this);
switch( installApk.checkControlAccessibility(this, packageName, versionCode)  ) {
case 0:
it.put("text", "点击下载安装");
break;
case 1:
it.put("text", "点击打开");
break;
case -1:
it.put("text", "点击更新升级");
break;
}
adapter.notifyDataSetChanged();
}
}

}// end class
