package com.xinyang.screenreader;

import java.util.Map;

import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.tencent.stat.StatService;

public class ResetPasswordActivity extends Activity {
private EditText editPassword, editPasswordRepeat, editPhone, editCode;
private Button btnGainCode;
private Button btnResetPassword;
private static int span;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.reset_password_activity);

editPhone = (EditText) findViewById(R.id.phone_edit);
editCode = (EditText) findViewById(R.id.code_edit);
editPassword = (EditText) findViewById(R.id.password_edit);
editPasswordRepeat = (EditText) findViewById(R.id.password_repeat_edit);

Button btnBack = (Button) findViewById(R.id.back_button);
btnBack.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
finish();
}
});

btnGainCode = (Button) findViewById(R.id.gain_code_button);
btnGainCode.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
String phone = editPhone.getText().toString();
if(phone.length()!=11){
Toast.makeText(ResetPasswordActivity.this, "请输入正确的手机号。", Toast.LENGTH_SHORT).show();				
return;
}
btnGainCode.setEnabled(false);

String formData = "phone=" + phone;
Https https = new Https();
https.getText_t(formData, new GainCodeHttpsDelegate(), 3, "com/xinyang/screenreader/ResetPasswordActivity$GainCodeHttpsDelegate");
}
});

btnResetPassword = (Button) findViewById(R.id.reset_password_button);
btnResetPassword.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
String phone = editPhone.getText().toString();
String code = editCode.getText().toString();
String password = editPassword.getText().toString();
String passwordRepeat = editPasswordRepeat.getText().toString();
if(phone.length()!=11){
Toast.makeText(ResetPasswordActivity.this, "请输入正确的手机号。", Toast.LENGTH_SHORT).show();				
return;
}
if(code.length()!=6){
Toast.makeText(ResetPasswordActivity.this, "请输入正确的手机验证码。。", Toast.LENGTH_SHORT).show();				
return;
}
if(password.length()<6||password.length()>16){
Toast.makeText(ResetPasswordActivity.this, "请输入6置16位的密码。", Toast.LENGTH_SHORT).show();				
return;
}
if(!password.equals(passwordRepeat)){
Toast.makeText(ResetPasswordActivity.this, "两次输入的密码不一致。", Toast.LENGTH_SHORT).show();				
return;
}

btnResetPassword.setEnabled(false);

String formData = "phone=" + phone + "&code=" + code + "&password=" + password;
Https https = new Https();
https.getText_t(formData, new ResetPasswordHttpsDelegate(), 4, "com/xinyang/screenreader/ResetPasswordActivity$ResetPasswordHttpsDelegate");
}
});

if(span>0) {
btnGainCode.setEnabled(false);
new Handler().postDelayed(mySpanRunnable, 1000);
}
} // end onCreate

private Runnable mySpanRunnable = new Runnable() {
@Override
public void run() {
span--;
if(span<=0) {
btnGainCode.setText("发送手机验证码");
btnGainCode.setEnabled(true);
}else{
new Handler().postDelayed(mySpanRunnable,1000);
btnGainCode.setText("还剩(" + span + ")秒后能重发。");
}
}
};

public class GainCodeHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
if(resultCode==0) {
Toast.makeText(ResetPasswordActivity.this, (String) item.get("message"), Toast.LENGTH_SHORT).show();				
btnGainCode.setEnabled(true);
}else{
Toast.makeText(ResetPasswordActivity.this, "恭喜！手机验证码短信已成功发送到你的手机。", Toast.LENGTH_SHORT).show();				
span = 121;
new Handler().postDelayed(mySpanRunnable, 1000);
}
}
@Override
public void onFailed_m(int errorCode) {
Toast.makeText(ResetPasswordActivity.this, "链接网络失败，请检查上网设置。", Toast.LENGTH_SHORT).show();				
btnGainCode.setEnabled(true);
}	
}

public class ResetPasswordHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {
if(resultCode==0) {
Toast.makeText(ResetPasswordActivity.this, (String) item.get("message"), Toast.LENGTH_SHORT).show();				
btnResetPassword.setEnabled(true);
}else{
Toast.makeText(ResetPasswordActivity.this, "恭喜！注册成功！", Toast.LENGTH_SHORT).show();				
Intent intent = getIntent();
       Bundle bundle = new Bundle();
bundle.putString("phone",editPhone.getText().toString());
bundle.putString("password", editPassword.getText().toString());
intent.putExtras(bundle);
       setResult(RESULT_OK,intent);
finish();
}
}
@Override
public void onFailed_m(int errorCode) {
Toast.makeText(ResetPasswordActivity.this, "链接网络失败，请检查上网设置。", Toast.LENGTH_SHORT).show();				
btnResetPassword.setEnabled(true);
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

}// end class
