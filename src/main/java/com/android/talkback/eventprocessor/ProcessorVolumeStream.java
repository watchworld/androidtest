package com.android.talkback.eventprocessor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.NonNull;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.android.talkback.CursorGranularity;
import com.xinyang.screenreader.R;
import com.android.talkback.SpeechController;
import com.android.utils.Role;
import com.android.utils.WeakReferenceHandler;
import com.google.android.marvin.talkback8.TalkBackService;
import com.android.talkback.controller.CursorController;
import com.android.talkback.controller.DimScreenController;
import com.android.talkback.controller.FeedbackController;
import com.android.talkback.volumebutton.VolumeButtonPatternDetector;
import com.android.utils.AccessibilityEventListener;
import com.android.utils.AccessibilityNodeInfoUtils;
import com.android.utils.PerformActionUtils;
import com.android.utils.SharedPreferencesUtils;

import java.util.List;
import com.czy.tools.Compared;
import android.widget.Toast;
import com.czy.virtual.VirtualScreen;
import com.czy.virtual.VirtualScreenActivity;
import android.content.Intent;
import java.util.ArrayList;
import android.content.SharedPreferences.Editor;
import com.bdtexample.BDTranslate;

/**
 * Locks the volume control stream during a touch interaction event.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ProcessorVolumeStream implements AccessibilityEventListener,
        TalkBackService.KeyEventListener, VolumeButtonPatternDetector.OnPatternMatchListener {
    /** Minimum API version required for this class to function. */
    public static final int MIN_API_LEVEL = Build.VERSION_CODES.JELLY_BEAN_MR2;

    private static final boolean API_LEVEL_SUPPORTS_WINDOW_NAVIGATION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;

    /** Default flags for volume adjustment while touching the screen. */
    private static final int DEFAULT_FLAGS_TOUCHING_SCREEN = (AudioManager.FLAG_SHOW_UI
            | AudioManager.FLAG_VIBRATE);

    /** Default flags for volume adjustment while not touching the screen. */
    private static final int DEFAULT_FLAGS_NOT_TOUCHING_SCREEN = (AudioManager.FLAG_SHOW_UI
            | AudioManager.FLAG_VIBRATE | AudioManager.FLAG_PLAY_SOUND);

    /** Stream to control when the user is touching the screen. */
    private static final int STREAM_TOUCHING_SCREEN = SpeechController.DEFAULT_STREAM;

    /** Stream to control when the user is not touching the screen. */
    private static final int STREAM_DEFAULT = AudioManager.USE_DEFAULT_STREAM_TYPE;

    /** Tag used for identification of the wake lock held by this class */
    private static final String WL_TAG = ProcessorVolumeStream.class.getSimpleName();

    /** The audio manager, used to adjust speech volume. */
    private final AudioManager mAudioManager;

    /** WakeLock used to keep the screen active during key events */
    private final WakeLock mWakeLock;

    /** Handler for completing volume key handling outside of the main key-event handler. */
    private final VolumeStreamHandler mHandler = new VolumeStreamHandler(this);

    /**
     * The cursor controller, used for determining the focused node and
     * navigating.
     */
    private final CursorController mCursorController;

    /**
     * Feedback controller for providing feedback on boundaries during volume
     * key navigation.
     */
    private final FeedbackController mFeedbackController;

    /** Whether the user is touching the screen. */
    private boolean mTouchingScreen = false;
    private SharedPreferences mPrefs;
    private TalkBackService mService;
    private DimScreenController mDimScreenController;

    private VolumeButtonPatternDetector mPatternDetector;

    @SuppressWarnings("deprecation")
    public ProcessorVolumeStream(FeedbackController feedbackController,
                                 CursorController cursorController,
                                 DimScreenController dimScreenController,
                                 TalkBackService service) {
        if (feedbackController == null) throw new IllegalStateException(
                "CachedFeedbackController is null");
        if (cursorController == null) throw new IllegalStateException("CursorController is null");
        if (dimScreenController == null) throw new IllegalStateException(
                "DimScreenController is null");

        mAudioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
        mCursorController = cursorController;
        mFeedbackController = feedbackController;

        final PowerManager pm = (PowerManager) service.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, WL_TAG);

        mPrefs = SharedPreferencesUtils.getSharedPreferences(service);
        mService = service;
        mDimScreenController = dimScreenController;
        mPatternDetector = new VolumeButtonPatternDetector();
        mPatternDetector.setOnPatternMatchListener(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                mTouchingScreen = true;
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                mTouchingScreen = false;
                break;
        }
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        boolean handled = mPatternDetector.onKeyEvent(event);

        if (handled) {
            // Quickly acquire and release the wake lock so that
            // PowerManager.ON_AFTER_RELEASE takes effect.
            mWakeLock.acquire();
            mWakeLock.release();
        }

        return handled;
    }

    @Override
    public boolean processWhenServiceSuspended() {
        return true;
    }

    private void handleBothVolumeKeysLongPressed() {
        if (TalkBackService.isServiceActive() && switchTalkBackActiveStateEnabled()) {
            mService.requestSuspendTalkBack();
        } else {
            mService.resumeTalkBack();
        }
    }

    private boolean switchTalkBackActiveStateEnabled() {
        return SharedPreferencesUtils.getBooleanPref(mPrefs, mService.getResources(),
                R.string.pref_two_volume_long_press_key,
                R.bool.pref_resume_volume_buttons_long_click_default);
    }

    private void navigateSlider(int button, @NonNull AccessibilityNodeInfoCompat node) {
        int action;
        if (button == VolumeButtonPatternDetector.VOLUME_UP) {
            action = AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD;
        } else if (button == VolumeButtonPatternDetector.VOLUME_DOWN) {
            action = AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD;
        } else {
            return;
        }

        PerformActionUtils.performAction(node, action);
    }

    private void navigateEditText(int button, @NonNull AccessibilityNodeInfoCompat node) {
        boolean result = false;

        Bundle args = new Bundle();
        CursorGranularity currentGranularity = mCursorController.getGranularityAt(node);
        if (currentGranularity != CursorGranularity.DEFAULT) {
            args.putInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                    currentGranularity.value);
        } else {
            args.putInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                    AccessibilityNodeInfoCompat.MOVEMENT_GRANULARITY_CHARACTER);
        }

        if (mCursorController.isSelectionModeActive()) {
            args.putBoolean(
                    AccessibilityNodeInfoCompat.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
        }

        EventState.getInstance().addEvent(
                EventState.EVENT_SKIP_FOCUS_PROCESSING_AFTER_GRANULARITY_MOVE);
        EventState.getInstance().addEvent(
                EventState.EVENT_SKIP_HINT_AFTER_GRANULARITY_MOVE);

        if (button == VolumeButtonPatternDetector.VOLUME_UP) {
            result = PerformActionUtils.performAction(node,
                    AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, args);
        } else if (button == VolumeButtonPatternDetector.VOLUME_DOWN) {
            result = PerformActionUtils.performAction(node,
                    AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, args);
        }

        if (!result) {
            mFeedbackController.playAuditory(R.raw.complete);
        }
    }

    private boolean attemptNavigation(int button) {
        AccessibilityNodeInfoCompat node = mCursorController.getCursorOrInputCursor();

        // Clear focus if it is on an IME
        if (node != null) {
            if (API_LEVEL_SUPPORTS_WINDOW_NAVIGATION) {
                for (AccessibilityWindowInfo awi : mService.getWindows()) {
                    if (awi.getId() == node.getWindowId()) {
                        if (awi.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                            node.recycle();
                            node = null;
                        }
                        break;
                    }
                }
            }
        }

        // If we cleared the focus before it is on an IME, try to get the current node again.
        if (node == null) {
            node = mCursorController.getCursorOrInputCursor();
        }

        if (node == null) return false;
        try {
            if (Role.getRole(node) == Role.ROLE_SEEK_CONTROL) {
                navigateSlider(button, node);
                return true;
            }

            // In general, do not allow volume key navigation when the a11y focus is placed but
            // it is not on the edit field that the keyboard is currently editing.
            //
            // Example 1:
            // EditText1 has input focus and EditText2 has accessibility focus.
            // getCursorOrInputCursor() will return EditText2 based on its priority order.
            // EditText2.isFocused() = false, so we should not allow volume keys to control text.
            //
            // Example 2:
            // EditText1 in Window1 has input focus. EditText2 in Window2 has input focus as well.
            // If Window1 is input-focused but Window2 has the accessibility focus, don't allow
            // the volume keys to control the text.
            boolean nodeWindowFocused;
            if (API_LEVEL_SUPPORTS_WINDOW_NAVIGATION) {
                nodeWindowFocused = node.getWindow() != null && node.getWindow().isFocused();
            } else {
                nodeWindowFocused = true;
            }

            if (node.isFocused() && nodeWindowFocused &&
                    AccessibilityNodeInfoUtils.isEditable(node)) {
                navigateEditText(button, node);
                return true;
            }

            return false;
      } finally {
            AccessibilityNodeInfoUtils.recycleNodes(node);
        }
    }

