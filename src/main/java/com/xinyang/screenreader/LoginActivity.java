package com.xinyang.screenreader;

import com.czy.tools.Compared;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.czyusercenter.data.User;
import com.tencent.stat.StatService;

public class LoginActivity extends Activity {
private EditText editPhone,editPassword;
private String mPassword;
private Button btnLogin;
private Button btnRegister;
private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.login_activity);

Compared compared = new Compared();
compared.getHardCode(this);

editPhone = (EditText) findViewById(R.id.phone_edit);
editPassword = (EditText) findViewById(R.id.password_edit);

btnRegister = (Button) findViewById(R.id.register_button);
btnRegister.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
Intent intent = new Intent( LoginActivity.this, RegisterActivity.class);
startActivityForResult(intent, 1);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
});

btnResetPassword = (Button) findViewById(R.id.reset_password_button);
btnResetPassword.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
Intent intent = new Intent( LoginActivity.this, ResetPasswordActivity.class);
startActivityForResult(intent, 1);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
});

btnLogin = (Button) findViewById(R.id.login_button);
btnLogin.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
String phone = editPhone.getText().toString();
String password = editPassword.getText().toString();
login(phone,password);
}
});

Button btnExit = (Button) findViewById(R.id.exit_button);
btnExit.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
Intent intent = getIntent();
Bundle bundle = new Bundle();
bundle.putBoolean("close",true);
intent.putExtras(bundle);
       setResult(RESULT_OK,intent);
finish();
}
});
    }

private void login(String phone, String password) {
if(phone.length()!=11) {
Toast.makeText(this, "请输入正确的手机号。", Toast.LENGTH_SHORT).show();				
return;
}
if(password.length()<6||phone.length()>16){
Toast.makeText(this, "请输入正确长度的密码。", Toast.LENGTH_SHORT).show();				
return;
}
btnLogin.setEnabled(false);
btnRegister.setEnabled(false);
btnResetPassword.setEnabled(false);

mPassword = getMd5(password);

String formData = "phone=" + phone + "&password=" + mPassword;
formData += "&time=";
SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
formData += myFormat.format(new Date());
formData += "&hardcode=";
Https https = new Https();
https.postText_t("", formData, new MyHttpsDelegate(), 0, "com/xinyang/screenreader/LoginActivity$MyHttpsDelegate");
}

public String getMd5(String plainText) {
try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			//32位加密
			return buf.toString();
			// 16位的加密
			//return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
return super.onKeyDown(keyCode, event);
	}

public class MyHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
if(resultCode==0){
Toast.makeText(LoginActivity.this, (String) item.get("message"), Toast.LENGTH_SHORT).show();				
btnLogin.setEnabled(true);
btnRegister.setEnabled(true);
btnResetPassword.setEnabled(true);
}else{
Toast.makeText(LoginActivity.this, "恭喜，登录成功。", Toast.LENGTH_SHORT).show();				

User user = new User();
if(user.is()) {
user.setYY(mPassword);
mPassword = null;
}

Intent intent = getIntent();
       setResult(RESULT_OK,intent);
finish();
}
}

@Override
public void onFailed_m(int errorCode) {
Toast.makeText(LoginActivity.this, "链接网络失败，请检查上网设置。", Toast.LENGTH_SHORT).show();				
btnLogin.setEnabled(true);
btnRegister.setEnabled(true);
btnResetPassword.setEnabled(true);
}	
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
super.onActivityResult(requestCode,resultCode,data);
switch( requestCode ) {
case 1: //注册返回
if( data != null ) {
Bundle bundle = data.getExtras();
if(bundle!=null) {
login(bundle.getString("phone"), bundle.getString("password"));
}
}
break;
default:
break;
}
}

@Override
protected void onResume() {
	super.onResume();
	StatService.onResume(this);
}

@Override
protected void onPause() {
	super.onPause();
	StatService.onPause(this);
}

}//end class
