package com.xinyang.screenreader;

import android.content.ComponentName;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.AlertDialog;
import android.content.DialogInterface;
import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.TextView;
import android.widget.Button;
import java.util.Map;
import android.view.View.OnClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.czy.tools.Compared;
import android.provider.Settings;
import com.android.talkback.TalkBackPreferencesActivity;
import com.czy.ocr.AccountActivity;

import com.czyusercenter.data.User;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
import java.io.IOException;
import com.czyusercenter.utils.PayResult;
import com.alipay.sdk.app.PayTask;
import android.text.TextUtils;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

public class SettingsFragment extends Fragment {
private TextView textShow;
private Button btnBindVip;
private Button btnBuyVip;
	private Handler mHandler;
	private static final int SDK_PAY_FLAG = 1;
	private static final String WX_APP_ID = "wx4d02492ca10c42b9";
//	final User user;
	private IWXAPI api;
public static boolean updatedVipInfo = false;

	public SettingsFragment() {
		// TODO Auto-generated constructor stub
//		user = new User();
	}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
View v = inflater.inflate(R.layout.settings_activity, container, false);
		// TODO Auto-generated constructor stub
		api = WXAPIFactory.createWXAPI(getActivity(), WX_APP_ID);
		//将应用的appid注册到微信
		api.registerApp(WX_APP_ID);

/*
InstallApk installer = new InstallApk(getActivity());
installer.downJson();
*/
//启动umeng统计服务
//MobclickAgent.setScenarioType(getActivity(), EScenarioType.E_UM_NORMAL);

// getActionBar().setDisplayShowHomeEnabled(true);

textShow = (TextView) v.findViewById(R.id.show_textView);
btnBindVip = (Button) v.findViewById(R.id.bind_vip_button);
btnBuyVip = (Button) v.findViewById(R.id.buy_vip_button);

btnBuyVip.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
getKFList(new BuyHttpsDelegate(), "com/xinyang/screenreader/SettingsFragment$BuyHttpsDelegate");
}
});

btnBindVip.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
User user = new User();
if(user.is()) {
Compared compared = new Compared();
compared.gainVipInfo(getActivity(), user.getId(), user.getPhone(), user.getIdCard(), user.getToken(),
new Compared.ComparedListener() {
@Override
public void onCompleted(String say) {
Toast.makeText(getActivity(), say, Toast.LENGTH_SHORT).show();
if(say.indexOf("激活成功")!=-1) {
btnBindVip.setVisibility(View.GONE);
btnBuyVip.setVisibility(View.GONE);
setUserVisibleHint(true);
}
}
@Override
public void onFailed() {
Toast.makeText(getActivity(), R.string.toast_connect_internet_failed, Toast.LENGTH_SHORT).show();
}
});
}
}
});

Button btnAccessibilitySettings = (Button) v.findViewById(R.id.open_accessibility_settings_button);
btnAccessibilitySettings.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
startActivity(intent);
}
});

Button btnLanguageAndInput = (Button) v.findViewById(R.id.open_language_and_input_button);
btnLanguageAndInput.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
try {
Intent intent = new Intent();
intent.setAction("android.intent.action.MAIN");
intent.setComponent(new ComponentName("com.android.settings","com.android.settings.LanguageSettings"));
startActivityForResult(intent, 0);
} catch (Exception e) {
try {
Intent intent2 = new Intent();
intent2.setAction("android.settings.INPUT_METHOD_SETTINGS");
startActivity(intent2);
} catch (Exception ex) {
Toast.makeText(getActivity(), "无法打开，请进入系统界面手动设置！", Toast.LENGTH_LONG).show();
ex.printStackTrace();
}
}
}
});

Button btnApps = (Button) v.findViewById(R.id.open_apps_button);
btnApps.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
startActivity(intent);
}
});

Button btnDeveloper = (Button) v.findViewById(R.id.open_developer_button);
btnDeveloper.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
startActivity(intent);
}
});

Button btnRing = (Button) v.findViewById(R.id.open_setting_ring_button);
btnRing.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
startActivity(intent);
}
});

Button btnTtsSettings = (Button) v.findViewById(R.id.open_tts_settings_button);
btnTtsSettings.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction("com.android.settings.TTS_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
}
});

Button btnSettingsActivity = (Button) v.findViewById(R.id.open_settings_activity_button);
btnSettingsActivity.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent( getActivity(), TalkBackPreferencesActivity.class);
startActivityForResult(intent, 1);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
});

