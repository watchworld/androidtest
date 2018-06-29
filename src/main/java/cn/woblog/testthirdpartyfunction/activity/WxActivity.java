package cn.woblog.testthirdpartyfunction.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;

import cn.woblog.testthirdpartyfunction.Constants;
import com.xinyang.screenreader.R;

public class WxActivity {

public static IWXAPI api = null;

   /**
     * @param isShareFriend true 分享到朋友，false分享到朋友圈
     */
public  static void share2Wx(Context context, boolean isShareFriend, String url) {
if(api == null) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, true);
        // 将该app注册到微信
        api.registerApp(Constants.WX_APP_ID);
}
WXWebpageObject webpage = new WXWebpageObject();
webpage.webpageUrl = url;
WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = webpage;
msg.title = "分享有礼送积分";
msg.description = "我不只是一个读屏";
        Bitmap bitmap = BitmapFactory.decodeResource( context.getResources(), R.mipmap.ic_launcher);
Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true);//缩略图大小
bitmap.recycle();
msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图

SendMessageToWX.Req req = new SendMessageToWX.Req();
req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = isShareFriend ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
