package com.msm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.EditorInfo;
import android.text.InputFilter;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import com.xinyang.screenreader.R;
import com.msm.MsmAssist;
import com.msm.Config;

public class CheckUI extends Activity
{
@Override
protected void onCreate(Bundle savedInstanceState)
{
super.onCreate(savedInstanceState);
setContentView(R.layout.check_ui);

//实例化辅助模块
m_assist=new MsmAssist(this);

//加载控件
m_etDate=(EditText) findViewById(R.id.IDET_LUNAR_DATE);
m_btnCheck=(Button) findViewById(R.id.IDB_CHECK);
m_tvInput=(TextView) findViewById(R.id.IDTV_INPUT);
m_tvEffect=(TextView) findViewById(R.id.IDTV_EFFECT);
m_radioGroup=(RadioGroup) findViewById(R.id.IDRG_CLASSIFY);
m_rbSun=(RadioButton) findViewById(R.id.IDRB_CHECKSUN);
m_rbLunar=(RadioButton) findViewById(R.id.IDRB_CHECKLUNAR);
m_btnContinue=(Button) findViewById(R.id.IDB_CONTINUE);
m_btnReturn=(Button) findViewById(R.id.IDB_RETURN);
m_lv=(ListView) findViewById(R.id.IDLV_SUNPOSITION);
m_rbSunPosition=(RadioButton) findViewById(R.id.IDRB_SUNPOSITION);
m_rbFestival=(RadioButton) findViewById(R.id.IDRB_FESTIVAL);

//设置编辑框软键盘右下角按钮样式
m_etDate.setImeOptions(EditorInfo.IME_ACTION_DONE);

//为按钮和单选按钮设置点击监听器
m_btnCheck.setOnClickListener(m_clickListener);
m_btnContinue.setOnClickListener(m_clickListener);
m_btnReturn.setOnClickListener(m_clickListener);
m_rbSun.setOnClickListener(m_clickListener);
m_rbLunar.setOnClickListener(m_clickListener);
m_rbSunPosition.setOnClickListener(m_clickListener);
m_rbFestival.setOnClickListener(m_clickListener);

//为单选按钮组设置选中变化监听器
m_radioGroup.setOnCheckedChangeListener(m_radioListener);

//为编辑框设置软键盘右下角按钮点击监听器
m_etDate.setOnEditorActionListener(m_editorListener);

//隐藏控件
m_etDate.setVisibility(View.INVISIBLE);
m_btnCheck.setVisibility(View.INVISIBLE);
m_tvInput.setVisibility(View.INVISIBLE);
m_tvEffect.setVisibility(View.INVISIBLE);
m_btnContinue.setVisibility(View.INVISIBLE);
m_lv.setVisibility(View.INVISIBLE);

int type=getIntent().getIntExtra(Config.CHECK_TYPE, -1);
if(Config.CHECK_SUN == type)
{
m_rbSun.setChecked(true);
radioButtonClick(R.id.IDRB_CHECKSUN);
}
else if(Config.CHECK_LUNAR == type)
{
m_rbLunar.setChecked(true);
radioButtonClick(R.id.IDRB_CHECKLUNAR);
}
//end_onCreate
}

//////////////////////////////////////////////////////////////////
//listener: 监听器

//点击监听器
View.OnClickListener m_clickListener=new View.OnClickListener()
{
@Override
public void onClick(View view)
{
if(R.id.IDB_CHECK == view.getId())
checkController();
else if(R.id.IDB_CONTINUE == view.getId())
{
m_assist.hideKeyboard((Activity) CheckUI.this);

m_etDate.setVisibility(View.INVISIBLE);
m_btnCheck.setVisibility(View.INVISIBLE);
m_btnContinue.setVisibility(View.INVISIBLE);
m_tvInput.setVisibility(View.INVISIBLE);
m_tvEffect.setVisibility(View.INVISIBLE);

m_radioGroup.setVisibility(View.VISIBLE);
m_rbSun.setVisibility(View.VISIBLE);
m_rbLunar.setVisibility(View.VISIBLE);
m_rbSunPosition.setVisibility(View.VISIBLE);
}
else if(R.id.IDB_RETURN == view.getId())
CheckUI.this.finish();
else if(R.id.IDRB_CHECKSUN == view.getId() || R.id.IDRB_CHECKLUNAR == view.getId())
radioButtonClick(view.getId());
else if(R.id.IDRB_SUNPOSITION == view.getId())
{
m_radioGroup.setVisibility(View.INVISIBLE);
m_rbSun.setVisibility(View.INVISIBLE);
m_rbLunar.setVisibility(View.INVISIBLE);
m_etDate.setVisibility(View.INVISIBLE);
m_btnCheck.setVisibility(View.INVISIBLE);
m_btnContinue.setVisibility(View.INVISIBLE);
m_tvInput.setVisibility(View.INVISIBLE);
m_tvEffect.setVisibility(View.INVISIBLE);

//为列表试图绑定适配器
m_lv.setAdapter(new ArrayAdapter<String>(CheckUI.this, android.R.layout.simple_list_item_1, m_assist.getSunPositionInfos()));
m_lv.setVisibility(View.VISIBLE);
}
else if(R.id.IDRB_FESTIVAL == view.getId())
{
m_radioGroup.setVisibility(View.INVISIBLE);
m_rbSun.setVisibility(View.INVISIBLE);
m_rbLunar.setVisibility(View.INVISIBLE);
m_etDate.setVisibility(View.INVISIBLE);
m_btnCheck.setVisibility(View.INVISIBLE);
m_btnContinue.setVisibility(View.INVISIBLE);
m_tvInput.setVisibility(View.INVISIBLE);
m_tvEffect.setVisibility(View.INVISIBLE);

//为列表试图绑定适配器
m_lv.setAdapter(new ArrayAdapter<String>(CheckUI.this, android.R.layout.simple_list_item_1, m_assist.getFestivalInfos()));
m_lv.setVisibility(View.VISIBLE);
}

//end_onClick
}
};

//单选按钮组选中变化监听器
RadioGroup.OnCheckedChangeListener m_radioListener=new RadioGroup.OnCheckedChangeListener()
{
@Override
public void onCheckedChanged(RadioGroup group, int id)
{
//radioButtonClick(id);
//end_onCheckedChanged
}
};

//软键盘右下角按钮点击事件监听器
EditText.OnEditorActionListener m_editorListener=new EditText.OnEditorActionListener()
{
@Override
public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
{
checkController();
return true;
//end_onEditorAction
}
};

///////////////////////////////////////////////////////////////////////
//method
//checkController: 查询控制器方法
private void checkController()
{
String info="";

info=m_etDate.getText().toString();
if(info.length() < 4 || info.length() > 9)
{
m_assist.showMessage("输入的日期不正确");
return;
}

//收起键盘
m_assist.hideKeyboard((Activity) CheckUI.this);

//隐藏编辑框和查询按钮
m_etDate.setVisibility(View.INVISIBLE);
m_btnCheck.setVisibility(View.INVISIBLE);

//隐藏单选按钮
m_radioGroup.setVisibility(View.INVISIBLE);
m_rbSun.setVisibility(View.INVISIBLE);
m_rbLunar.setVisibility(View.INVISIBLE);

//显示文本试图和继续查按钮
m_btnContinue.setVisibility(View.VISIBLE);
m_tvInput.setVisibility(View.VISIBLE);
m_tvEffect.setVisibility(View.VISIBLE);

//输入师徒获取焦点
m_tvInput.setFocusable(true);
m_tvInput.setFocusableInTouchMode(true);
m_tvInput.requestFocus();

//根据查询类型调用具体方法
if(Config.CHECK_SUN == m_checkType)
checkSun(info);
else
checkLunar(info);

//end_checkController
}

/*checkSun: 查阳历
*date: 从编辑框得到的用户输入日期信息字符串*/
private void checkSun(String date)
{
String s_year="", s_month="", s_day="";
int year=0;
int month=0;
int day=0;
String sun="阳历： ";
String lunar="农历： ";

/*首先检查输入字段长度是否合法
*只有以下四种长度是合法的*/
switch(date.length())
{
case 4:
case 5:
case 8:
case 9:
break;
default:
m_assist.showMessage("输入的日期长度非法");
return;
}

/*如果日期长度为4或5，表示用户省略了年份，及默认查询当年日期
*因而在date开头追加年份信息，否则下面的月日获取也要另外做处理*/
if(4 == date.length() || 5 == date.length())
date=thisYear() + date;

s_year=date.substring(0, 4);

//如果日期长度为8表示不要求查询闰月
if(8 == date.length())
{
s_month=date.substring(4, 6);
s_day=date.substring(6);
}
else if(9 == date.length())//否则要求查询闰月
{
s_month=date.substring(5, 7);
s_day=date.substring(7);
}

year=Integer.parseInt(s_year);
month=Integer.parseInt(s_month);
day=Integer.parseInt(s_day);

if(9 == date.length())
month+=12;

lunar += m_assist.createLunarDateInfo(year, month, day);
sun += m_assist.checkSun(year, month, day);

showCheck(lunar, sun);
//end_checkSun
}

/*checkLunar: 查农历
*date: 从编辑框得到用户输入的日期信息字符串*/
private void checkLunar(String date)
{
if(4 == date.length())
date=thisYear() + date;
else if(date.length() >= 5 && date.length() <= 7)
{
m_assist.showMessage("日期长度非法");
return;
}

String s_year=date.substring(0, 4), s_month=date.substring(4, 6), s_day=date.substring(6);
int year=Integer.parseInt(s_year);
int month=Integer.parseInt(s_month);
int day=Integer.parseInt(s_day);
String sun="阳历： ";
String lunar="农历： ";

sun += String.format("%d年%d月%d日", year, month, day);
lunar += m_assist.checkLunar(year, month, day);

showCheck(sun, lunar);
//end_checkLunar
}

/*showCheck: 显示查询
*input: 用户输入的查询日期字符串
*effect: 查询结果字符串*/
private void showCheck(String input, String effect)
{
m_tvInput.setText(input);
m_tvEffect.setText(effect);
//end_showCheck
}

/*radioButtonClick: 单选按钮被点击后的处理
*id: 被被点击的单选按钮ID*/
private void radioButtonClick(final int id)
{
/*显示编辑框、查询按钮和重选类型按钮*/
m_etDate.setVisibility(View.VISIBLE);
m_btnCheck.setVisibility(View.VISIBLE);
m_btnContinue.setVisibility(View.VISIBLE);

//隐藏单选按钮和文本试图
m_radioGroup.setVisibility(View.INVISIBLE);
m_rbSun.setVisibility(View.INVISIBLE);
m_rbLunar.setVisibility(View.INVISIBLE);
m_rbSunPosition.setVisibility(View.INVISIBLE);
m_tvInput.setVisibility(View.INVISIBLE);
m_tvEffect.setVisibility(View.INVISIBLE);

m_etDate.setText("");
m_etDate.setFocusable(true);
m_etDate.setFocusableInTouchMode(true);
m_etDate.requestFocus();

if(R.id.IDRB_CHECKSUN == id)
{
m_etDate.setContentDescription(getResources().getString(R.string.SR_DESCRIPTION_LUNAR_DATE));
m_checkType=Config.CHECK_SUN;
m_etDate.setFilters(new InputFilter [] {new InputFilter.LengthFilter(9)});
}
else if(R.id.IDRB_CHECKLUNAR == id)
{
m_etDate.setContentDescription(getResources().getString(R.string.SR_DESCRIPTION_SUN_DATE));
m_checkType=Config.CHECK_LUNAR;
m_etDate.setFilters(new InputFilter [] {new InputFilter.LengthFilter(8)});
}

m_assist.showKeyboard((Activity) CheckUI.this);
//end_radioButtonClick
}

//thisYearInfo: 今年的年份信息字符串
private String thisYear()
{
Calendar calendar=Calendar.getInstance();
String date=DateFormat.format("yyyy-MM-dd", calendar.getTime()).toString();
String [] dates=date.split("-");

return dates[0];
//end_thisYear
}

/////////////////////////////////////////////////////////////////////////////
//data
MsmAssist m_assist;//辅助模块对象
private EditText m_etDate;//日期输入编辑框
private Button m_btnCheck;//查询按钮
private Button m_btnContinue;//继续查按钮
private Button m_btnReturn;//返回按钮
private TextView m_tvInput;//显示用户输入的年月日的文本试图
private TextView m_tvEffect;//显示查询结果的文本试图
private RadioGroup m_radioGroup;//单选按钮组
private RadioButton m_rbSun;//查阳历单选按钮
private RadioButton m_rbLunar;//查农历单选按钮
private RadioButton m_rbSunPosition;//看节气单选按钮
private RadioButton m_rbFestival;//查看节日单选按钮
private ListView m_lv;//列表试图
private int m_checkType;//查询类型

}
