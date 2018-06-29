/*一些条件定义*/
package com.msm;

public class Config
{
public static final int EXIT_BATTERY_BROADCAST=0;
public static final int TIME = 1;
public static final int SUN = 2;
public static final int LUNAR = 3;
public static final int BATTERY = 4;
public static final int NETWORK = 5;

//查询类型常量值
public static final int CHECK_SUN=6;
public static final int CHECK_LUNAR=7;

/*日期查询键名字符串
*弹窗启动查询活动传参时用*/
public static final String CHECK_TYPE="com.msm.ui.CheckUI.CHECK_TYPE";
}