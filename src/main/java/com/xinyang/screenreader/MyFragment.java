package com.xinyang.screenreader;

import com.czy.tools.Compared;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.czyusercenter.data.User;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import com.android.utils.SharedPreferencesUtils;
import com.bdtexample.BDTranslate;
import com.android.talkback.eventprocessor.ProcessorFocusAndSingleTap;

public class MyFragment extends Fragment {
private List<Map<String,Object> > items;
private SimpleAdapter adapter;
private int how = 0;//0 can submit data
private String strData=null;
private int iData=0;

	public MyFragment() {
		// TODO Auto-generated constructor stub
	}

@Override

public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

View v = inflater.inflate(R.layout.my_fragment, container, false);
		// TODO Auto-generated constructor stub

items = new ArrayList<Map<String,Object> >();

ListView listView =  (ListView) v.findViewById( R.id.listView);
adapter = new SimpleAdapter(getActivity(), items, R.layout.my_list_item, new String[]{"name"}, new int[]{R.id.text_view_name});
listView.setAdapter(adapter);

listView.setOnItemClickListener( new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> ada, View v, int position, long id) {
if(position>=0 && position<items.size()) {
if(how != 0) {
Toast.makeText(getActivity(), "正在处理之前的数据请求，请稍后。", Toast.LENGTH_SHORT).show();
return;
}
final User user = new User();
switch(position) {
case 0: // 如果没登录的状态下是登录
if(!user.is()) {
Intent intent = new Intent( getActivity(), LoginActivity.class);
getActivity().startActivityForResult(intent, 1);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
break;
case 1://如果没登录的状态下是注册
if(!user.is()) {
}
break;
case 16: // logout
Compared compared = new Compared();
compared.getHardCode(getActivity());
String formData = "phone=" + user.getPhone() + "&token=" + user.getToken();
formData += "&time=";
SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
formData += myFormat.format(new Date());
formData += "&hardcode=";
Https https = new Https();
https.postText_t("", formData, new ChangeHttpsDelegate(), 7, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
break;
default:
{
Builder dialog = new AlertDialog.Builder(getActivity());
final EditText editLine;
switch(position) {
case 3:
if(!user.getName().isEmpty()){
return;
}
dialog.setTitle("真实姓名");
dialog.setMessage("真实姓名提交后将无法修改。");
editLine = new EditText(getActivity());
dialog.setView(editLine);
   dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String name = editLine.getText().toString();
if(name.length()>20) {
Toast.makeText(getActivity(), "真实姓名过长", Toast.LENGTH_SHORT).show();
 }else if(name.length() <= 1 ) {
Toast.makeText(getActivity(), "真实姓名过短", Toast.LENGTH_SHORT).show();
}else{
String formData = "";
try{
formData = "id=" + user.getId() + "&field=name&data=" + URLEncoder.encode(name,"utf-8").toLowerCase();
}catch(Exception e){
}
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 1;
strData = name;
}
    }
   });
   dialog.setNegativeButton("取消", null);
break;
case 5:
dialog.setTitle("性别")
.setMessage("选择你的性别。")
.setPositiveButton("男", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String formData = "id=" + user.getId() + "&field=sex&data=1";
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 2;
iData = 1;
    }
   })
.setNegativeButton("女", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String formData = "id=" + user.getId() + "&field=sex&data=0";
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 2;
iData = 0;
    }
   });
break;
case 7:
dialog.setTitle("年龄");
dialog.setMessage("请输入你的年龄。");
editLine = new EditText(getActivity());
editLine.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_CLASS_NUMBER);
dialog.setView(editLine);
   dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
int age;
try {
age = Integer.parseInt(editLine.getText().toString());
} catch(Exception e) {
age = 0;
}
if(age<=1||age>120){
Toast.makeText(getActivity(), "请输入正确的年龄", Toast.LENGTH_SHORT).show();				
}else{
String formData = "id=" + user.getId() + "&field=age&data=" + age;
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 3;
iData = age;
}
    }
   });
   dialog.setNegativeButton("取消", null);
break;
case 8:
if(!user.getEmail().isEmpty()){
return;
}
dialog.setTitle("邮箱");
dialog.setMessage("邮箱绑定后将无法修改。");
editLine = new EditText(getActivity());
editLine.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
dialog.setView(editLine);
   dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String email = editLine.getText().toString();
