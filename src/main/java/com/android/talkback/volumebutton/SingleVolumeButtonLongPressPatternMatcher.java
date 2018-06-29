package com.android.talkback.volumebutton;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

public class SingleVolumeButtonLongPressPatternMatcher extends VolumeButtonPatternMatcher {

    private static final int LONG_PRESS_TIMEOUT = 1000; // ViewConfiguration.getLongPressTimeout();

    private VolumeButtonAction mVolumeUpAction;

    public SingleVolumeButtonLongPressPatternMatcher(int keyCode) {
        super(VolumeButtonPatternDetector.LONG_PRESS_PATTERN, keyCode);
    }

    @Override
    public void onKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != getButtonCombination()) {
            return;
        }

        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            handleActionDownEvent(keyEvent);
        } else {
            handleActionUpEvent(keyEvent);
        }
    }

    private void handleActionDownEvent(KeyEvent event) {
mVolumeUpAction = createAction(event);
    }

    private void handleActionUpEvent(KeyEvent event) {
        if (mVolumeUpAction != null) {
            mVolumeUpAction.pressed = false;
            mVolumeUpAction.endTimestamp = event.getEventTime();
        }
    }

    @Override
    public boolean checkMatch() {
        if (mVolumeUpAction == null ) {
            return false;
        }
long doubleButtonStartTimestamp = mVolumeUpAction.startTimestamp;
        long upButtonEndTimestamp = mVolumeUpAction.pressed ? SystemClock.uptimeMillis() :
                mVolumeUpAction.endTimestamp;
        long doubleButtonEndTimestamp = upButtonEndTimestamp;
        return doubleButtonEndTimestamp - doubleButtonStartTimestamp > LONG_PRESS_TIMEOUT;
    }

    @Override
    public void clear() {
        mVolumeUpAction = null;
    }
}
