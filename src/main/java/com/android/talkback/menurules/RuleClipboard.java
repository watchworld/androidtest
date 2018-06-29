package com.android.talkback.menurules;

import android.widget.Toast;
import com.android.utils.SharedPreferencesUtils;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Intent;
import java.util.ArrayList;
import android.view.Menu;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.MenuItem;
import com.android.talkback.CursorGranularity;
import com.android.talkback.CursorGranularityManager;
import com.xinyang.screenreader.R;
import com.google.android.marvin.talkback8.TalkBackService;
import com.android.talkback.contextmenu.ContextMenuItem;
import com.android.talkback.contextmenu.ContextMenuItemBuilder;
import com.android.talkback.controller.CursorController;
import com.android.utils.WebInterfaceUtils;
import com.android.utils.AccessibilityNodeInfoUtils;
import com.android.talkback.SpeechController;
import java.util.LinkedList;
import java.util.List;
import android.content.ClipboardManager;
import com.czy.tools.Compared;
import com.czy.virtual.VirtualScreen;
import com.czy.virtual.VirtualScreenActivity;
import com.bdtexample.BDTranslate;

/**
 * Adds supported granularities to the local context menu. If the target node
 * contains web content, adds web-specific granularities.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class RuleClipboard implements NodeMenuRule {
    @Override
    public boolean accept(TalkBackService service, AccessibilityNodeInfoCompat node) {
        return !CursorGranularityManager.getSupportedGranularities(service, node).isEmpty();
    }

    @Override
    public List<ContextMenuItem> getMenuItemsForNode(
            TalkBackService service, ContextMenuItemBuilder menuItemBuilder,
            AccessibilityNodeInfoCompat node) {
        final CursorController cursorController = service.getCursorController();
        final CursorGranularity current = cursorController.getGranularityAt(node);
        final List<ContextMenuItem> items = new LinkedList<>();
        final List<CursorGranularity> granularities = CursorGranularityManager
                .getSupportedGranularities(service, node);

        // Don't populate the menu if only object is supported.
        if (granularities.size() == 1) {
            return items;
        }

        final ClipboardMenuItemClickListener clickListener =
                new ClipboardMenuItemClickListener(service, node);

            ContextMenuItem item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_copy_current_text, Menu.NONE, service.getString(R.string.menu_copy_current_text));
            item.setOnMenuItemClickListener(clickListener);
            items.add(item);
item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_append_copy_current_text, Menu.NONE, service.getString(R.string.menu_append_copy_current_text));
            item.setOnMenuItemClickListener(clickListener);
            items.add(item);

Compared compared = new Compared();
if( compared.checkType(service, false) > 0 ) {
item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_open_secretary, Menu.NONE, service.getString(R.string.menu_open_secretary));
            item.setOnMenuItemClickListener(clickListener);
            items.add(item);
if(BDTranslate.opening) {
item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_close_translate, Menu.NONE, service.getString(R.string.menu_close_translate));
} else {
item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_open_translate, Menu.NONE, service.getString(R.string.menu_open_translate));
}
            item.setOnMenuItemClickListener(clickListener);
            items.add(item);
}
/*
item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_copy_current_title, Menu.NONE, service.getString(R.string.menu_copy_current_title));
            item.setOnMenuItemClickListener(clickListener);
            items.add(item);
item = menuItemBuilder.createMenuItem(service, Menu.NONE,
                    R.string.menu_append_copy_current_title, Menu.NONE, service.getString(R.string.menu_append_copy_current_title));
            item.setOnMenuItemClickListener(clickListener);
            items.add(item);
*/
        return items;
    }

    @Override
    public CharSequence getUserFriendlyMenuName(Context context) {
        return context.getString(R.string.title_clipboard);
    }

    @Override
    public boolean canCollapseMenu() {
        return false;
    }

    private static class ClipboardMenuItemClickListener
            implements MenuItem.OnMenuItemClickListener {

private final Context mContext;
private final AccessibilityNodeInfoCompat mNode;

        public ClipboardMenuItemClickListener(
                TalkBackService service, AccessibilityNodeInfoCompat node) {
mContext = service;
            mNode = AccessibilityNodeInfoCompat.obtain(node);
        }

private CharSequence getCopyText(AccessibilityNodeInfoCompat node) {
if(node==null) {
return null;
}
CharSequence text = AccessibilityNodeInfoUtils.getNodeText(node);
if(text!=null) {
return text;
}

String childStr = "";
int count = node.getChildCount();
for(int i=0; i<count; i++) {
CharSequence childText = getCopyText(node.getChild(i));
if(childText!=null) {
childStr += childText;
childStr += ",";
}
} // for
if( childStr.length() > 1 ) {
text = childStr.substring(0, childStr.length()-1);
}
return text;
}

        @Override
        public boolean onMenuItemClick(MenuItem item) {
if( mNode == null || item == null) {
                    return false;
                }
                final int itemId = item.getItemId();
switch(itemId) {
case R.string.menu_copy_current_text:
{
CharSequence text = getCopyText(mNode);
if(text==null) {
return false;
}
ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
cm.setText(text);
((TalkBackService)mContext).getSpeechController().speak("复制成功。", SpeechController.QUEUE_MODE_UNINTERRUPTIBLE, 0, null);
}
break;
case R.string.menu_append_copy_current_text:
{
CharSequence text = getCopyText(mNode);
if(text==null) {
return false;
}
ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
CharSequence header = cm.getText();
if(header!=null) {
cm.setText( header.toString() + "\n" + text);
((TalkBackService)mContext).getSpeechController().speak("追加复制成功。", SpeechController.QUEUE_MODE_UNINTERRUPTIBLE, 0, null);
} else {
cm.setText(text);
((TalkBackService)mContext).getSpeechController().speak("复制成功。", SpeechController.QUEUE_MODE_UNINTERRUPTIBLE, 0, null);
}
}
break;
case R.string.menu_open_secretary:
{
Compared compared = new Compared();
if( compared.checkType(mContext, true) > 0 ) {
AccessibilityNodeInfo info = ((TalkBackService)mContext).getRootInActiveWindow();
if(info != null) {
VirtualScreen vs = ((TalkBackService)mContext).getVirtualScreen();
if(vs!=null && !vs.getOpening()) {
AccessibilityNodeInfoCompat root = new AccessibilityNodeInfoCompat(info);
ArrayList<String> ls = new ArrayList<String>();
if(vs.parseScreen(root, ls)) {
// vs.setOpening(true);
SpeechController sc = ((TalkBackService)mContext).getSpeechController();
if(sc!=null) {
sc.speak( "打开文本小秘", SpeechController.QUEUE_MODE_UNINTERRUPTIBLE, 0, null);
}
Intent vsActivity = new Intent( mContext, VirtualScreenActivity.class);
            vsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
vsActivity.putStringArrayListExtra("all", ls);
mContext.startActivity(vsActivity);
}
root.recycle();
}
}
}
}
break;
case R.string.menu_open_translate:
case R.string.menu_close_translate:
{
Compared compared = new Compared();
if( compared.checkType(mContext, true) > 0 ) {
// must be vip, can open the translate
if(BDTranslate.opening) {
BDTranslate.opening = false;
Toast.makeText(mContext, "时时翻译关闭。", Toast.LENGTH_SHORT).show();
} else {
BDTranslate.opening = true;
Toast.makeText(mContext, "时时翻译打开。", Toast.LENGTH_SHORT).show();
}
try {
SharedPreferences prefs = SharedPreferencesUtils.getSharedPreferences(mContext);
Editor editor = prefs.edit();
editor.putBoolean(mContext.getString(R.string.pref_translate_key), !prefs.getBoolean(mContext.getString(R.string.pref_translate_key), false));
editor.commit();//提交修改
} catch(Exception e) {
e.printStackTrace();
}
}
}
break;
default:
return false;
}
return true;
        }

    }

}