if(email.length()<=6||email.length()>32){
Toast.makeText(getActivity(), "请输入正确的电子邮件地址。", Toast.LENGTH_SHORT).show();				
}else{
String formData = "id=" + user.getId() + "&field=email&data=" + email;
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 4;
strData = email;
}
    }
   });
   dialog.setNegativeButton("取消", null);
break;
case 9:
dialog.setTitle("QQ");
dialog.setMessage("请绑定你的qq号。");
editLine = new EditText(getActivity());
editLine.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_CLASS_NUMBER);
dialog.setView(editLine);
   dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String qq = editLine.getText().toString();
if(qq.length()<=6||qq.length()>12){
Toast.makeText(getActivity(), "请输入正确的QQ号。", Toast.LENGTH_SHORT).show();				
}else{
String formData = "id=" + user.getId() + "&field=qq&data=" + qq;
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 5;
strData = qq;
}
    }
   });
   dialog.setNegativeButton("取消", null);
break;
case 10:
strData = null;
Spinner spinner = new Spinner(getActivity());
ArrayAdapter<String> adapter;  
final List<String> list = new ArrayList<String>();
list.add("请选择你所在城市。");
list.add("北京市");
list.add("广东省");
list.add("山东省");
list.add("江苏省");
list.add("河南省");
list.add("上海市");
list.add("河北省");
list.add("浙江省");
list.add("香港特别行政区");
list.add("陕西省");
list.add("湖南省");
list.add("重庆市");
list.add("福建省");
list.add("天津市");
list.add("云南省");
list.add("四川省");
list.add("广西壮族自治区");
list.add("安徽省");
list.add("海南省");
list.add("江西省");
list.add("湖北省");
list.add("山西省");
list.add("辽宁省");
list.add("台湾省");
list.add("黑龙江");
list.add("内蒙古自治区");
list.add("澳门特别行政区");
list.add("贵州省");
list.add("甘肃省");
list.add("青海省");
list.add("新疆维吾尔自治区");
list.add("西藏区");
list.add("吉林省");
list.add("宁夏回族自治区");
adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
spinner.setAdapter(adapter);  
spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
if(position<=0){
strData = null;
}else{
strData = list.get(position);
}
}
public void onNothingSelected(AdapterView<?> arg0) {
}
});
dialog.setTitle("地区")
.setView(spinner)
.setPositiveButton("确定", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
if(strData!=null){
String formData = "";
try{
formData = "id=" + user.getId() + "&field=city&data=" + URLEncoder.encode(strData,"utf-8").toLowerCase();
}catch(Exception e){
}
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 6;
}else{
Toast.makeText(getActivity(), "请选择所在城市。", Toast.LENGTH_SHORT).show();
}
}
})
.setNegativeButton("取消", null);
break;
case 11:
dialog.setTitle("详细地址");
dialog.setMessage("请输入你的详细地址。");
editLine = new EditText(getActivity());
dialog.setView(editLine);
   dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String address = editLine.getText().toString();
if(address.length()<=6||address.length()>50){
Toast.makeText(getActivity(), "请输入正确的详细地址。", Toast.LENGTH_SHORT).show();				
}else{
String formData = "";
try {
formData = "id=" + user.getId() + "&field=address&data=" + URLEncoder.encode(address,"utf-8").toLowerCase();
}catch(Exception e){
}
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 7;
strData = address;
}
    }
   });
   dialog.setNegativeButton("取消", null);
break;
case 12:
dialog.setTitle("介绍");
dialog.setMessage("请输入你的介绍。");
editLine = new EditText(getActivity());
dialog.setView(editLine);
   dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
String describe = editLine.getText().toString();
if(describe.length()<=6||describe.length()>50){
Toast.makeText(getActivity(), "请输入正确的介绍。", Toast.LENGTH_SHORT).show();				
}else{
String formData = "";
try {
formData = "id=" + user.getId() + "&field=describes&data=" + URLEncoder.encode(describe,"utf-8").toLowerCase();
}catch(Exception e){
}
Https https = new Https();
https.getText_t(formData, new ChangeHttpsDelegate(), 5, "com/xinyang/screenreader/MyFragment$ChangeHttpsDelegate");
how = 8;
strData = describe;
}
    }
   });
   dialog.setNegativeButton("取消", null);
break;
default:
return;
}
   dialog.show();
}
break;
}//switch
}
}
});

