package com.msm;

import android.view.ViewGroup;
import java.util.List;
import android.view.accessibility.AccessibilityEvent;
import com.msm.Config;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.MotionEvent;
import android.graphics.drawable.BitmapDrawable;
import android.widget.PopupWindow;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.provider.Settings;

import com.xinyang.screenreader.R;
import com.msm.ui.CheckUI;
import com.android.talkback.TalkBackPreferencesActivity;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface;
import com.google.android.marvin.talkback8.TalkBackService;
import android.os.Build;

public class NimbleMenu {
//构造函数
public static void setNimbleMenu(Context context) {
m_context = context;
m_assist = new MsmAssist(m_context);
}

//消息处理者
public static Handler m_handler=new Handler()
{
@Override
public void handleMessage(Message msg)
{
switch(msg.what)
{
case Config.TIME:
m_assist.seeTime();
break;
case Config.SUN:
m_assist.seeSun();
break;
case Config.LUNAR:
m_assist.seeLunar();
break;
case Config.BATTERY:
m_assist.seeBattery();
break;
case Config.NETWORK:
m_assist.seeNetwork();
break;
}
}
};

/**
 * 显示弹窗
*/
public static void showPopupWindow()
{
close();

//加载弹窗所用的布局文件
View popupWindowView = LayoutInflater.from(m_context).inflate(R.layout.popupwindow, null);

//为弹窗中的按钮设置监听
popupWindowView.findViewById(R.id.IDTV_XINYANG).setOnClickListener(m_clickListener);
m_tvTime=(TextView) popupWindowView.findViewById(R.id.IDTV_TIME);
m_tvSun=(TextView) popupWindowView.findViewById(R.id.IDTV_SUN);
m_tvLunar=(TextView) popupWindowView.findViewById(R.id.IDTV_LUNAR);
m_tvBattery=(TextView) popupWindowView.findViewById(R.id.IDTV_BATTERY);
m_tvNetwork=(TextView) popupWindowView.findViewById(R.id.IDTV_NETWORK);
m_tvSearch=(TextView) popupWindowView.findViewById(R.id.IDTV_SEARCH);
Button btnReturn = (Button) popupWindowView.findViewById(R.id.IDB_RETURN);

//设置点击监听
m_tvTime.setOnClickListener(m_clickListener);
m_tvSun.setOnClickListener(m_clickListener);
m_tvLunar.setOnClickListener(m_clickListener);
m_tvBattery.setOnClickListener(m_clickListener);
m_tvNetwork.setOnClickListener(m_clickListener);
m_tvSearch.setOnClickListener(m_clickListener);
btnReturn.setOnClickListener(m_clickListener);

//有没有父VIEW
dialog = new AlertDialog.Builder(m_context)
 .setView(popupWindowView)
.setOnDismissListener(  new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            TalkBackService service = TalkBackService.getInstance();
            if (service != null) {
service.resetFocusedNode(250);
service.getRingerModeAndScreenMonitor().unregisterDialog(dialog);
}
dialog = null;
isOpening = false;
        }
    })
.setCancelable(true)
.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY);
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            }
            // Only need to register overlay dialogs (since they'll cover the lock screen).
            TalkBackService service = TalkBackService.getInstance();
            if (service != null) {
                service.getRingerModeAndScreenMonitor().registerDialog(dialog);
            }
        dialog.show();
isOpening = true;
//end_showPopupWindow
}

