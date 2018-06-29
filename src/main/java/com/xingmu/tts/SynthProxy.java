package com.xingmu.tts;

import com.android.utils.SharedPreferencesUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.util.Log;

public class SynthProxy {
private static  int mNeedCompleted = 0;// 0 not need completed event
private static boolean bCanStop = true; // can you stop speaking
private static boolean bCanSpeak = true;
private OnSpeakCompletedListener onSpeakCompletedListener;

public static final int AISOUND_MIN = -32768;
public static final int AISOUND_NORMAL = 0;
public static final int AISOUND_MAX = 32767;

public static final int AISOUND_AREA = 256;//语言，0自动，1中文，2英文
public static final int AISOUND_ROLE = 1280;//角色
public static final int AISOUND_STYLE = 1281;//风格
public static final int AISOUND_SPEED = 1282;//语速
public static final int AISOUND_PITCH = 1283;//音调
public static final int AISOUND_VOLUME = 1284;//音量
public static final int AISOUND_NUMBER = 770; //数值读法还是数字读法
public static final int AISOUND_WORDS = 774; //单词还是字母。
public static final int AISOUND_VEMODE = 1536; //
public static final int AISOUND_ROLE_XIAOYAN = 3;//小燕
public static final int AISOUND_ROLE_XIAOFENG = 4;//小风
public static final int AISOUND_ROLE_XUJIU = 51;//徐炯
public static final int AISOUND_ROLE_XUDUO = 52; //许多
public static final int AISOUND_ROLE_XIAOPING = 53;//小平
public static final int AISOUND_ROLE_TLY = 54;//唐老鸭
public static final int AISOUND_ROLE_BABYXU = 55;//徐宝宝

 static {
 System.loadLibrary("XMengine");
 }

public void setOnSpeakCompletedListener(OnSpeakCompletedListener onSpeakCompletedListener) {
this.onSpeakCompletedListener = onSpeakCompletedListener;
}

public SynthProxy(Context context, AssetManager paramAssetManager, String paramString) {
 JniCreate(paramAssetManager, paramString);
load(context);
speakThread = new LooperThread();
speakThread.start();
JniSpeak(".", new SynthesisCallback(){
	@Override
	public int getMaxBufferSize() {
		// TODO Auto-generated method stub
		return 0;
}
	@Override
	public int start(int sampleRateInHz, int audioFormat, int channelCount) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int audioAvailable(byte[] buffer, int offset, int length) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int done() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void error() {
		// TODO Auto-generated method stub
}
	@Override
	public void error(int errorCode) {
		// TODO Auto-generated method stub
}
	@Override
	public boolean hasStarted() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean hasFinished() {
		// TODO Auto-generated method stub
		return false;
	}
});

 }

public static void setCanSpeak(boolean canSpeak) {
SynthProxy.bCanSpeak = canSpeak;
}

 private static native int JniCreate(AssetManager paramAssetManager, String paramString);
 private static native int JniDestory();
 private static native int JniSetParam(int paramInt1, int paramInt2);
 private static native int JniGetParam(int paramInt1);
 private static native int JniSpeak(String paramString, SynthesisCallback paramSynthesisCallback);
 private static native int JniStop();
 
private static int paramIntFromString(String paramString) {
if(paramString.equals("role")) {
return 1280;
}
if (paramString.equals("speed")) {
return 1282;
}
if (paramString.equals("pitch")) {
return 1283;
}
if (paramString.equals("read_digit")) {
return 770;
}
if (paramString.equals("read_word")) {
return 774;
}
if (paramString.equals("speak_style")) {
return 1281;
}
if (paramString.equals("vemode")) {
return 1536;
}
return -1;
}
 
protected void finalize() {
shutdown();
}

public String[] getLanguage() {
return new String[] { "zh", "CHN", "" };
 }

public int isLanguageAvailable(String paramString1, String paramString2, String paramString3) {
 if ((paramString1.equalsIgnoreCase("zh")) || (paramString1.equalsIgnoreCase("en"))) {
 return 0;
 }
 return -2;
 }
 
 public int setLanguage(String paramString1, String paramString2, String paramString3) {
 return 0;
 }
 
public static final int setVolume(int volume) {
return JniSetParam(AISOUND_VOLUME,volume);
}
public static final int getVolume() {
return JniGetParam(AISOUND_VOLUME);
}
public static final int setPitch(int paramInt) {
return JniSetParam(AISOUND_PITCH, paramInt); //convertValue(paramInt, -32768, 0, 32767));
 }
public static final int getPitch() {
return JniGetParam(AISOUND_PITCH);
}
public static final int change(int role) {
return JniSetParam(AISOUND_ROLE,role);
}
 public static final int setRole(int paramInt) {
 return JniSetParam(AISOUND_ROLE, paramInt);
 }
 public static int getRole() {
return JniGetParam(AISOUND_ROLE);
 }
 public static int setStyle(int style) {
return JniSetParam(AISOUND_STYLE,style);
}
public static int getStyle() {
return JniGetParam(AISOUND_STYLE);
}
 public static int setNumber(int number) {
return JniSetParam(AISOUND_NUMBER,number);
}
public static int getNumber() {
return JniGetParam(AISOUND_NUMBER);
}
 public static int setWords(int words) {
return JniSetParam(AISOUND_WORDS,words);
}
public static int getWords() {
return JniGetParam(AISOUND_WORDS);
}
 public static int setVemode(int vemode) {
return JniSetParam(AISOUND_VEMODE,vemode);
}
public static int getVemode() {
return JniGetParam(AISOUND_VEMODE);
}
public static final int setSpeed(int paramInt) {
return JniSetParam(AISOUND_SPEED, paramInt); //convertValue(paramInt, -32768, 0, 32767));
 }
public static final int getSpeed() {
return JniGetParam(AISOUND_SPEED);
}
public static void showObject(Activity context) {
Intent intent = new Intent(context, AisoundActivity.class);
context.startActivityForResult(intent, 999);
}