private void adjustVolumeFromKeyEvent(int button) {
mPatternDetector.clearState();

        final int direction = ((button == VolumeButtonPatternDetector.VOLUME_UP) ?
AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER);

mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
direction, AudioManager.FLAG_PLAY_SOUND);

SpeechController sc = mService.getSpeechController();
if(sc!=null) {
Double max = new Double( mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
Double current = new Double(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
sc.speak( String.format("百分之 %d", Math.round(current / max * 100)),
SpeechController.QUEUE_MODE_INTERRUPT, 0, null);
}
/*
        if (mTouchingScreen) {
            mAudioManager.adjustStreamVolume(
                    STREAM_TOUCHING_SCREEN, direction, DEFAULT_FLAGS_TOUCHING_SCREEN);
        } else {
            // Attempt to adjust the suggested stream, but let the system
            // override in special situations like during voice calls, when an
            // application has locked the volume control stream, or when music
            // is playing.
            mAudioManager.adjustSuggestedStreamVolume(
                    direction, STREAM_DEFAULT, DEFAULT_FLAGS_NOT_TOUCHING_SCREEN);
        }
}
*/
    }

    @Override
    public void onPatternMatched(int patternCode, int buttonCombination) {
        mHandler.postPatternMatched(patternCode, buttonCombination);
    }

private int clickedTimeUp = 0;
private int clickedTimeDown = 0;

public void onPatternMatchedInternal(int patternCode, final int buttonCombination) {
        switch (patternCode) {
case VolumeButtonPatternDetector.SHORT_PRESS_PATTERN:
// mPatternDetector.clearState();
if( buttonCombination == VolumeButtonPatternDetector.VOLUME_UP ) {
if(clickedTimeUp == 0) {
clickedTimeUp = 1;
mHandler.postDelayed( new Runnable() {
@Override
public void run() {
clickedTimeUp = 0;
handleSingleTap(buttonCombination);
}
}, 300);
}else{
clickedTimeUp = 0;
mHandler.removeCallbacksAndMessages(null);
//                mPatternDetector.clearState();
if(mService!=null) {
try {
Intent newIntent = new Intent();
newIntent.setAction("com.czyalarm.MUSIC_SERVICE_ACTION");
newIntent.setPackage("com.czy.alarm");
newIntent.putExtra("which", "speak_current_time");
mService.startService(newIntent);
} catch(Exception e) {
}
}
}
}else if( buttonCombination == VolumeButtonPatternDetector.VOLUME_DOWN ) {
if(clickedTimeDown == 0) {
clickedTimeDown = 1;
mHandler.postDelayed( new Runnable() {
@Override
public void run() {
clickedTimeDown = 0;
handleSingleTap(buttonCombination);
}
}, 300);
}else{
clickedTimeDown = 0;
mHandler.removeCallbacksAndMessages(null);
//                mPatternDetector.clearState();
if(mService!=null) {
// 必须是VIP才能打开文本小蜜
Compared compared = new Compared();
if( compared.checkType(mService, true) > 0 ) {
AccessibilityNodeInfo info = mService.getRootInActiveWindow();
if(info != null) {
VirtualScreen vs = mService.getVirtualScreen();
if(vs!=null && !vs.getOpening()) {
AccessibilityNodeInfoCompat root = new AccessibilityNodeInfoCompat(info);
ArrayList<String> ls = new ArrayList<String>();
if(vs.parseScreen(root, ls)) {
// vs.setOpening(true);
SpeechController sc = mService.getSpeechController();
if(sc!=null) {
sc.speak( "打开文本小秘", SpeechController.QUEUE_MODE_UNINTERRUPTIBLE, 0, null);
}
Intent vsActivity = new Intent( mService, VirtualScreenActivity.class);
            vsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
vsActivity.putStringArrayListExtra("all", ls);
mService.startActivity(vsActivity);
}
root.recycle();
}
}
} else {
Toast.makeText(mService, "文本小秘必须VIP或特供版才能使用。", Toast.LENGTH_SHORT).show();				
}
}
}
} else {
handleSingleTap(buttonCombination);
}
                break;
case VolumeButtonPatternDetector.LONG_PRESS_PATTERN:
mPatternDetector.clearState();
if( buttonCombination == VolumeButtonPatternDetector.VOLUME_UP ) {
if(mService!=null) {
try {
Intent newIntent = new Intent();
newIntent.setAction("com.czyalarm.MUSIC_SERVICE_ACTION");
newIntent.setPackage("com.czy.alarm");
newIntent.putExtra("which", "start_meter_key_long_pressed");
mService.startService(newIntent);
} catch(Exception e) {
}
}
} else if( buttonCombination == VolumeButtonPatternDetector.VOLUME_DOWN ) {
if(mService!=null) {
// must be vip, can open the translate
Compared compared = new Compared();
if( compared.checkType(mService, true) > 0 ) {
if(BDTranslate.opening) {
BDTranslate.opening = false;
Toast.makeText(mService, "时时翻译关闭。", Toast.LENGTH_SHORT).show();
} else {
BDTranslate.opening = true;
Toast.makeText(mService, "时时翻译打开。", Toast.LENGTH_SHORT).show();
}
try {
Editor editor = mPrefs.edit();
editor.putBoolean(mService.getString(R.string.pref_translate_key), !mPrefs.getBoolean(mService.getString(R.string.pref_translate_key), false));
editor.commit();//提交修改
} catch(Exception e) {
e.printStackTrace();
}
} else {
Toast.makeText(mService, "时时翻译必须VIP或特供版才能使用。", Toast.LENGTH_SHORT).show();
}
}
}
break;
            case VolumeButtonPatternDetector.TWO_BUTTONS_LONG_PRESS_PATTERN:
                handleBothVolumeKeysLongPressed();
                mPatternDetector.clearState();
                break;
            case VolumeButtonPatternDetector.TWO_BUTTONS_THREE_PRESS_PATTERN:
                if (!mService.isInstanceActive()) {
                    // If the service isn't active, the user won't get any feedback that
                    // anything happened, so we shouldn't change the dimming setting.
                    return;
                }

                boolean globalShortcut = isTripleClickEnabledGlobally();
                boolean dimmed = mDimScreenController.isDimmingEnabled();

                if (dimmed && (globalShortcut || mDimScreenController.isInstructionDisplayed())) {
                    mDimScreenController.disableDimming();
                } else if (!dimmed && globalShortcut) {
                    mDimScreenController.showDimScreenDialog();
                }

                break;
        }
    }

private void handleSingleTap(int button) {
        if (TalkBackService.isServiceActive() && attemptNavigation(button)) {
            return;
        }

if( TalkBackService.getServiceState() == TalkBackService.SERVICE_STATE_ACTIVE) {
adjustVolumeFromKeyEvent(button);
}
    }

private boolean isTripleClickEnabledGlobally() {
        SharedPreferences prefs = SharedPreferencesUtils.getSharedPreferences(mService);
        return SharedPreferencesUtils.getBooleanPref(prefs, mService.getResources(),
                R.string.pref_dim_volume_three_clicks_key,
                R.bool.pref_dim_volume_three_clicks_default);
    }

    /**
     * Used to run potentially long methods outside of the key handler so that we don't ever
     * hit the key handler timeout.
     */
    private static final class VolumeStreamHandler
            extends WeakReferenceHandler<ProcessorVolumeStream> {

        public VolumeStreamHandler(ProcessorVolumeStream parent) {
            super(parent);
        }

        @Override
        protected void handleMessage(Message msg, ProcessorVolumeStream parent) {
            int patternCode = msg.arg1;
            int buttonCombination = msg.arg2;
            parent.onPatternMatchedInternal(patternCode, buttonCombination);
        }

        public void postPatternMatched(int patternCode, int buttonCombination) {
            Message msg = obtainMessage(0 /* what */,
                    patternCode /* arg1 */,
                    buttonCombination /* arg2 */);
            sendMessage(msg);
        }

    }
}
