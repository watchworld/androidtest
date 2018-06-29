package com.msm;

class Lunar
{
/*getLunarInfo: 获取农历信息
*year: 指定的阳历年份
*month: 指定的阳历月份
*day: 指定的阳历日子
*返回值： 指定阳历年月日的农历日期信息字符串*/
public native String getLunarInfo(int year, int month, int day);

/*getSunInfo: 获取阳历信息
*year: 指定的农历年份
*month: 指定的农历月份，要查询闰月，在当月数值基础上加12。例如要查询2017年润六月15的阳历日期，可传参2017, 18, 15
*day: 指定的农历日子
*返回值： 指定农历年月日对应的阳历日期信息字符串*/
public native String getSunInfo(int year, int month, int day);

public native void create();
public native void destroy();

public native void seeLunar(int year, int month, int day);
public native void seeSun(int year, int month, int day);

public native int lunarYear();
public native int lunarMonth();
public native int lunarDay();

/*sunInterval: 阳历间隔天数
*start_y:
*start_m:
*start_d:
*stop_y:
stop_m:
stop_d: 
*返回值： 起始年月日至截止年月日，含截止日子的总天数*/
public native int sunInterval(int start_y, int start_m, int start_d, int stop_y, int stop_m, int stop_d);

/*lunarInterval: 农历间隔天数，参数及返回值同上*/
public native int lunarInterval(int start_y, int start_m, int start_d, int stop_y, int stop_m, int stop_d);

/*getSunPosition: 获取指定年月日及其后的节气信息
*year: 指定的年份
*month: 指定的月份
*day: 指定的日子
*返回值： 指定年月日起及其之后一年内的节气信息数组*/
public native int [] getSunPosition(int year, int month, int day);
}