/*changeTitle: 更改文本试图标题，指定ID的控件更改后，其他控件标题恢复原状
*id: 发生变化的控件ID,该ID是Config中自定义的常量值，并非真正的控件ID
*content: 更新到控件的内容，只在电池有效，因其是接受广播才能创建的*/
public static void changeTitle(final int id, String content)
{
String ttime=m_context.getResources().getString(R.string.SR_TIME);
String tsun=m_context.getResources().getString(R.string.SR_SUN);
String tlunar=m_context.getResources().getString(R.string.SR_LUNAR);
String tbattery=m_context.getResources().getString(R.string.SR_BATTERY);
String tnetwork=m_context.getResources().getString(R.string.SR_NETWORK);

switch(id)
{
case Config.TIME:
{
m_tvTime.setText(ttime + m_assist.seeTime());
m_tvSun.setText(tsun);
m_tvLunar.setText(tlunar);
m_tvBattery.setText(tbattery);
m_tvNetwork.setText(tnetwork);

m_tvTime.setVisibility(View.INVISIBLE);
m_tvTime.setVisibility(View.VISIBLE);
}
break;
case Config.SUN:
{
m_tvTime.setText(ttime);
m_tvSun.setText(tsun + m_assist.seeSun());
m_tvLunar.setText(tlunar);
m_tvBattery.setText(tbattery);
m_tvNetwork.setText(tnetwork);
}
break;
case Config.LUNAR:
{
m_tvTime.setText(ttime);
m_tvSun.setText(tsun);
m_tvLunar.setText(tlunar + m_assist.seeLunar());
m_tvBattery.setText(tbattery);
m_tvNetwork.setText(tnetwork);
}
break;
case Config.BATTERY:
{
m_tvTime.setText(ttime);
m_tvSun.setText(tsun);
m_tvLunar.setText(tlunar);
m_tvBattery.setText(tbattery + content);
m_tvNetwork.setText(tnetwork);
}
break;
case Config.NETWORK:
{
m_tvTime.setText(ttime);
m_tvSun.setText(tsun );
m_tvLunar.setText(tlunar);
m_tvBattery.setText(tbattery);
m_tvNetwork.setText(tnetwork + m_assist.seeNetwork());
}
break;
}
//end_changeTitle
}


////////////////////////////////////////////////////////////////////////////////////////////////////
//监听器

//点击监听器
static View.OnClickListener m_clickListener=new View.OnClickListener()
{
@Override
public void onClick(View view)
{
if(dialog!=null) {
switch(view.getId())
{
case R.id.IDTV_XINYANG:
{
//心阳设置
Intent intent = new Intent( m_context, TalkBackPreferencesActivity.class);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
m_context.startActivity(intent);
}
break;
case R.id.IDB_RETURN:
break;
case R.id.IDTV_TIME:
{
Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
m_context.startActivity(intent);
}
break;
case R.id.IDTV_SUN:
{
Intent intent=new Intent(m_context, CheckUI.class);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
intent.putExtra(Config.CHECK_TYPE, Config.CHECK_SUN);
m_context.startActivity(intent);
}
break;
case R.id.IDTV_LUNAR:
{
Intent intent=new Intent(m_context, CheckUI.class);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
intent.putExtra(Config.CHECK_TYPE, Config.CHECK_LUNAR);
m_context.startActivity(intent);
}
break;
case R.id.IDTV_SEARCH:
{
Intent intent=new Intent(m_context, CheckUI.class);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
intent.putExtra(Config.CHECK_TYPE, -1);
m_context.startActivity(intent);
}
break;
case R.id.IDTV_NETWORK:
{
Intent intent=new Intent(Settings.ACTION_WIFI_SETTINGS);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
m_context.startActivity(intent);
}
break;
} // switch
dialog.dismiss();
dialog = null;
isOpening = false;
}
}
};

public static void close() {
if(dialog!=null) {
dialog.dismiss();
dialog = null;
isOpening = false;
}
}

//method
/*getTextProcess: 获取事件源文本的处理方法
*event: 收到的AccessibilityEvent事件*/
public static void getTextProcess(AccessibilityEvent event)
{
List <CharSequence> texts=event.getText();
String content="";
Message msg=new Message();

if(texts==null || texts.isEmpty())
return;

for(CharSequence text: texts)
{
msg.what=-1;
content=text.toString();

if(content.contains("时间"))
msg.what=Config.TIME;
else if(content.contains("公历日期"))
msg.what=Config.SUN;
else if(content.contains("农历日期"))
msg.what=Config.LUNAR;
else if(content.contains("电量"))
msg.what=Config.BATTERY;
else if(content.contains("无线网"))
msg.what=Config.NETWORK;

if(-1 != msg.what)
NimbleMenu.m_handler.sendMessage(msg);
}

//end_getTextProcess
}

////////////////////////////////////////////////////////////////////////////////////////
//data
public static boolean isOpening = false;
public static AlertDialog dialog = null;
protected static Context m_context;
private static MsmAssist m_assist;//辅助对象

static TextView m_tvTime;
static TextView m_tvSun;
static TextView m_tvLunar;
public static TextView m_tvBattery;
static TextView m_tvNetwork;
static TextView m_tvSearch;
}
