//电池广播接收类
package com.msm;

import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.widget.Toast;

class BatteryReceiver extends BroadcastReceiver
{
public BatteryReceiver(Context context)
{
super();
this.m_context=context;
}

@Override
public void onReceive(Context context, Intent intent)
{
Config config=new Config();
Message msg=new Message();
String message;
int currently=intent.getExtras().getInt("level");
int total=intent.getExtras().getInt("scale");
message=String.format("当前电量百分之%d", currently * 100 / total);
switch(intent.getExtras().getInt("status"))
{
case BatteryManager.BATTERY_STATUS_CHARGING:
message += "正在充电";
break;
case BatteryManager.BATTERY_STATUS_FULL:
message += "电池以充满";
break;
}

Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
msg.what=config.EXIT_BATTERY_BROADCAST;
MsmAssist.m_handler.sendMessage(msg);
//end_onReceive
}

////////////////////////////////////////////////////////////
//data
private Context m_context;
}
