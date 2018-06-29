/************************************************************
*这是提供辅助功能的类
*功能包括
*seeTime: 看当前时间
*seeSun: 看当前阳历日期
*seeLunar: 看当前农历日期
*seeBatter: 看当前手机电量
*seeNetwork: 看当前网络状态
*seePhone: 看手机信号
*checkSun: 查阳历
*checkLunar: 查农历
************************************************************/

package com.msm;

import com.android.talkback.SpeechController;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import com.google.android.marvin.talkback8.TalkBackService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import com.xinyang.screenreader.R;

public class MsmAssist
{
 static {
//加载lunar.so
System.loadLibrary("lunar");
 }

public MsmAssist(Context context)
{
this.m_context=context;
}

/*消息处理-释放动态注册的电池广播*/
public static Handler m_handler = new Handler()
{
public void handleMessage(Message msg)
{

if(Config.EXIT_BATTERY_BROADCAST == msg.what)
{
if(null != m_receiver && null != m_context)
m_context.unregisterReceiver(m_receiver);
}

//end_handleMessage
}
};


/*message: 要显示的信息字串*/
public static void showMessage(String message)
{
        TalkBackService service = TalkBackService.getInstance();
        if (service != null) {
SpeechController sc = service.getSpeechController();
if(sc!=null) {
sc.speak( message, SpeechController.QUEUE_MODE_UNINTERRUPTIBLE, 0, null);
}
}
}

//看当前时间
public String seeTime()
{
Calendar calendar=Calendar.getInstance();
String date=DateFormat.format("yyyy-MM-dd-kk-mm-ss", calendar.getTime()).toString();
String [] dates=date.split("-");
String time=dates[3]+"点" +dates[4]+"分" +dates[5]+"秒";

showMessage(time);
return time;
//end_seeTime
}

//看当前阳历日期
public String seeSun()
{
Calendar calendar=Calendar.getInstance();
String date=DateFormat.format("yyyy-MM-dd-kk-mm-ss", calendar.getTime()).toString();
String [] dates=date.split("-");
String sun=dates[0]+"年" +dates[1]+"月" +dates[2]+"日";

switch(calendar.get(Calendar.DAY_OF_WEEK))
{
case 1:
sun += "星期日";
break;
case 2:
sun += "星期1";
break;
case 3:
sun += "星期2";
break;
case 4:
sun += "星期3";
break;
case 5:
sun += "星期4";
break;
case 6:
sun += "星期5";
break;
case 7:
sun += "星期6";
break;
}

sun += sunFestival(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
showMessage(sun);
return sun;
//end_seeSun
}

//看当前阴历日期
public String seeLunar()
{
Lunar lunar=new Lunar();
Calendar calendar=Calendar.getInstance();
String date=DateFormat.format("yyyy-MM-dd-kk-mm-ss", calendar.getTime()).toString();
String [] dates=date.split("-");
int year=0, month=0, day=0;
String info;

lunar.create();
lunar.seeLunar(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
year=lunar.lunarYear();
month=lunar.lunarMonth();
day=lunar.lunarDay();
lunar.destroy();

info=createLunarDateInfo(year, month, day);
if(!info.contains("非法"))
info += lunarFestival(year, month, day);

showMessage(info);
return info;
//end_seeLunar
}

//看电量信息
public void seeBattery()
{
IntentFilter filter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
m_receiver=new BatteryReceiver(m_context);
m_context.registerReceiver(m_receiver, filter);
//end_seeBattery
}

//看网络状态
public String seeNetwork()
{
ConnectivityManager cm=(ConnectivityManager) m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
WifiManager wm=(WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
WifiInfo wi=null;


try
{wi=wm.getConnectionInfo();}
catch(Exception e)
{copyMessage(e.getMessage());}

if(null == wi)
{
//showMessage("无效的WI");
return "获取无线状态失败";
}

//检查是否是WIFI或蜂窝移动
boolean bWifi=false;
boolean bMobile=false;
try
{
bWifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
bMobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
}
catch(Exception e)
{
copyMessage(e.getMessage());
}

//获取网络信号、速度等状态信息
String strNetworkInfo="";
if(bWifi && wi.getBSSID() != null)
{strNetworkInfo= String.format("无线网%s, 信号%d格，共5格", wi.getSSID(), WifiManager.calculateSignalLevel(wi.getRssi(), 5));}
//else if(bMobile && wi.getBSSID() != null)
//{strNetworkInfo= String.format("移动数据%s, 信号%d格，共5格", wi.getSSID(), WifiManager.calculateSignalLevel(wi.getRssi(), 5));}
else if(bMobile && !bWifi)
{strNetworkInfo="未连接无线网";}
else
{strNetworkInfo="未知网络";}

showMessage(strNetworkInfo);
return strNetworkInfo;
//end_seeNetwork
}

//看手机信号
public void seePhone()
{
TelephonyManager tm;
PhoneStateListener psl;


//end_seePhone
}

//看信号
public void seeSignal()
{

//end_seeSignal
}

/*checkSun: 查阳历
*year: 指定的农历年分
*month: 指定的农历月份
*day: 指定的农历日子
*返回值： 指定农历年月日对应的阳历日期信息字符串*/
public String checkSun(final int year, final int month, final int day)
{
String info="";
Lunar lunar=new Lunar();

info=lunar.getSunInfo(year, month, day);
return info;
//end_checkSun
}

/*checkLunar: 查农历
*year: 指定的阳历年份
*month: 指定的阳历月份
*day: 指定的阳历日子
*返回值： 指定阳历日期对应的农历日期信息字符串*/
public String checkLunar(final int year, final int month, final int day)
{
Lunar lunar=new Lunar();
int lunar_year=0, lunar_month=0, lunar_day=0;

lunar.create();
lunar.seeLunar(year, month, day);
lunar_year=lunar.lunarYear();
lunar_month=lunar.lunarMonth();
lunar_day=lunar.lunarDay();
lunar.destroy();

return createLunarDateInfo(lunar_year, lunar_month, lunar_day);
//end_checkLunar
}

/*createLunarDateInfo: 创建农历日期信息字符串
*year: 农历年
*month: 农历月
*day: 农历日
*返回值： 农历年月日的信息字符串*/
public String createLunarDateInfo(final int year, final int month, final int day)
{
String info="";
if(0 == year || 0 == month || 0 == day)
return "日期非法";

info=String.valueOf(year) + "年";
if(1 == month)
info += "正月";
else if(13 == month)
info += "闰正月";
else if(month >= 2 && month <= 12)
info += String.format("%d月", month);
else if(month >= 14 && month <= 24)
info += String.format("闰%d月", month - 12);

if(day >= 1 && day <= 10)
info += String.format("初%d", day);
else
info += String.format("%d", day);

return info;
//end_createLunarDateInfo
}

/*hideKeyboard: 隐藏键盘
*instance: 软键盘所在活动的实例*/
public void hideKeyboard(Activity instance)
{
InputMethodManager imm=(InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
if(null != imm)
imm.hideSoftInputFromWindow(instance.getWindow().getDecorView().getWindowToken(), 0);
//end_hideKeyboard
}

/*showKeyboard: 显示键盘
*instance: 键盘拥有者活动实例*/
public void showKeyboard(Activity instance)
{
InputMethodManager imm=(InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
if(null != imm)
imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
//end_showKeyboard
}

//获取节气信息字符串数组
public String [] getSunPositionInfos()
{
Lunar lunar=new Lunar();
Calendar calendar=Calendar.getInstance();
String [] dates=DateFormat.format("yyyy-MM-dd", calendar.getTime()).toString().split("-");
int [] datas=lunar.getSunPosition(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
int length=datas.length / 3;
String [] infos=new String[length];
int year=Integer.parseInt(dates[0]);
int sy=year, sm=0, sd=0;
int ly=0, lm=0, ld=0;

if(length <= 0)
return infos;

lunar.create();
infos[0]=firstJqInfo(datas[0], datas[1], datas[2], datas[3], datas[4], datas[5]);
for(int i=1, j=3; i<length; i++)
{
sm=datas[j+1];
sd=datas[j+2];
if(datas[j] > 19
    && (datas[j+1] == 1 ||datas[j+1] == 2)
    && sy == year)
sy += 1;

lunar.seeLunar(sy, sm, sd);
ly=lunar.lunarYear();
lm=lunar.lunarMonth();
ld=lunar.lunarDay();

infos[i]=String.format("%s： 农历%s, 国历%d月%d日", getJqString(datas[j]), createLunarDateInfo(ly, lm, ld), datas[j+1], datas[j+2]);
j+=3;
}

lunar.destroy();
return infos;
//end_getSunPositionInfos
}

//getFestivalInfos: 获取节日信息字符串数组
public ArrayList <String> getFestivalInfos()
{
ArrayList <String> infos=new ArrayList <String>();
Lunar lunar=new Lunar();
Calendar calendar=Calendar.getInstance();
String date=DateFormat.format("yyyy-MM-dd", calendar.getTime()).toString();
String [] dates=date.split("-");
int sy=Integer.parseInt(dates[0]), sm=Integer.parseInt(dates[1]), sd=Integer.parseInt(dates[2]);
int ly=0, lm=0, ld=0;
String info="";
int xny=0, cx=0;

lunar.create();
lunar.seeLunar(sy, sm, sd);
ly=lunar.lunarYear();
lm=lunar.lunarMonth();
ld=lunar.lunarDay();
lunar.destroy();

info=todayLunarFestival(lm, ld);
if(!info.isEmpty())
infos.add(info);

info=todaySunFestival(sm ,sd);
if(!info.isEmpty())
infos.add(info);

xny=lunar.lunarInterval(ly, lm, ld, ly, 12, 23);
cx=lunar.lunarInterval(ly, lm, ld, ly+1, 1, 1)-1;

if(1 == lm && ld < 15)
infos.add(String.format("正月十五元宵节，距离元宵节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 1, 15)));
if(lm < 2 || (2 == lm && ld < 2))
infos.add(String.format("二月初二龙抬头，距离龙抬头还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 2, 2)));
if(lm < 5 || (5 == lm && ld < 5))
infos.add(String.format("五月初五端午节，距离端午节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 5, 5)));
if(lm < 7 || (7 == lm && ld < 7))
infos.add(String.format("七月初七七夕节，距离七夕节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 7, 7)));
if(lm < 7 || (7 == lm && ld < 15))
infos.add(String.format("农历七月十五中元节，距离中元节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 7, 15)));
if(lm < 8 || (8 == lm && ld < 15))
infos.add(String.format("农历八月十五中秋节，距离中秋节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 8, 15)));
if(lm < 9 || (9 == lm && ld < 9))
infos.add(String.format("九月初九重阳节，距离重阳节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 9, 9)));
if(lm < 12 || (12 == lm && ld < 8))
infos.add(String.format("腊月初八腊八节，距离腊八节还有%d天", lunar.lunarInterval(ly, lm, ld, ly, 12, 8)));
if(lm < 12 || (12 == lm && ld < 23))
infos.add(String.format("腊月二十三小年夜，距离小年夜还有%d天",xny));
if(lm < 12 || (12 == lm && ld < (cx - xny + 23)))
infos.add(String.format("腊月%d除夕，距离除夕还有%d天", (cx - xny + 23), cx));

if(sm < 2 || (2 == sm && sd < 14))
infos.add(String.format("国历2月14情人节，距离情人节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 2, 14)));
if(sm < 3 || (3 == sm && sd < 8))
infos.add(String.format("国历3月8日妇女节，距离妇女节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 3, 8)));
if(sm < 4 || (4 == sm && sd < 1))
infos.add(String.format("国历4月1日愚人节，距离愚人节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 4, 1)));
if(sm < 5 || (5 == sm && sd < 1))
infos.add(String.format("国历5月1日劳动节，距离劳动节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 5, 1)));
if(sm < 6 || (6 == sm && sd < 1))
infos.add(String.format("国历6月1日儿童节，距离儿童节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 6, 1)));
if(sm < 7 || (7 == sm && sd <1))
infos.add(String.format("国历7月1日中国共产党建党纪念日，距离建党纪念日还有%d天", lunar.sunInterval(sy, sm, sd, sy, 7, 1)));
if(sm < 8 || (8 == sm && sd <1))
infos.add(String.format("国历8月1日建军节，距离建军节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 8, 1)));
if(sm < 9 || (9 == sm && sd < 10))
infos.add(String.format("国历9月10日教师节，距离教师节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 9, 10)));
if(sm < 10 || (10 == sm && sd < 1))
infos.add(String.format("国历10月1日国庆节，距离国庆节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 10, 1)));
if(sm < 10 || (10 == sm && sd < 15))
infos.add(String.format("国历10月15日国际盲人节，距离国际盲人节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 10, 15)));
if(sm < 11 || (11 == sm && sd < 11))
infos.add(String.format("国历11月11日光棍节，距离光棍节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 11, 11)));
if(sm < 12 || (12 == sm && sd <24))
infos.add(String.format("国历12月24日平安夜，距离平安夜还有%d天", lunar.sunInterval(sy, sm, sd, sy, 12, 24)));
if(sm < 12 || (12 == sm && sd <25))
infos.add(String.format("国历12月25日圣诞节，距离圣诞节还有%d天", lunar.sunInterval(sy, sm, sd, sy, 12, 25)));
if(sm < 12 || (12 == sm && sd <= 31))
infos.add(String.format("国历1月1日元旦节，距离元旦还有%d天", lunar.sunInterval(sy, sm, sd, sy+1, 1, 1)));

return infos;
//end_getFestivalInfos
}

///////////////////////////////////////////////////////////////
//method
/*firstJqInfo: 获取第一个节气的信息
*id1: 当前节气ID
*m1: 当前节气的起始月份
*d1: 当前节气的起始日子
*id2: 下一个节气的ID
*m2: 下一节气的起始月份
*d2: 下一节气的起始日子
*返回值： 当前节气第几天，距离下一节气还有多少天的字符串*/
String firstJqInfo(int id1, int m1, int d1, int id2, int m2, int d2)
{
Lunar lunar=new Lunar();
String info="";
Calendar calendar=Calendar.getInstance();
String date=DateFormat.format("yyyy-MM-dd", calendar.getTime()).toString();
String [] dates=date.split("-");
int year=Integer.parseInt(dates[0]);
int month=Integer.parseInt(dates[1]);
int day=Integer.parseInt(dates[2]);
int ly=0, lm=0, ld=0;

lunar.seeLunar(year, m1, d1);
ly=lunar.lunarYear();
lm=lunar.lunarMonth();
ld=lunar.lunarDay();

info=String.format("%s： 农历%s, 国历%d月%d日。今天是： ", getJqString(id1), createLunarDateInfo(ly, lm, ld), m1, d1);

if(12 == m1 && 1 == m2 && 12 == month)
info+=String.format("%s第%d天，距离%s还有%d天", getJqString(id1), lunar.sunInterval(year, m1, d1, year, month, day), getJqString(id2), lunar.sunInterval(year, month, day, year+1, m2, d2));
else if(12 == m1 && 1 == m2 && 1 == month)
info+=String.format("%s第%d天，距离%s还有%d天", getJqString(id1), lunar.sunInterval(year-1, m1, d1, year, month, day), getJqString(id2), lunar.sunInterval(year, month, day, year, m2, d2));
else
info+=String.format("%s第%d天， 距离%s还有%d天", getJqString(id1), lunar.sunInterval(year, m1, d1, year, month, day), getJqString(id2), lunar.sunInterval(year, month, day, year, m2, d2));

return info;
//end_firstJqInfo
}

//getJqString: 获取节气字符串
private String getJqString(int id)
{
String info="";

switch(id)
{
case 1:
info="立春";
break;
case 2:
info="雨水";
break;
case 3:
info="惊蛰";
break;
case 4:
info="春分";
break;
case 5:
info="清明";
break;
case 6:
info="谷雨";
break;
case 7:
info="立夏";
break;
case 8:
info="小满";
break;
case 9:
info="芒种";
break;
case 10:
info="夏至";
break;
case 11:
info="小暑";
break;
case 12:
info="大暑";
break;
case 13:
info="立秋";
break;
case 14:
info="处暑";
break;
case 15:
info="白露";
break;
case 16:
info="秋分";
break;
case 17:
info="寒露";
break;
case 18:
info="霜降";
break;
case 19:
info="立冬";
break;
case 20:
info="小雪";
break;
case 21:
info="大雪";
break;
case 22:
info="冬至";
break;
case 23:
info="小寒";
break;
case 24:
info="大寒";
break;
default:
return info;
}

return info;
}

/*lunarFestival: 距离指定日期最近的农历节日还有多少天
*year:
*month:
*day:
*返回值： 距离指定年月日最近的农历节日的天数*/
private String lunarFestival(int year, int month, int day)
{
Lunar lunar=new Lunar();
String today=todayLunarFestival(month, day);
String info="";
int distance=0;

if(1 == month && day < 15)
info+=String.format("距离元宵节还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 1, 15)));
else if(month < 2 || (2 == month && day < 2))
info+=String.format("距离龙抬头还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 2, 2)));
else if(month < 5 || (5 == month && day < 5))
info+=String.format("距离端午节还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 5, 5)));
else if(month < 7 || (7 == month && day < 7))
info+=String.format("距离牛郎会织女还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 7, 7)));
else if(month < 7 || (7 == month && day < 15))
info+=String.format("距离中元节还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 7, 15)));
else if(month < 8 || (8 == month && day < 15))
info+=String.format("距离中秋节还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 8, 15)));
else if(month < 9 || (9 == month && day < 9))
info+=String.format("距离重阳节还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 9, 9)));
else if(month < 12 || (12 == month && day < 8))
info+=String.format("距离腊八节还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 12, 8)));
else if(month < 12 || (12 == month && day < 23))
info+=String.format("距离小年夜还有%d天", (distance=lunar.lunarInterval(year, month, day, year, 12, 23)));
else
info+=String.format("距离除夕还有%d天", (distance=lunar.lunarInterval(year, month, day, year+1, 1, 1)-1));

if(distance <= 10)
today += info;

return today;
//end_lunarFestival
}

/*sunFestival: 阳历节日信息
*year:
*month:
*day:
*返回值： 指定年月日的节日信息和距离最近的节日天数信息字符串*/
private String sunFestival(int year, int month, int day)
{
Lunar lunar=new Lunar();
String today=todaySunFestival(month, day);
String info="";
int distance=0;

if(month < 2 || (2 == month && day < 14))
info += String.format("距离情人节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 2, 14)));
else if( month < 3 || (3 == month && day < 8))
info += String.format("距离妇女节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 3, 8)));
else if(month < 4 || (4 == month && day < 1))
info += String.format("距离愚人节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 4, 1)));
else if(month < 5 || (5 == month && day < 1))
info += String.format("距离劳动节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 5, 1)));
else if(month < 6 || (6 == month && day < 1))
info += String.format("距离儿童节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 6, 1)));
else if(month < 7 || (7 == month && day < 1))
info += String.format("距离党的生日还有%d天", (distance=lunar.sunInterval(year, month, day, year, 7, 1)));
else if(month < 8 || (8 == month && day < 1))
info += String.format("距离建军节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 8, 1)));
else if(month < 9 || (9 == month && day < 10))
info += String.format("距离教师节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 9, 10)));
else if(month < 10 || (10 == month && day < 1))
info += String.format("距离国庆节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 10, 1)));
else if(month < 10 || (10 == month && day < 15))
info += String.format("距离国际盲人节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 10, 15)));
else if(month < 11 || (11 == month && day < 11))
info += String.format("距离光棍节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 11, 11)));
else if(month < 12 || (12 == month && day < 24))
info += String.format("距离平安夜还有%d天", (distance=lunar.sunInterval(year, month, day, year, 12, 24)));
else if(month < 12 || (12 == month && day < 25))
info += String.format("距离圣诞节还有%d天", (distance=lunar.sunInterval(year, month, day, year, 12, 25)));
else
info += String.format("距离元旦还有%d天", (distance=lunar.sunInterval(year, month, day, year+1, 1, 1)));

if(distance <= 10)
today += info;

return today;
//end_sunFestival
}

/*todaySunFestival: 今天国历节日
*month:
*day:
*返回值： 指定月日的节日信息字符串*/
String todaySunFestival(int month, int day)
{
String info="";

if(1 == month && 1 == day)
info="元旦快乐！";
else if(2 == month && 14 == day)
info="情人节快乐！";
else if(3 == month && 8 == day)
info="祝所有女性朋友青春永驻！";
else if(4 == month && 1 == day)
info="今日愚人节，小心被人耍！";
else if(5 == month && 1 == day)
info="祝所有劳动者节日快乐！";
else if(6 == month && 1 == day)
info="祝小朋友们节日快乐！";
else if(7 == month && 1 == day)
info="祝贺党的生日！";
else if(8 == month && day == 1)
info="向军人和军属致敬！";
else if(9 == month && 10 == day)
info="祝所有老师节日快乐！";
else if(10 == month && 1 == day)
info="祝祖国更加繁荣富强！";
else if(10 == month && 15 == day)
info="祝视障小伙伴们的每一天洒满阳光！";
else if(11 == month && 11 == day)
info="祝所有光棍们急速脱光！";
else if(12 == month && 24 == day)
info="祝您永远平安喜乐！";
else if(12 == month && 25 == day)
info="祝您圣诞节快乐！";

return info;
//end_todayFestival
}

/*todayLunarFestival: 今天农历节日信息
*month: 
*day:
*返回值： 指定月日的农历节日信息字符串*/
String todayLunarFestival(int month, int day)
{
String info="";
if(1 == month && 15 == day)
info="今天记得吃汤圆哦！";
else if(2 == month && 2 == day)
info="今日龙抬头，祝您全年大丰收";
else if(5 == month && 5 == day)
info="今日端午节，你吃粽子了吗？";
else if(7 == month && 7 == day)
info="今夜牛郎会织女，你的另一半在身边吗？";
else if(7 == month && 15 == day)
info="今日中元节，放一盏河灯，祈求祖先护佑！";
else if(8 == month && 15 == day)
info="今日中秋节，愿您合家团圆！";
else if(9 == month && 9 == day)
info="今日重阳节，敬老爱老，传统美德！";
else if(12 == month && 8 == day)
info="今日腊八节，记得喝一碗腊八粥！";
else if(12 == month && 23 == day)
info="今日小年夜，有钱没钱,回家过年！";

return info;
//end_todayLunarFestival
}

/*copyMessage: 拷贝消息到剪贴板
*message: 要拷贝到剪贴板的消息字符串*/
protected static void copyMessage(String message)
{
ClipboardManager cm= (ClipboardManager) m_context.getSystemService(Context.CLIPBOARD_SERVICE);
ClipData data= ClipData.newPlainText("Label", message);
cm.setPrimaryClip(data);
showMessage("信息已拷贝到剪贴板");
//end_copyMessage
}

/////////////////////////////////////////////////////////
//data
private static Context m_context;
private static BatteryReceiver m_receiver;

}
