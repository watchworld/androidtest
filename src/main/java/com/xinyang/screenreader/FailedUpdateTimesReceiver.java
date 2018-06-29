package com.xinyang.screenreader;

import com.google.android.marvin.talkback8.TalkBackService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.czy.tools.Compared;
import net.czy.manager.InstallApk;

public class FailedUpdateTimesReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {
// TODO Auto-generated method stub  
final  TalkBackService ts = TalkBackService.getInstance();
if( ts != null ) {
final Compared compared = new Compared();
int type = compared.checkType(ts, true);
if( type == 2 ) {
new Thread(new Runnable() {
@Override
public void run() {
if(compared.updateType(ts, false, false)>0) {
//InstallApk installApk = new InstallApk(ts);
//installApk.downJson();
}
}
});
} else if( type == 0 ) {
ts.regainVipType();
}
}
    }  
}  
