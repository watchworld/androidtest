package com.xingmu.tts;

//import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.xinyang.screenreader.R;

import com.tencent.stat.StatService;

public class AisoundActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
setContentView(R.layout.aisound_activity);

// SeekBar
final SeekBar volumeBar = (SeekBar) findViewById( R.id.seekbar_aisound_volume );
final SeekBar speedBar = (SeekBar) findViewById( R.id.seekbar_aisound_speed );
final SeekBar pitchBar = (SeekBar) findViewById( R.id.seekbar_aisound_pitch );

volumeBar.setProgress( integerToPercent(SynthProxy.getVolume()) );
speedBar.setProgress( integerToPercent(SynthProxy.getSpeed()) );
pitchBar.setProgress( integerToPercent(SynthProxy.getPitch()) );

volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
@Override
public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
if(fromUser) {
SynthProxy.setVolume( percentToInteger(progress) );
speakVolume(progress);
}
}
@Override

public void onStartTrackingTouch(SeekBar seekBar) {
}
@Override

public void onStopTrackingTouch(SeekBar seekBar) {
}

});

speedBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
@Override
public void onProgressChanged(SeekBar bar,int progress,boolean fromUser) {
if(fromUser) {
SynthProxy.setSpeed( percentToInteger(progress) );
speakSpeed(progress);
}
}
@Override

public void onStartTrackingTouch(SeekBar seekBar) {
}
@Override

public void onStopTrackingTouch(SeekBar seekBar) {
}

});

pitchBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
@Override
public void onProgressChanged(SeekBar bar,int progress,boolean fromUser) {
if(fromUser) {
SynthProxy.setPitch( percentToInteger(progress) );
speakPitch(progress);
}
}
@Override

public void onStartTrackingTouch(SeekBar seekBar) {
}
@Override

public void onStopTrackingTouch(SeekBar seekBar) {
}

});

final String[] items = new String[]{"晓燕","小峰","许久","许多","晓萍","唐老鸭","许宝宝","取消"};
final Button btnRole = (Button) findViewById(R.id.btnRole);
btnRole.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
new AlertDialog.Builder( new ContextThemeWrapper( AisoundActivity.this, R.style.AppTheme ) )
.setIcon(R.drawable.ic_stat_info)
.setTitle("发音角色：")
.setItems( items,  
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog,int which) {
switch(which){
case 0:
SynthProxy.change(SynthProxy.AISOUND_ROLE_XIAOYAN);
break;
case 1:
SynthProxy.change(SynthProxy.AISOUND_ROLE_XIAOFENG);
break;
case 2:
SynthProxy.change(SynthProxy.AISOUND_ROLE_XUJIU);
break;
case 3:
SynthProxy.change(SynthProxy.AISOUND_ROLE_XUDUO);
break;
case 4:
SynthProxy.change(SynthProxy.AISOUND_ROLE_XIAOPING);
break;
case 5:
SynthProxy.change(SynthProxy.AISOUND_ROLE_TLY);
break;
case 6:
SynthProxy.change(SynthProxy.AISOUND_ROLE_BABYXU);
break;
default:
return;
}//switch
btnRole.setText("发音角色： " + items[which]);
SynthProxy.speak("你选择了：" + items[which], "czy");
}
})
.show();
}
});
btnRole.setText("发音角色： " + items[getRoleIndex(SynthProxy.getRole())]);

final Button btnStyle = (Button) findViewById(R.id.btnStyle);
btnStyle.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
new AlertDialog.Builder( new ContextThemeWrapper( AisoundActivity.this, R.style.AppTheme ) )
.setIcon(R.drawable.ic_stat_info)
.setTitle("发音风格：")
.setItems( new String[]{"平铺直叙","一字一顿","取消"} ,  
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog,int which) {
switch(which){
case 0://平铺直叙
SynthProxy.setStyle(1);
btnStyle.setText("发音风格： 平铺直叙");
SynthProxy.speak("平铺直叙","czy");
break;
case 1://数字读法
SynthProxy.setStyle(0);
btnStyle.setText("发音风格： 一字一顿");
SynthProxy.speak("一字一顿","czy");
break;
default:
break;
}
}
})
.show();
}
});
switch(SynthProxy.getStyle()){
case 0:
btnStyle.setText("发音风格： 一字一顿");
break;
default:
btnStyle.setText("发音风格： 平铺直叙");
break;
}

