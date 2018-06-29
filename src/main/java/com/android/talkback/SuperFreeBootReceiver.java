package com.android.talkback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.marvin.talkback8.TalkBackService;

public class SuperFreeBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
if(intent!=null) {
Bundle bundle = intent.getExtras();
if(bundle!=null) {
        TalkBackService service = TalkBackService.getInstance();
        if (service != null) {
if(bundle.getBoolean("super_free_enabled")) {
service.resumeTalkBack();
}else{
service.suspendTalkBack();
}
        }
}
}
    }

}//end class
