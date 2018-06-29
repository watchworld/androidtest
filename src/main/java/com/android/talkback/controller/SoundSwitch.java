package com.android.talkback.controller;

import java.io.File;
import android.os.Environment;
import com.xinyang.screenreader.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import com.google.android.marvin.talkback8.TalkBackService;
import android.widget.Toast;

public class SoundSwitch {
public static int sort = 0; // 0 apple, 1 modern, 2 default, 3 myself

public static boolean update(SharedPreferences prefs, String s) {
boolean reload = false;
String str = prefs.getString(s, "originally");
if(str.equals("modern")) {
if(sort==3) {
reload = true;
}
sort = 1;
}else if(str.equals("originally")) {
if(sort==3) {
reload = true;
}
sort = 2;
}else if(str.equals("myself")) {
if(sort!=3) {
reload = true;
sort = 3;
}
} else {
if(sort==3) {
reload = true;
}
sort = 0;
}
return reload;
}

public static int getSoundId(int soundId) {
switch(sort) {
case 0:
return soundId;
case 1:
return getSoundId_modern(soundId);
case 2:
return getSoundId_default(soundId);
default:
return soundId;
}
}

public static int getSoundId_default(int soundId) {
switch(soundId) {
case R.raw.back:
return R.raw.default_back;
case R.raw.bold:
return R.raw.default_bold;
case R.raw.chime_down:
return R.raw.default_chime_down;
case R.raw.chime_up:
return R.raw.default_chime_up;
case R.raw.complete:
return R.raw.default_complete;
case R.raw.focus:
return R.raw.default_focus;
case R.raw.focus_actionable:
return R.raw.default_focus_actionable;
case R.raw.hyperlink:
return R.raw.default_hyperlink;
case R.raw.italic:
return R.raw.default_italic;
case R.raw.long_clicked:
return R.raw.default_long_clicked;
//case R.raw.paused_feedback:
//return R.raw.default_paused_feedback;
case R.raw.radial_menu_1:
return R.raw.default_radial_menu_1;
case R.raw.radial_menu_2:
return R.raw.default_radial_menu_2;
case R.raw.radial_menu_3:
return R.raw.default_radial_menu_3;
case R.raw.radial_menu_4:
return R.raw.default_radial_menu_4;
case R.raw.radial_menu_5:
return R.raw.default_radial_menu_5;
case R.raw.radial_menu_6:
return R.raw.default_radial_menu_6;
case R.raw.radial_menu_7:
return R.raw.default_radial_menu_7;
case R.raw.radial_menu_8:
return R.raw.default_radial_menu_8;
//case R.raw.ready:
//return R.raw.default_ready;
//case R.raw.screen_close:
//return R.raw.default_screen_close;
case R.raw.scroll_more:
return R.raw.default_scroll_more;
case R.raw.scroll_tone:
return R.raw.default_scroll_tone;
case R.raw.tick:
return R.raw.default_tick;
case R.raw.view_entered:
return R.raw.default_view_entered;
case R.raw.window_state:
return R.raw.default_window_state;
default:
return 0;
}
}

public static int getSoundId_modern(int soundId) {
switch(soundId) {
case R.raw.back:
return R.raw.modern_back;
case R.raw.bold:
return R.raw.modern_bold;
case R.raw.chime_down:
return R.raw.modern_chime_down;
case R.raw.chime_up:
return R.raw.modern_chime_up;
case R.raw.complete:
return R.raw.modern_complete;
case R.raw.focus:
return R.raw.modern_focus;
case R.raw.focus_actionable:
return R.raw.modern_focus_actionable;
case R.raw.hyperlink:
return R.raw.modern_hyperlink;
case R.raw.italic:
return R.raw.modern_italic;
case R.raw.long_clicked:
return R.raw.modern_long_clicked;
//case R.raw.paused_feedback:
//return R.raw.modern_paused_feedback;
case R.raw.radial_menu_1:
return R.raw.modern_radial_menu_1;
case R.raw.radial_menu_2:
return R.raw.modern_radial_menu_2;
case R.raw.radial_menu_3:
return R.raw.modern_radial_menu_3;
case R.raw.radial_menu_4:
return R.raw.modern_radial_menu_4;
case R.raw.radial_menu_5:
return R.raw.modern_radial_menu_5;
case R.raw.radial_menu_6:
return R.raw.modern_radial_menu_6;
case R.raw.radial_menu_7:
return R.raw.modern_radial_menu_7;
case R.raw.radial_menu_8:
return R.raw.modern_radial_menu_8;
//case R.raw.ready:
//return R.raw.modern_ready;
//case R.raw.screen_close:
//return R.raw.modern_screen_close;
case R.raw.scroll_more:
return R.raw.modern_scroll_more;
case R.raw.scroll_tone:
return R.raw.modern_scroll_tone;
case R.raw.tick:
return R.raw.modern_tick;
case R.raw.view_entered:
return R.raw.modern_view_entered;
case R.raw.window_state:
return R.raw.modern_window_state;
default:
return 0;
}
}

public static String getSoundPath_myself(int soundId) {
StringBuilder builder = new StringBuilder();
builder.append( Environment.getExternalStorageDirectory().getAbsolutePath() + "/心阳/读屏音效/");
File soundFolder = new File(builder.toString());
if(!soundFolder.exists()) {
soundFolder.mkdirs();
}
switch(soundId) {
case R.raw.back:
builder.append("返回");
break;
case R.raw.bold:
builder.append("明显");
break;
case R.raw.chime_down:
builder.append("到尾");
break;
case R.raw.chime_up:
builder.append("到头");
break;
case R.raw.complete:
builder.append("分隔提示");
break;
case R.raw.focus:
builder.append("不能点击");
break;
case R.raw.focus_actionable:
builder.append("焦点切换");
break;
case R.raw.hyperlink:
builder.append("网页链接");
break;
case R.raw.italic:
builder.append("斜体字");
break;
case R.raw.long_clicked:
builder.append("长按");
break;
case R.raw.radial_menu_1:
builder.append("菜单提示1");
break;
case R.raw.radial_menu_2:
builder.append("菜单提示2");
break;
case R.raw.radial_menu_3:
builder.append("菜单提示3");
break;
case R.raw.radial_menu_4:
builder.append("菜单提示4");
break;
case R.raw.radial_menu_5:
builder.append("菜单提示5");
break;
case R.raw.radial_menu_6:
builder.append("菜单提示6");
break;
case R.raw.radial_menu_7:
builder.append("菜单提示7");
break;
case R.raw.radial_menu_8:
builder.append("菜单提示8");
break;
case R.raw.scroll_more:
builder.append("滚屏刷新");
break;
case R.raw.scroll_tone:
builder.append("滚屏");
break;
case R.raw.tick:
builder.append("点击");
break;
case R.raw.view_entered:
builder.append("空白");
break;
case R.raw.window_state:
builder.append("弹出窗口");
break;
default:
break;
}
builder.append(".ogg");
File soundFile = new File(builder.toString());
if(!soundFile.exists()) {
return null;
}
return builder.toString();
}

}