final Button btnNumber = (Button) findViewById(R.id.btnNumber);
btnNumber.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
new AlertDialog.Builder( new ContextThemeWrapper( AisoundActivity.this, R.style.AppTheme ) )
.setIcon(R.drawable.ic_stat_info)
.setTitle("数字朗读发誓：")
.setItems( new String[]{"自动判断","数字读法","数值读法","取消"} ,  
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog,int which) {
switch(which){
case 0://自动判断
SynthProxy.setNumber(0);
btnNumber.setText("数字朗读方式： 自动判断");
break;
case 1://数字读法
SynthProxy.setNumber(1);
btnNumber.setText("数字朗读方式： 数字读法");
break;
case 2://数值读法
SynthProxy.setNumber(2);
btnNumber.setText("数字朗读方式： 数值读法");
break;
default:
break;
}
SynthProxy.speak("5.3.2.1 1000","czy");
}
})
.show();
}
});
switch(SynthProxy.getNumber()){
case 0:
btnNumber.setText("数字朗读方式： 自动判断");
break;
case 1:
btnNumber.setText("数字朗读方式： 数字读法");
break;
default:
btnNumber.setText("数字朗读方式： 数值读法");
break;
}

final Button btnWords = (Button) findViewById(R.id.btnWords);
btnWords.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
new AlertDialog.Builder( new ContextThemeWrapper( AisoundActivity.this, R.style.AppTheme ) )
.setIcon(R.drawable.ic_stat_info)
.setTitle("英文单词朗读发誓：")
.setItems( new String[]{"自动判断","按字母发音","按单词发音","取消"} ,  
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog,int which) {
switch(which){
case 0://自动判断
SynthProxy.setWords(0);
btnWords.setText("英文字母朗读方式： 自动判断");
break;
case 1://字母读法
SynthProxy.setWords(1);
btnWords.setText("英文字母朗读方式： 按字母发音");
break;
case 2://按单词发音
SynthProxy.setWords(2);
btnWords.setText("英文字母朗读方式： 按单词发音");
break;
default:
break;
}
SynthProxy.speak("Hello abc","czy");
}
})
.show();
}
});
switch(SynthProxy.getWords()){
case 0:
btnWords.setText("英文单词朗读方式： 自动判断");
break;
case 1:
btnWords.setText("英文单词朗读方式： 按字母发音");
break;
default:
btnWords.setText("英文单词朗读方式： 按单词发音");
break;
}

Button btnTest = (Button) findViewById(R.id.btn_test );
btnTest.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {

/*
int r;
while(true) {
r = SynthProxy.JniSetParam(1280, ii);
ii++;
if(r==0 || ii >10000 )
break;
}
if(r==0){
SynthProxy.JniSpeak("hello abc 哈哈"+(ii-1));
}else{
SynthProxy.JniSpeak("failed");
}
*/
SynthProxy.speak("心阳 v 2017","czy");
}
});

Button btnVolumeIncrease = (Button) findViewById(R.id.volume_increase_button );
btnVolumeIncrease.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {
int volume = volumeBar.getProgress(); // integerToPercent(SynthProxy.getVolume());
if(volume<100) {
volume += 5;
volume -= (volume%5);
SynthProxy.setVolume(percentToInteger(volume));
volumeBar.setProgress( volume );
speakVolume(volume);
}
}
});

Button btnSpeedIncrease = (Button) findViewById(R.id.speed_increase_button );
btnSpeedIncrease.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {
int speed = speedBar.getProgress(); // integerToPercent(SynthProxy.getSpeed());
if(speed<100) {
speed += 5;
speed -= (speed%5);
SynthProxy.setSpeed(percentToInteger(speed));
speedBar.setProgress( speed );
speakSpeed(speed);
}
}
});

Button btnPitchIncrease = (Button) findViewById(R.id.pitch_increase_button );
btnPitchIncrease.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {
int pitch = pitchBar.getProgress(); //integerToPercent(SynthProxy.getPitch());
if(pitch < 100) {
pitch += 5;
pitch -= (pitch%5);
SynthProxy.setPitch(percentToInteger(pitch));
pitchBar.setProgress( pitch );
speakPitch(pitch);
}
}
});

