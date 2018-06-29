package com.xingmu.tts;

import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeechService;
import com.android.utils.FailoverTextToSpeech;

public class CZYTextToSpeechService extends TextToSpeechService {

	public CZYTextToSpeechService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String[] onGetLanguage() {
		// TODO Auto-generated method stub
		return new String[] { "zh", "CHN", "" };
	}

	@Override
	protected int onIsLanguageAvailable(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int onLoadLanguage(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

@Override
public void onCreate() {
    super.onCreate();
FailoverTextToSpeech.getSynthProxy(this);
}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
SynthProxy.stop();
	}

	@Override
	protected void onSynthesizeText(SynthesisRequest arg0,
			SynthesisCallback arg1) {
		// TODO Auto-generated method stub
SynthProxy sp = FailoverTextToSpeech.getSynthProxy(this);
if(sp!=null) {
sp.speak(arg0, arg1);
}
	}

}
