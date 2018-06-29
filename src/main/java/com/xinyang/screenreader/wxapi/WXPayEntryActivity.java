package com.xinyang.screenreader.wxapi;

import android.widget.Toast;
import com.xinyang.screenreader.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.xinyang.screenreader.SettingsFragment;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;
	private static final String WX_APP_ID = "wx4d02492ca10c42b9";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_pay_result);

    	api = WXAPIFactory.createWXAPI(this,WX_APP_ID);
		api.registerApp(WX_APP_ID);
api.handleIntent(getIntent(), this);
   }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {

		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
SettingsFragment.updatedVipInfo = true;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			result = R.string.errcode_unsupported;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
finish();
	}

}