Button btnVolumeDecrease = (Button) findViewById(R.id.volume_decrease_button );
//btnVolumeDecrease.setNextFocusDownId(R.id.volume_increase_button);
//btnVolumeDecrease.setNextFocusRightId(R.id.volume_increase_button);
//btnVolumeDecrease.setNextFocusForwardId(R.id.volume_increase_button);
		btnVolumeDecrease.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {
int volume = volumeBar.getProgress(); // integerToPercent(SynthProxy.getVolume());
if(volume>0) {
volume -= 5;
volume -= (volume%5);
if(volume<0)
volume = 0;
SynthProxy.setVolume(percentToInteger(volume));
volumeBar.setProgress( volume );
speakVolume(volume);
}
}
});

Button btnSpeedDecrease = (Button) findViewById(R.id.speed_decrease_button );
//btnSpeedDecrease.setNextFocusDownId(R.id.speed_increase_button);
//btnSpeedDecrease.setNextFocusRightId(R.id.speed_increase_button);
//btnSpeedDecrease.setNextFocusForwardId(R.id.speed_increase_button);
btnSpeedDecrease.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {
int speed = speedBar.getProgress(); // integerToPercent(SynthProxy.getSpeed());
if(speed>0) {
speed -= 5;
speed -= (speed%5);
if(speed<0)
speed = 0;
SynthProxy.setSpeed(percentToInteger(speed));
speedBar.setProgress( speed );
speakSpeed(speed);
}
}
});

Button btnPitchDecrease = (Button) findViewById(R.id.pitch_decrease_button );
//btnPitchDecrease.setNextFocusDownId(R.id.pitch_increase_button);
//btnPitchDecrease.setNextFocusRightId(R.id.pitch_increase_button);
//btnPitchDecrease.setNextFocusForwardId(R.id.pitch_increase_button);
btnPitchDecrease.setOnClickListener( new OnClickListener() {
@Override
public void onClick(View v) {
int pitch = pitchBar.getProgress(); // integerToPercent(SynthProxy.getPitch());
if(pitch>0) {
pitch -= 5;
pitch -= (pitch%5);
if(pitch<0)
pitch = 0;
SynthProxy.setPitch(percentToInteger(pitch));
pitchBar.setProgress( pitch );
speakPitch(pitch);
}
}
});

} //end onCreate

public int getRoleIndex(int id) {
switch(id) {
case SynthProxy.AISOUND_ROLE_XIAOYAN:
return 0;
case SynthProxy.AISOUND_ROLE_XIAOFENG:
return 1;
case SynthProxy.AISOUND_ROLE_XUJIU:
return 2;
case SynthProxy.AISOUND_ROLE_XUDUO:
return 3;
case SynthProxy.AISOUND_ROLE_XIAOPING:
return 4;
case SynthProxy.AISOUND_ROLE_TLY:
return 5;
case SynthProxy.AISOUND_ROLE_BABYXU:
return 6;
default:
return 0;
}
}

public int percentToInteger(Integer percent) {
Double r = percent.doubleValue() / 100.0 * 65535.0 - 32768.0;
if(r>SynthProxy.AISOUND_MAX){
r = new Double(SynthProxy.AISOUND_MAX);
}
if(r<SynthProxy.AISOUND_MIN){
r = new Double(SynthProxy.AISOUND_MIN);
}
return r.intValue();
}

public int integerToPercent(Integer integer) {
Double r = (integer.doubleValue() + 32768.0) / 65535.0 * 100.0;
return (int)Math.round(r);
}

@Override
protected void onDestroy() {
super.onDestroy();
SynthProxy.save(this);
}

public void speakVolume(int v) {
switch(v) {
case 0:
SynthProxy.speak(v+"最小","czy");
break;
case 100:
SynthProxy.speak(v+"最大","czy");
break;
default:
SynthProxy.speak(v+"变量","czy");
break;
}//switch
}

public void speakSpeed(int v) {
switch(v) {
case 0:
SynthProxy.speak(v+"最慢","czy");
break;
case 100:
SynthProxy.speak(v+"最快","czy");
break;
default:
SynthProxy.speak(v+"变速","czy");
break;
}//switch
}

private void speakPitch(int v) {
switch(v) {
case 0:
SynthProxy.speak(v+"最低","czy");
break;
case 100:
SynthProxy.speak(v+"最高","czy");
break;
default:
SynthProxy.speak(v+"变调","czy");
break;
}//switch
}

    @Override
    public void onResume() {
        super.onResume();
//MobclickAgent.onResume(this);
	StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//MobclickAgent.onPause(this);
	StatService.onPause(this);
    }

}