Button btnYZMActivity = (Button) v.findViewById(R.id.yzm_activity_button);
btnYZMActivity.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent( getActivity(), AccountActivity.class);
startActivityForResult(intent, 9);
}
});

/*
Button btnAisoundActivity = (Button) v.findViewById(R.id.open_aisound_activity_button);
btnAisoundActivity.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent( getActivity(), AisoundActivity.class);
startActivityForResult(intent, 2);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
});
*/


mHandler = new Handler(Looper.getMainLooper()) {
@Override
				public void handleMessage(Message msg) {
				if(msg.what == 0){//alipay
					final String payInfo = (String) msg.obj;
					Runnable payRunnable = new Runnable() {
						@Override
						public void run() {
							// 构造PayTask 对象
							PayTask alipay = new PayTask(getActivity());
							// 调用支付接口，获取支付结果
							String result = alipay.pay(payInfo, true);

							Message msg = new Message();
							msg.what = 2;
							msg.obj = result;
							mHandler.sendMessage(msg);
		}
					};
					// 必须异步调用
					Thread payThread = new Thread(payRunnable);
					payThread.start();
    } else if(msg.what == 1) {//wxpay
					final String payInfo =  (String) msg.obj;
					try{
						JSONObject json = new JSONObject(payInfo);
						if(null != json && !json.has("retcode") ){
							PayReq req = new PayReq();
							req.appId			= json.getString("appid");
							req.partnerId		= json.getString("partnerid");
							req.prepayId		= json.getString("prepayid");
							req.nonceStr		= json.getString("noncestr");
							req.timeStamp		= json.getString("timestamp");
							req.packageValue	= json.getString("package");
							req.sign			= json.getString("sign");
							req.extData			= "app data"; // optional
							// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
							api.sendReq(req);
						}
					}catch(Exception e){
						Log.e("PAY_GET", "异常："+e.getMessage());
}
} else if(msg.what == 2) {//alipay result
					PayResult payResult = new PayResult((String) msg.obj);
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
if(TextUtils.equals(resultStatus, "9000")) {
Toast.makeText(getActivity(), "支付成功,请稍后，正在更新账户信息。", Toast.LENGTH_SHORT).show();
					getUserInfo();
					} else {
						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(getActivity(), "支付提交中，请稍后。", Toast.LENGTH_SHORT).show();
					getUserInfo();
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(getActivity(), "支付失败，错误信息：" + resultInfo +  resultStatus, Toast.LENGTH_SHORT).show();
						}
					}
				} else if(msg.what == 3 || msg.what == 4) { // last result
					String json =  (String) msg.obj;
if(json!=null && json.equals("ok")) {
btnBindVip.setVisibility(View.VISIBLE);
btnBuyVip.setVisibility(View.GONE);
textShow.setText("购买VIP服务成功。");
Toast.makeText(getActivity(), "恭喜你成为VIP用户，如果你还没绑定手机，请绑定你的手机。", Toast.LENGTH_SHORT).show();
Compared compared = new Compared();
compared.updateType(getActivity(), true, false);
} else {
Toast.makeText(getActivity(), "更新VIP账户信息超时，请重新登录你的账号刷新查看账号信息。", Toast.LENGTH_SHORT).show();
}
} else if(msg.what == 5 ) {//上服务器检测是不是VIP
getUserVip();
				}
			}
};

return v;
}

@Override
public void onResume() {
	super.onResume();
setUserVisibleHint(true);
if(updatedVipInfo) {
getUserInfo();
}
}

@Override
public void setUserVisibleHint(boolean isVisibleToUser) {
super.setUserVisibleHint(isVisibleToUser);
if(btnBuyVip!=null && btnBindVip!=null && isVisibleToUser) {
Compared compared = new Compared();
final int userType =  compared.checkType(getActivity(),true);
//如果是VIP更新过期日期
// 其他的需要显示
switch( userType ) {
case 1: //特供版
textShow.setText("你使用的是特供版心阳世界。");
break;
case 2:
compared.updateType(getActivity(), false, false);
textShow.setText("你使用的是VIP版心阳世界\n到期日期为： " + compared.getUntilTime());
btnBuyVip.setVisibility(View.VISIBLE);
btnBuyVip.setText("续期VIP服务");
break;
default:
// 如果是普通版，检测用户是否登录，用户是否是VIP或者是特供版，是的话显示激活按钮。
User user = new User();
if(user.is()) {
switch(user.getType()) {
case 0:
textShow.setText("你是普通用户,\n使用的是普通版心阳世界。");
btnBuyVip.setVisibility(View.VISIBLE);
break;
case 1: // 特供
textShow.setText("检测到你是特供版用户，但还未绑定该手机。");
btnBindVip.setVisibility(View.VISIBLE);
break;
case 2:
textShow.setText("检测到你是VIP用户，但还未绑定该手机。");
btnBindVip.setVisibility(View.VISIBLE);
btnBuyVip.setVisibility(View.VISIBLE);
btnBuyVip.setText("续期VIP服务");
break;
} // switch
break;
}
textShow.setText("你还未登录账户\n使用的是普通版心阳世界。");
break;
}
} else {
}
 }

