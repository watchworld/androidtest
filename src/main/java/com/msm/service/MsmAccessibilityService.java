package com.msm.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.content.Context;
import android.os.Message;
import android.os.Handler;

import java.util.List;

import com.msm.NimbleMenu;
import com.msm.Config;

public class MsmAccessibilityService extends AccessibilityService
{
public MsmAccessibilityService()
{super();}

@Override
public void onServiceConnected()
{
//end_onServiceContected
}

//accessibility事件回调
@Override
public void onAccessibilityEvent(AccessibilityEvent event)
{
//根据事件类型处理
switch(event.getEventType())
{
case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
getTextProcess(event);
break;
}
//end_onAccessibilityEvent
}

//accessibility服务打断回调
@Override
public void onInterrupt()
{
//end_onInterrupt
}

///////////////////////////////////////////////////////////////
//method
/*getTextProcess: 获取事件源文本的处理方法
*event: 收到的AccessibilityEvent事件*/
private void getTextProcess(AccessibilityEvent event)
{
List <CharSequence> texts=event.getText();
String content="";
Message msg=new Message();

if(texts.isEmpty())
return;

for(CharSequence text: texts)
{
msg.what=-1;
content=text.toString();

if(content.contains("北京时间"))
msg.what=Config.TIME;
else if(content.contains("国历日期"))
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

//////////////////////////////////////////////////////////
//data
}