return v;
}

@Override
public void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
}

public void updateMy() {
User user = new User();
if(user.is()) {
items.clear();
Map<String,Object> item = new HashMap<String,Object>();
item.put("name", "用户ID: " + user.getId());
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "昵称: " + user.getNickname());
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "手机号: " + user.getPhone());
items.add(item);
item = new HashMap<String,Object>();
String name = user.getName();
if(!name.isEmpty())
item.put("name", "真实姓名: " + name);
else
item.put("name", "真实姓名: 点击设置真实姓名");
items.add(item);
item = new HashMap<String,Object>();
String idCard = user.getIdCard();
if(idCard!=null && !idCard.isEmpty())
item.put("name", "身份证号: " + idCard);
else
item.put("name", "身份证号: 尚未录入。");
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "性别： " + (user.getSex() ? "男" : "女") );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "积分： " + user.getPoint() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "年龄： " + user.getAge() );
items.add(item);
item = new HashMap<String,Object>();
String email = user.getEmail();
if(!email.isEmpty())
item.put("name", "电子邮件： " + email );
else
item.put("name", "电子邮件： 点击绑定邮箱" );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "QQ： " + user.getQq() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "所在地： " + user.getCity() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "详细地址： " + user.getAddress() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "个人介绍： " + user.getDescribe() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "注册日期： " + user.getCreatedAt() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "上次登录时间： " + user.getUpdatedAt() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "上次登录IP： " + user.getIp() );
items.add(item);
item = new HashMap<String,Object>();
item.put("name", "点击退出当前账号");
items.add(item);
}else{
items.clear();
Map<String,Object> item = new HashMap<String,Object>();
item.put("name", "还未登录，点击登录。");
items.add(item);
}
adapter.notifyDataSetChanged();
}

@Override
public void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
User user = new User();
if(user.is()) {
updateMy();
} else {
items.clear();
Map<String,Object> item = new HashMap<String,Object>();
item.put("name", "还未登录，点击登录。");
items.add(item);
adapter.notifyDataSetChanged();
}
}

public void openEdit() {
}

public class ChangeHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
if(how == 7 ) {
//  logout
if(item.containsKey("success")) {
User user = new User();
 user.logoff();
try {
SharedPreferences prefs = SharedPreferencesUtils.getSharedPreferences(getActivity());
if( prefs!=null ) {
Editor editor = prefs.edit();
if(ProcessorFocusAndSingleTap.touchSort) {
ProcessorFocusAndSingleTap.touchSort = false;
editor.putBoolean(getString(R.string.pref_touch_sort_key), false);
}
if( BDTranslate.opening ) {
BDTranslate.opening = false;
editor.putBoolean(getString(R.string.pref_translate_key), false);
}
editor.commit();
}
} catch(Exception e) {
e.printStackTrace();
}
 updateMy();
Toast.makeText(getActivity(), "成功注销你的账号。", Toast.LENGTH_SHORT).show();				
} else {
Toast.makeText(getActivity(), (String) item.get("error"), Toast.LENGTH_SHORT).show();				
}
} else {
if(resultCode==0) {
Toast.makeText(getActivity(), (String) item.get("message"), Toast.LENGTH_SHORT).show();				
}else{
User user = new User();
String text = "";
switch(MyFragment.this.how) {
case 1:
text = "真实姓名";
user.setName(strData);
break;
case 2:
text = "性别";
user.setSex(iData==1);
break;
case 3:
text = "年龄";
user.setAge(iData);
break;
case 4:
text = "绑定邮箱";
user.setEmail(strData);
break;
case 5:
text = "QQ号";
user.setQq(strData);
break;
case 6:
text = "所在地区";
user.setCity(strData);
break;
case 7:
text = "详细地址";
user.setAddress(strData);
break;
case 8:
text = "介绍";
user.setDescribe(strData);
break;
default:
break;
}
Toast.makeText(getActivity(), "恭喜，修改(" + text + ")成功。", Toast.LENGTH_SHORT).show();				
updateMy();
}
MyFragment.this.how = 0; //又能提交数据了
}
}
@Override
public void onFailed_m(int errorCode) {
Toast.makeText(getActivity(), "链接网络失败，请检查上网设置。", Toast.LENGTH_SHORT).show();				
MyFragment.this.how = 0; //又能提交数据了
}	
}

}//end class
