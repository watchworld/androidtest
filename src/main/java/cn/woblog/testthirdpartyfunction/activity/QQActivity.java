package cn.woblog.testthirdpartyfunction.activity;

import java.util.ArrayList;
import com.tencent.connect.share.QzoneShare;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import com.xinyang.screenreader.R;
import cn.woblog.testthirdpartyfunction.Util;

public class QQActivity {

    public static Tencent mTencent;

    private static final String mAppid = "1105895679";

public static void shareOnlyImageOnQZone(Activity context, String url) {
        final Bundle params = new Bundle();
params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享有礼送积分");
params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "我不只是一个读屏");
params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        //本地地址一定要传sdcard路径，不要什么getCacheDir()或getFilesDir()
//params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/a.png"));
params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN); //打开这句话，可以实现分享纯图到QQ空间
doShareToQQ(context, params);
    }

public static void shareOnlyImageOnQQ(Activity context, String url) {
        final Bundle params = new Bundle();
  //分享类型
params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);//（图文）  
    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "分享有礼送积分");//必填
params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "我不只是一个读屏");//选填
params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url); //必填
params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>());//wiki上写着选填，但不填会出错  
        doShareToQQ(context, params);
    }

public static void doShareToQQ(final Activity context, final Bundle params) {
        // QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, context);
        }
                if (null != mTencent) {
mTencent.shareToQQ(context, params, new IUiListener() {
        @Override
        public void onCancel() {
//Util.toastMessage(QQActivity.this, "onCancel: ");
        }
        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
//            Util.toastMessage(QQActivity.this, "onComplete: " + response.toString());
        }
        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
//            Util.toastMessage(QQActivity.this, "onError: " + e.errorMessage, "e");
        }
 });
                }
            }
        });
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
    }
*/
}
