package com.android.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.xinyang.screenreader.R;
import com.google.android.marvin.talkback8.TalkBackService;
import com.android.utils.compat.CompatUtils;

public class MyAccessibilityNodeInfoUtils {
    /**
     * Class for Samsung's TouchWiz implementation of AdapterView. May be
     * {@code null} on non-Samsung devices.
     */
    private static final Class<?> CLASS_TOUCHWIZ_TWADAPTERVIEW = CompatUtils.getClass(
            "com.sec.android.touchwiz.widget.TwAdapterView");

    /**
     * Class for Samsung's TouchWiz implementation of AbsListView. May be
     * {@code null} on non-Samsung devices.
     */
    private static final Class<?> CLASS_TOUCHWIZ_TWABSLISTVIEW = CompatUtils.getClass(
            "com.sec.android.touchwiz.widget.TwAbsListView");

    private static final String CLASS_RECYCLER_VIEW_CLASS_NAME =
"android.support.v7.widget.RecyclerView";

    /**
     * Determines if the generating class of an
     * {@link AccessibilityNodeInfoCompat} matches any of the given
     * {@link Class}es by type.
     *
     * @param node A sealed {@link AccessibilityNodeInfoCompat} dispatched by
     *            the accessibility framework.
     * @return {@code true} if the {@link AccessibilityNodeInfoCompat} object
     *         matches the {@link Class} by type or inherited type,
     *         {@code false} otherwise.
     * @param referenceClasses A variable-length list of {@link Class} objects
     *            to match by type or inherited type.
     */
    private static boolean nodeMatchesAnyClassByType(AccessibilityNodeInfoCompat node,
                                                     Class<?>... referenceClasses) {
        if (node == null)
            return false;

        for (Class<?> referenceClass : referenceClasses) {
            if (ClassLoadingCache.checkInstanceOf(node.getClassName(), referenceClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a given node is scrollable.
     *
     * @param node The node to examine.
     * @return {@code true} if the node is scrollable.
     */
    private static boolean isScrollable(AccessibilityNodeInfoCompat node) {
        return node.isScrollable()
                || supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD,
                                     AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD);

    }

    /**
     * Determines if the class of an {@link AccessibilityNodeInfoCompat} matches
     * a given {@link Class} by package and name.
     *
     * @param node A sealed {@link AccessibilityNodeInfoCompat} dispatched by
     *            the accessibility framework.
     * @param referenceClassName A class name to match.
     * @return {@code true} if the {@link AccessibilityNodeInfoCompat} matches
     *         the class name.
     */
    public static boolean nodeMatchesClassByName(AccessibilityNodeInfoCompat node,
                                                 CharSequence referenceClassName) {
        return node != null &&
                ClassLoadingCache.checkInstanceOf(node.getClassName(), referenceClassName);

    }

    /**
     * Determines whether a node is a top-level item in a scrollable container.
 * 确认一个ITEM是否是可滚动的顶部元素。
     *
     * @param node The node to test.
     * @return {@code true} if {@code node} is a top-level item in a scrollable
     *         container.
     */
    public static boolean isTopLevelScrollItem(AccessibilityNodeInfoCompat node) {
        if (node == null) {
            return false;
        }

        AccessibilityNodeInfoCompat parent = null;
        AccessibilityNodeInfoCompat grandparent = null;

        try {
            parent = node.getParent();
            if (parent == null) {
                // Not a child node of anything.
                return false;
            }

            // Certain scrollable views in M's Android TV SetupWraith are permanently broken and
            // won't ever be fixed because the setup wizard is bundled. This affects <= M only.
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M &&
//FILTER_BROKEN_LISTS_TV_M.accept(parent)) {
//                return false;
//            }

if(isScrollable(node)) {
                return true;
            }

            // AdapterView, ScrollView, and HorizontalScrollView are focusable
            // containers, but Spinner is a special case.
            // TODO: Rename or break up this method, since it actually returns
            // whether the parent is scrollable OR is a focusable container that
            // should not block its children from receiving focus.
            //noinspection SimplifiableIfStatement
            if (Role.getRole(parent) == Role.ROLE_DROP_DOWN_LIST) return false;

            // Top-level items in a scrolling pager are actually two levels down since the first
            // level items in pagers are the pages themselves.
            grandparent = parent.getParent();
            if (Role.getRole(grandparent) == Role.ROLE_PAGER) return true;

            return nodeMatchesAnyClassByType(parent, AdapterView.class, ScrollView.class,
                    HorizontalScrollView.class, CLASS_TOUCHWIZ_TWADAPTERVIEW) ||
                    nodeMatchesClassByName(parent, CLASS_RECYCLER_VIEW_CLASS_NAME);
        } finally {
            recycleNodes(parent, grandparent);
        }
    }

    /**
     * Returns whether the specified node has text.
     * For the purposes of this check, any node with a CollectionInfo is considered to not have
     * text since its text and content description are used only for collection transitions.
     *
     * @param node The node to check.
     * @return {@code true} if the node has text.
     */
private static boolean hasText(AccessibilityNodeInfoCompat node) {
        return node != null
                && node.getCollectionInfo() == null
                && (!TextUtils.isEmpty(node.getText())
                        || !TextUtils.isEmpty(node.getContentDescription()));

    }

    private static boolean hasNonActionableSpeakingChildren(AccessibilityNodeInfoCompat node,
                                    Map<AccessibilityNodeInfoCompat, Boolean> speakingNodeCache,
                                    Set<AccessibilityNodeInfoCompat> visitedNodes) {
        final int childCount = node.getChildCount();

        AccessibilityNodeInfoCompat child;

// Has non-actionable, speaking children?
        for (int i = 0; i < childCount; i++) {
            child = node.getChild(i);

            if (child == null) {
                continue;
            }

            if (!visitedNodes.add(child)) {
                child.recycle();
                return false;
            }

            // Ignore invisible nodes.
            if (!isVisible(child)) {
                continue;
            }

            // Ignore focusable nodes.
            if (isAccessibilityFocusableInternal(child, speakingNodeCache, visitedNodes)) {
                continue;
            }

            // Recursively check non-focusable child nodes.
            if (isSpeakingNode(child, speakingNodeCache, visitedNodes)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns {@code true} if the node supports at least one of the specified
     * actions. To check whether a node supports multiple actions, combine them
     * using the {@code |} (logical OR) operator.
     *
     * Note: this method will check against the getActions() method of AccessibilityNodeInfo, which
     * will not contain information for actions introduced in API level 21 or later.
     *
     * @param node The node to check.
     * @param actions The actions to check.
     * @return {@code true} if at least one action is supported.
     */
    public static boolean supportsAnyAction(AccessibilityNodeInfoCompat node,
            int... actions) {
        if (node != null) {
            final int supportedActions = node.getActions();

            for (int action : actions) {
                if ((supportedActions & action) == action) {
                    return true;
                }
            }
        }

        return false;
    }

private static boolean isSpeakingNode(AccessibilityNodeInfoCompat node,
Map<AccessibilityNodeInfoCompat, Boolean> speakingNodeCache,
Set<AccessibilityNodeInfoCompat> visitedNodes) {
        if (speakingNodeCache != null && speakingNodeCache.containsKey(node)) {
            return speakingNodeCache.get(node);
        }

        boolean result = false;
        if (hasText(node)) {
            result = true;
        } else if (node.isCheckable()) { // Special case for check boxes.
            result = true;
//        } else if (WebInterfaceUtils.hasLegacyWebContent(node)) { // Special case for web content.
//            result = true;
        } else if (hasNonActionableSpeakingChildren(node, speakingNodeCache, visitedNodes)) {
            // Special case for containers with non-focusable content.
            result = true;
        }

        if (speakingNodeCache != null) {
            speakingNodeCache.put(node, result);
        }

        return result;
    }

    /**
     * Helper method that returns {@code true} if the specified node is visible
     * to the user
     */
    public static boolean isVisible(AccessibilityNodeInfoCompat node) {
        return node != null && (node.isVisibleToUser() /* || WebInterfaceUtils.isWebContainer(node) */ );
    }

    /**
     * Returns whether a node is clickable. That is, the node supports at least one of the
     * following:
     * <ul>
     * <li>{@link AccessibilityNodeInfoCompat#isClickable()}</li>
     * <li>{@link AccessibilityNodeInfoCompat#ACTION_CLICK}</li>
     * </ul>
     *
     * @param node The node to examine.
     * @return {@code true} if node is clickable.
     */
    public static boolean isClickable(AccessibilityNodeInfoCompat node) {
        return node != null && (node.isClickable()
                || supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_CLICK));
    }

    /**
     * Returns whether a node is long clickable. That is, the node supports at least one of the
     * following:
     * <ul>
     * <li>{@link AccessibilityNodeInfoCompat#isLongClickable()}</li>
     * <li>{@link AccessibilityNodeInfoCompat#ACTION_LONG_CLICK}</li>
     * </ul>
     *
     * @param node The node to examine.
     * @return {@code true} if node is long clickable.
     */
    public static boolean isLongClickable(AccessibilityNodeInfoCompat node) {
        return node != null
                && (node.isLongClickable()
                        || supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_LONG_CLICK));

    }

    /**
     * Returns whether a node is expandable. That is, the node supports the following action:
     * <ul>
     * <li>{@link AccessibilityNodeInfoCompat#ACTION_EXPAND}</li>
     * </ul>
     *
     * @param node The node to examine.
     * @return {@code true} if node is expandable.
     */
    public static boolean isExpandable(AccessibilityNodeInfoCompat node) {
        return node != null && supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_EXPAND);
    }

    /**
     * Returns whether a node is collapsible. That is, the node supports the following action:
     * <ul>
     * <li>{@link AccessibilityNodeInfoCompat#ACTION_COLLAPSE}</li>
     * </ul>
     *
     * @param node The node to examine.
     * @return {@code true} if node is collapsible.
     */
    public static boolean isCollapsible(AccessibilityNodeInfoCompat node) {
        return node != null && supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
    }

    /**
     * Returns whether a node is actionable. That is, the node supports one of
     * the following actions:
     * <ul>
     * <li>{@link AccessibilityNodeInfoCompat#isClickable()}
     * <li>{@link AccessibilityNodeInfoCompat#isFocusable()}
     * <li>{@link AccessibilityNodeInfoCompat#isLongClickable()}
     * </ul>
     * This parities the system method View#isActionableForAccessibility(), which
     * was added in JellyBean.
     *
     * @param node The node to examine.
     * @return {@code true} if node is actionable.
     */
    public static boolean isActionableForAccessibility(AccessibilityNodeInfoCompat node) {
        if (node == null) {
            return false;
        }

        // Nodes that are clickable are always actionable.
        if (isClickable(node) || isLongClickable(node)) {
            return true;
        }

        if (node.isFocusable()) {
            return true;
        }

//        if (WebInterfaceUtils.hasNativeWebContent(node)) {
//            return supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_FOCUS);
//        }

        return supportsAnyAction(node, AccessibilityNodeInfoCompat.ACTION_FOCUS,
                AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT,
                AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT);
    }

private static boolean isAccessibilityFocusableInternal(AccessibilityNodeInfoCompat node,
Map<AccessibilityNodeInfoCompat, Boolean> speakingNodeCache,
Set<AccessibilityNodeInfoCompat> visitedNodes) {
        if (node == null) {
            return false;
        }

        // Never focus invisible nodes.
if(!node.isVisibleToUser()) {
            return false;
        }

        // Always focus "actionable" nodes.
if(isActionableForAccessibility(node)) {
            return true;
        }

return  /* isTopLevelScrollItem(node) &&*/
isSpeakingNode(node, speakingNodeCache, visitedNodes);
    }

    /**
     * Recycles the given nodes.
     *
     * @param nodes The nodes to recycle.
     */
    public static void recycleNodes(Collection<AccessibilityNodeInfoCompat> nodes) {
        if (nodes == null) {
            return;
        }

        for (AccessibilityNodeInfoCompat node : nodes) {
            if (node != null) {
                node.recycle();
            }
        }

        nodes.clear();
    }

    /**
     * Recycles the given nodes.
     *
     * @param nodes The nodes to recycle.
     */
    public static void recycleNodes(AccessibilityNodeInfoCompat... nodes) {
        if (nodes == null) {
            return;
        }

        for (AccessibilityNodeInfoCompat node : nodes) {
            if (node != null) {
                node.recycle();
            }
        }
    }

    /**
     * Returns whether a node should receive focus from focus traversal or touch
     * exploration. One of the following must be true:
     * <ul>
     * <li>The node is actionable (see
     * {@link #isActionableForAccessibility(AccessibilityNodeInfoCompat)})</li>
     * <li>The node is a top-level list item (see
     * {@link #isTopLevelScrollItem(AccessibilityNodeInfoCompat)})</li>
     * </ul>
     *
     * @param node The node to check.
     * @return {@code true} of the node is accessibility focusable.
     */
public static boolean isAccessibilityFocusable(AccessibilityNodeInfoCompat node) {
        Set<AccessibilityNodeInfoCompat> visitedNodes = new HashSet<AccessibilityNodeInfoCompat>();
        try {
            return isAccessibilityFocusableInternal(node, null, visitedNodes);
        } finally {
MyAccessibilityNodeInfoUtils.recycleNodes(visitedNodes);
        }
    }

    /**
     * Gets the text of a <code>node</code> by returning the content description
     * (if available) or by returning the text.
     *with children node
     *
     * @param node The node.
     * @return The node text.
     */
public static CharSequence getAllNodeText( AccessibilityNodeInfoCompat node) {
StringBuilder builder = new StringBuilder();

CharSequence label = AccessibilityNodeInfoUtils.getNodeText( node );
if(label != null ) {
builder.append(label);
} else {
for(int i=0; i<node.getChildCount(); i++) {
AccessibilityNodeInfoCompat child = node.getChild(i);
if( child != null ) {
if( child.getChildCount()>0 ) {
CharSequence c = getAllNodeText( child );
if( c != null ) {
builder.append(c);
}
} else {
CharSequence c = AccessibilityNodeInfoUtils.getNodeText( child );
if( c != null ) {
builder.append(c);
}
}
} // has
} // for
}

return builder.length()==0 ? null : builder.toString();
}

public static String getNodeFormat( Context context, AccessibilityNodeInfoCompat node, AccessibilityEvent event, boolean emptyLabel) {
if( node == null ) {
return null;
}

StringBuilder ext = new StringBuilder("%s");
//if(!emptyLabel) {
//ext.append(",");
//}

	String className = node.getClassName().toString();

if("android.widget.TextView".equals(className)) {
if(emptyLabel) {
ext.append(context.getString(R.string.value_text));
}
} else if( "android.widget.ImageButton".equals(className) || "android.widget.Button".equals(className)) {
ext.append( context.getString(R.string.value_button) );
} else if("android.widget.ImageView".equals(className)) {
ext.append( context.getString(R.string.value_tabwidget));
if( node.isClickable() ) {
ext.append(context.getString(R.string.value_button) );
}
} else if("android.widget.RadioButton".equals(className)) {
ext.append(context.getString(R.string.value_radio_button));
appendCheckable(context, node, ext);
} else if("android.widget.ListView".equals(className)) {
ext.append(context.getString(R.string.value_listview));
} else if("android.widget.Spinner".equals(className)) {
ext.insert(0, context.getString(R.string.value_spinner));
} else if("android.widget.EditText".equals(className)) {
appendEditable(context, node, ext);
} else if("android.widget.CheckBox".equals(className)) {
ext.append(context.getString(R.string.value_checkbox));
appendCheckable(context, node, ext);
} else if("android.widget.SeekBar".equals(className)) {
ext.insert(0, context.getString(R.string.value_seek_bar));
if(event!=null && event.getItemCount()>0) {
final int percent = (100 * event.getCurrentItemIndex()) / event.getItemCount();
ext.append( + percent + "%" );
}
} else if("android.widget.FrameLayout".equals(className)) {
} else if("android.widget.LinearLayout".equals(className)) {
} else {
}

AccessibilityNodeInfoCompat parent = node.getParent();
if( parent!=null ) {
if("android.widget.Spinner".equals(parent.getClassName())) {
ext.append(context.getString(R.string.value_spinner));
}
}

if(node.isClickable() && !node.isEnabled()) {
ext.append(context.getString(R.string.value_disabled));
}

return ext.length()>0 ? ext.toString() : null;
}

/**
 * 编辑框处理
*/
private static void appendEditable(Context context, AccessibilityNodeInfoCompat node, StringBuilder ext) {
boolean isCurrentlyEditing = node.isFocused();
        if (hasWindowSupport()) {
            isCurrentlyEditing = isCurrentlyEditing && isInputWindowOnScreen();
        }

ext.insert(0, ": ");

        if (isCurrentlyEditing) {
ext.insert(0, "," + context.getString(R.string.value_edit_box_editing) );
}

ext.insert(0, context.getString(R.string.value_edit_box));

if(node.isPassword()) {
ext.insert(0, context.getString(R.string.value_edit_box_password));
}
}

/**
 * 给字符串加上选中标记
*/
private static void appendCheckable(Context context, AccessibilityNodeInfoCompat node, StringBuilder ext) {
if(node.isCheckable()) {
ext.append(",");
if(node.isChecked()) {
ext.append(context.getString(R.string.value_checked));
} else {
ext.append(context.getString(R.string.value_not_checked));
}
}
}

    // package visibility for tests
private static boolean isInputWindowOnScreen() {
TalkBackService service = TalkBackService.getInstance();
        if (service == null) {
            return false;
        }

WindowManager windowManager = new WindowManager(service.isScreenLayoutRTL());
List<AccessibilityWindowInfo> windows = service.getWindows();
windowManager.setWindows(windows);
return windowManager.isInputWindowOnScreen();
    }

    // package visibility for tests
private static boolean hasWindowSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

/** 
 *  判断该node下是否有可以获取 ACCESSIBILITY_FOCUS 焦点的子node
 * @return int 在第几层获取到有焦点的子node
*/
public static boolean isChildAccessibilityFocusable(AccessibilityNodeInfoCompat node ) {
if( node == null ) {
return false;
}
for(int i=0; i<node.getChildCount(); i++ ) {
AccessibilityNodeInfoCompat child = node.getChild(i);
if(MyAccessibilityNodeInfoUtils.isAccessibilityFocusable( child ) ) {
return true;
} else if(node.getChildCount() > 0 ) {
if(isChildAccessibilityFocusable(child)) {
return true;
}
}
} // for
return false;
}

public static AccessibilityNodeInfoCompat findAccessibilityFocusableNode( AccessibilityNodeInfoCompat touchedNode ) {
if(touchedNode==null) {
return null;
}

if(isChildAccessibilityFocusable(touchedNode)) {
return null;
}

AccessibilityNodeInfoCompat focusable = null;
do {
if(MyAccessibilityNodeInfoUtils.isAccessibilityFocusable( touchedNode ) ) {
focusable = touchedNode;
break;
}
touchedNode = touchedNode.getParent();
} while(touchedNode!=null);

return focusable;
}

/**
 * 
  */
public static AccessibilityNodeInfoCompat getFirstChildNode(AccessibilityNodeInfoCompat node) {
if(node==null) {
return null;
}
for(int i=0; i<node.getChildCount(); i++ ) {
AccessibilityNodeInfoCompat child = node.getChild(i);

if(child.getChildCount()>0) {
AccessibilityNodeInfoCompat ccNode = MyAccessibilityNodeInfoUtils.getFirstChildNode(child);
if( ccNode != null ) {
return ccNode;
}
}

if(MyAccessibilityNodeInfoUtils.isAccessibilityFocusable( child ) ) {
return child;
}

} // for

return null;
}

/**
 * 
  */
public static AccessibilityNodeInfoCompat getLastChildNode(AccessibilityNodeInfoCompat node) {
if(node==null) {
return null;
}
for(int i=node.getChildCount()-1; i>=0 ; i-- ) {
AccessibilityNodeInfoCompat child = node.getChild(i);

if(child.getChildCount()>0) {
AccessibilityNodeInfoCompat ccNode = MyAccessibilityNodeInfoUtils.getLastChildNode(child);
if( ccNode != null ) {
return ccNode;
}
}

if(MyAccessibilityNodeInfoUtils.isAccessibilityFocusable( child ) ) {
return child;
}

} // for

return null;
}

/**
 * 
  */
public static AccessibilityNodeInfoCompat getNextNode(AccessibilityNodeInfoCompat parent,AccessibilityNodeInfoCompat current) {
if(parent==null || current == null) {
return null;
}

boolean next = false;

for(int i=0; i<parent.getChildCount(); i++ ) {
AccessibilityNodeInfoCompat child = parent.getChild(i);
if( next ) {
AccessibilityNodeInfoCompat ccNode = MyAccessibilityNodeInfoUtils.getFirstChildNode( child );
if(ccNode!=null) {
return ccNode;
} else if(MyAccessibilityNodeInfoUtils.isAccessibilityFocusable( child ) ) {
return child;
}
} else if( current.equals(child) ) {
next = true;
}
} // for

return null;
}

/**
 * 
  */
public static AccessibilityNodeInfoCompat getPreviousNode(AccessibilityNodeInfoCompat parent,AccessibilityNodeInfoCompat current) {
if(parent==null || current == null) {
return null;
}

boolean previous = false;

for(int i=parent.getChildCount()-1; i>=0 ; i-- ) {
AccessibilityNodeInfoCompat child = parent.getChild(i);
if( previous ) {
AccessibilityNodeInfoCompat ccNode = MyAccessibilityNodeInfoUtils.getLastChildNode( child );
if(ccNode!=null) {
return ccNode;
} else if(MyAccessibilityNodeInfoUtils.isAccessibilityFocusable( child ) ) {
return child;
}
} else if( current.equals(child) ) {
previous = true;
}
} // for

return null;
}

public static AccessibilityNodeInfoCompat getNextNode(AccessibilityNodeInfoCompat current) {
if(current==null) {
return null;
}

AccessibilityNodeInfoCompat nextNode = getFirstChildNode( current );

if( nextNode == null ) {
for(AccessibilityNodeInfoCompat parent = current.getParent();  parent!=null; ) {
nextNode = getNextNode( parent, current);
if(nextNode==null) {
current = parent;
parent = current.getParent();
} else {
break;
}
} // for
}

return nextNode;
}

public static AccessibilityNodeInfoCompat getPreviousNode(AccessibilityNodeInfoCompat current) {
if(current==null) {
return null;
}

AccessibilityNodeInfoCompat previousNode = getLastChildNode( current );

if( previousNode == null ) {
for(AccessibilityNodeInfoCompat parent = current.getParent();  parent!=null; ) {
previousNode = getPreviousNode( parent, current);
if(previousNode==null) {
current = parent;
parent = current.getParent();
} else {
break;
}
} // for
}

return previousNode;
}

}