private void getUserInfo() {
Runnable getUserInfoRunnable = new Runnable() {
@Override
public void run() {
try {
Thread.sleep(3000);
} catch (InterruptedException e) {
e.printStackTrace();
}
							Message msg = new Message();
							msg.what = 5;
							msg.obj = null;
							mHandler.sendMessage(msg);
			}
		};
Thread getUserInfoThread = new Thread(getUserInfoRunnable);
getUserInfoThread.start();
}

private void getUserVip() {
Https https = new Https();
https.getText_t( "", new BuyHttpsDelegate(), 3003, "com/xinyang/screenreader/SettingsFragment$BuyHttpsDelegate");
	}

public void getKFList(HttpsDelegate httpsDelegate, String httpsDelegatePath) {
Https https = new Https();
https.getText_t( "", httpsDelegate, 3000, httpsDelegatePath);
}

	private void doWXPay(String agentuserid, String agent, int duration) {
Https https = new Https();
https.getText_t( "agentid=" + agentuserid + "&agentnum=" + agent  + "&duration=" + duration, new BuyHttpsDelegate(), 3002, "com/xinyang/screenreader/SettingsFragment$BuyHttpsDelegate");
	}

private void doAliPay(String agentuserid, String agent, int duration) {
Https https = new Https();
https.getText_t( "agentid=" + agentuserid + "&agentnum=" + agent + "&duration=" + duration, new BuyHttpsDelegate(), 3001, "com/xinyang/screenreader/SettingsFragment$BuyHttpsDelegate");
}

public class BuyHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
if(how == 3000 ) {
try {
JSONArray arr = new JSONArray((String)item.get("json"));
String[] items = new String[arr.length()];
final String[] agents = new String[arr.length()];
final String[] agentuserids = new String[arr.length()];
for(int i=0; i< arr.length(); i++) {
JSONObject obj = arr.getJSONObject(i);
agentuserids[i] = obj.getString("id");
agents[i] = obj.getString("agent");
items[i] = obj.getString("name") + "(" + agents[i] + ")";
}
AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
builder.setIcon( android.R.drawable.ic_dialog_info );
builder.setTitle("选择你的专属客服");
//builder.setMessage((String)item.get("json")); //"你必须先选择售后客服，请和客服核对后选择。");
builder.setItems(items, 
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, final int which) {
						new AlertDialog.Builder(getActivity())
.setTitle("请选择购买时长。")
.setItems(new String[]{"购买一个月（12元）", "购买一年（88元）"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface di, final int span) {
						new AlertDialog.Builder(getActivity())
.setTitle("请选择支付方式")
.setItems(new String[]{"支付宝付款", "微信付款"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
int duration = 1;
switch(span) {
case 1:
duration = 12;
break;
default:
duration = 1;
break;
}
								switch (i) {
									case 0:
										//支付宝支付
doAliPay(agentuserids[which], agents[which], duration);
										break;
									case 1:
										//微信支付
										doWXPay(agentuserids[which], agents[which], duration);
										break;
								}
							}
						}).create().show();
							}
						}).create().show();
}
});
builder.setNegativeButton("取消", null);
builder.show();
} catch(Exception e) {
}
} else if(how == 3001 ) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = (String) item.get("json");
				mHandler.sendMessage(msg);
} else if(how == 3002 ) {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = (String) item.get("json");
				mHandler.sendMessage(msg);
} else if(how == 3003 ) {
				Message msg = new Message();
				msg.what = 4;
				msg.obj = (String) item.get("result");
				mHandler.sendMessage(msg);
}
}
@Override
public void onFailed_m(int errorCode) {
                    Toast.makeText(getActivity(), "连接网络失败，请检查网络状态。", Toast.LENGTH_SHORT).show();  
}
}

}//end class
