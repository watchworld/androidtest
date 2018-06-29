package cn.woblog.testthirdpartyfunction.activity;

import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import cn.woblog.testthirdpartyfunction.Constants;
import com.xinyang.screenreader.R;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;

public class SinaActivity implements IWeiboHandler.Response {

    private IWeiboShareAPI mWeiboShareAPI = null;

public void onCreate(Activity context,  Bundle  savedInstanceState) {
mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, Constants.APP_KEY);
                mWeiboShareAPI.registerApp();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(context.getIntent(), this);
        }
}

public  void testShareImage(Activity context, String url) {
WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            weiboMessage.mediaObject = getWebpageObj(context, url);
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
            mWeiboShareAPI.sendRequest(context, request);
    }

private WebpageObject getWebpageObj(Activity context, String url) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "分享有礼送积分";
        mediaObject.description = "我不只是一个读屏";

        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = url;
        mediaObject.defaultText = "WebPage default text";
        return mediaObject;
    }

public void onNowIntent( Intent intent) {
if(mWeiboShareAPI!=null&&intent!=null) {
mWeiboShareAPI.handleWeiboResponse(intent, this);
}
}

    @Override
    public void onResponse(BaseResponse baseResp) {
        if(baseResp!= null){
            switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
//                Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
//                Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
//                Toast.makeText(this, 
//                        getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
//                        Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}
