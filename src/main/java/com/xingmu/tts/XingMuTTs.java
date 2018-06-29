package com.xingmu.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;

public class XingMuTTs extends Activity {
private static final String[] supportedLanguages = { "zh", "eng" };

@Override
protected void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);

ArrayList<String> localArrayList = new ArrayList<String>();
ArrayList<String> supportArrayList = new ArrayList<String>();

Bundle bun = getIntent().getExtras();
    if (bun != null) {
ArrayList<String> arr = bun.getStringArrayList("checkVoiceDataFor");
if(arr != null) {
for(int i=0; i < arr.size(); i++) {
if(arr.get(i).length() > 0) {
          }
        }//for
      }//arr
    }//bun

for(int i=0;i<supportedLanguages.length;i++)    {
supportArrayList.add(supportedLanguages[i]);
    }

Intent it     = new Intent();
it.putStringArrayListExtra("availableVoices", supportArrayList);
it.putStringArrayListExtra("unavailableVoices", localArrayList);
    setResult(1, it);
finish();
  }

}
