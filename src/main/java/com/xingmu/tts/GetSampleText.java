package com.xingmu.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class GetSampleText extends Activity {

@Override
protected void onCreate(Bundle bundle) {
super.onCreate(bundle);
Intent localIntent = new Intent();
Intent it = getIntent();
Bundle bun = it.getExtras();
if( bun != null) {
String lan = bun.getString("language");
//if(lan.equals("zh")) {
localIntent.putExtra("sampleText", "hello world");
setResult(1, localIntent);
//    }else{
//}
}
      finish();
  }

}