 public void shutdown() {
if(speakThread!=null) {
speakThread.mHandler.getLooper().quit();
speakThread = null;
}
JniDestory();
}

public static void load(Context context) {
try {
SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(context);
setRole( sharedPreferences.getInt("aisound_role",3) );
setVolume( sharedPreferences.getInt("aisound_volume", 32767) );
setSpeed( sharedPreferences.getInt("aisound_speed",0) );
setPitch( sharedPreferences.getInt("aisound_pitch",0) );
setStyle( sharedPreferences.getInt("aisound_style",1) );
setNumber( sharedPreferences.getInt("aisound_number",0) );
setWords( sharedPreferences.getInt("aisound_words",0) );
} catch(Exception e) {
e.printStackTrace();
}
}

public static void save(Context context) {
try {
SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences( context );
Editor editor = sharedPreferences.edit();//获取编辑器
editor.putInt("aisound_role",getRole());
editor.putInt("aisound_volume",getVolume());
editor.putInt("aisound_speed",getSpeed());
editor.putInt("aisound_pitch",getPitch());
editor.putInt("aisound_style",getStyle());
editor.putInt("aisound_number",getNumber());
editor.putInt("aisound_words",getWords());
editor.commit();//提交修改
} catch(Exception e) {
e.printStackTrace();
}
}

 public static void stop() {
if(bCanStop) {
isCallbackCompleted(0); // not need to callback completed function
if(speakThread!=null) {
speakThread.mHandler.removeMessages(1);
}
JniStop();
if(mAudio!=null && mAudio.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
mAudio.stop();
}
} // else {
//bCanStop = true;
//}
}
 
public int startSpeaking(String text) {
return JniSpeak(text + " .", new SynthesisCallback() {
	@Override
	public int getMaxBufferSize() {
		// TODO Auto-generated method stub
		return MIN_AUDIO_BUFFER_SIZE;
	}

	@Override
	public int start(int sampleRateInHz, int audioFormat, int channelCount) {
		// TODO Auto-generated method stub
try {
if(mAudio==null) {
mAudio = new AudioTrack( AudioManager.STREAM_MUSIC, sampleRateInHz, channelCount, audioFormat, MIN_AUDIO_BUFFER_SIZE, AudioTrack.MODE_STREAM);
}
mAudio.play();
}catch(Exception e) {
}
		return 0;
	}

	@Override
	public int audioAvailable(byte[] buffer, int offset, int length) {
		// TODO Auto-generated method stub
try {
mAudio.write(buffer, 0, length);
}catch(Exception e){
}
		return 0;
	}

	@Override
	public int done() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void error() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasFinished() {
		// TODO Auto-generated method stub
		return false;
	}
});

}

private static AudioTrack mAudio;
private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
private static LooperThread speakThread;

/**
 * 异步朗读累
*/
private class LooperThread extends Thread {
public Handler mHandler;
public void run() {
Looper.prepare();
mHandler = new Handler() {
public void handleMessage(Message msg) {
// process incoming messages here    
switch(msg.what) {
case 1:
isCallbackCompleted(1); // increase the completed number
String utteranceId = msg.getData().getString("utteranceId");
if(utteranceId.equals("czy")) {
bCanStop = false;
startSpeaking(msg.getData().getString("text"));
bCanStop = true;
} else {
startSpeaking(msg.getData().getString("text"));
}
if(isCallbackCompleted(2)){
if( onSpeakCompletedListener!=null) {
//android.util.Log.i("czytts", "speaking "+utteranceId);
onSpeakCompletedListener.completedSpeaking(utteranceId);
}
}
break;
}//switch
            }    
        };    
Looper.loop();
}
} // end class

public int speak(SynthesisRequest paramSynthesisRequest, SynthesisCallback paramSynthesisCallback) {
    if ((paramSynthesisRequest == null) || (paramSynthesisRequest.getText() == null) || (paramSynthesisRequest.getText().equals(""))) {
      return 0;
    }
return JniSpeak(paramSynthesisRequest.getText() + " .", paramSynthesisCallback);
}

/**
 * async speak
*/
public static void speak(String text,  String utteranceId) {
stop();
speak_append(text,utteranceId);
}

public static void speak_append(String text, String utteranceId) {
text = text.replaceAll("\\[[A-Za-z]\\d+\\]","");
if(SynthProxy.bCanSpeak) {
if(speakThread!=null) {
Message msg = new Message();
msg.what = 1;
Bundle bundle = new Bundle();
bundle.putString("text",text);
bundle.putString("utteranceId", utteranceId);
//android.util.Log.i("czytts", "begin: "+utteranceId);
msg.setData(bundle);
speakThread.mHandler.sendMessage(msg);
}
}
}

/**
 * need to callback method
*/
public static synchronized boolean isCallbackCompleted(int which) {
switch(which) {
case 0:
mNeedCompleted = 0;
break;
case 1:
mNeedCompleted++;
break;
case 2:
if(mNeedCompleted==0){
break;
}
mNeedCompleted--;
if( mNeedCompleted==0) {
return true;
}
break;
}
return false;
}

public interface OnSpeakCompletedListener {
public void completedSpeaking(String utteranceId);
}

}
