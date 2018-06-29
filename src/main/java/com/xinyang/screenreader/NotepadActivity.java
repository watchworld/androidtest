package com.xinyang.screenreader;

import android.view.KeyEvent;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import java.io.File;
import android.os.Environment;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import android.widget.EditText;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.czy.manager.InstallApk;

public class NotepadActivity extends Activity {
private String mFilePath = null;
private EditText editTitle;
private EditText editArea ;
private Button btnSaveFile;
private boolean bChanged = false; // checked edittext changed

private TextWatcher textWatcher = new TextWatcher() {
@Override
public void onTextChanged(CharSequence s, int start, int before,
int count) {
}
@Override
public void beforeTextChanged(CharSequence s, int start, int count,
int after) {
}
@Override
public void afterTextChanged(Editable s) {
bChanged = true;
btnSaveFile.setEnabled(true);
}
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notepad_activity);

editTitle = (EditText) findViewById(R.id.edit_text_title);
editArea = (EditText) findViewById(R.id.edit_text_area);

Button btnBack = (Button) findViewById(R.id.back_button);
btnBack.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
if(!closeActivity()) {
finish();
}
}
});

btnSaveFile = (Button) findViewById(R.id.btn_save_file);
btnSaveFile.setEnabled(false);
btnSaveFile.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
clickedSaveFile(false);
}
});

Button btnSaveFileA = (Button) findViewById(R.id.btn_save_file_a);
btnSaveFileA.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {

}
});

Intent intent = getIntent();
if(intent!=null) {
Bundle bundle = intent.getExtras();
if(bundle!=null) {
String title = bundle.getString("filename");
if(title.length()>4 ) {
title = title.substring(0, title.length()-4);
}
editTitle.setText(title);
editTitle.setCursorVisible(false);
editTitle.setFocusable(false);
editTitle.setFocusableInTouchMode(false);
editTitle.setKeyListener(null);
mFilePath = bundle.getString("filepath");
        try {
editArea.setText(read(mFilePath));
        } catch (Exception e) {
Toast.makeText(getApplicationContext(), R.string.toast_open_file_error, Toast.LENGTH_SHORT).show();
        }
}
}


editArea.addTextChangedListener( textWatcher);

} // onCreate

@Override
protected void onResume() {
	super.onResume();

}

/**
 * save file
 * 
 * @param filenameStr
 *            file name
 * @param filecontentStr
 *            file contents
 * @throws Exception
 */
public static void save(String filenameStr, String filecontentStr)
        throws Exception {
File file = new File(filenameStr);
    FileOutputStream fos = new FileOutputStream(file, false);
    fos.write(filecontentStr.getBytes());
    fos.close();
}

/**
 * read file
 * 
 * @param filenameStr
 *            file path
 * @return file contents
 * @throws Exception
 */
public static String read(String filenameStr) throws Exception {

    File file = new File(filenameStr);
    FileInputStream fis = new FileInputStream(file);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = 0;
    while ((len = fis.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
    }
    byte[] data = baos.toByteArray();
    return new String(data);
}

private void saveFile(String filepath,String contents) {
        try {
save(filepath, contents);
editTitle.setCursorVisible(false);
editTitle.setFocusable(false);
editTitle.setFocusableInTouchMode(false);
editTitle.setKeyListener(null);
mFilePath = filepath;
bChanged = false;
btnSaveFile.setEnabled(false);
            Toast.makeText(getApplicationContext(), R.string.toast_save_file_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
Toast.makeText(getApplicationContext(), R.string.toast_save_file_error, Toast.LENGTH_SHORT).show();
        }
}

public void clickedSaveFile( final boolean closeNow) {
if(mFilePath == null ) {
String path = Environment.getExternalStorageDirectory() + "/notepad";
File specItemDir = new File( path );
if(!specItemDir.exists()){
	specItemDir.mkdir();
}
if(specItemDir.exists()){
String title = editTitle.getText().toString();
if(title.isEmpty()) {
Toast.makeText(NotepadActivity.this, R.string.toast_file_name_not_empty, Toast.LENGTH_SHORT).show();  
} else {
final String filepath = path + "/" + title + ".txt";
if(title.equals(getString(R.string.edit_text_title)) ) {
for(int i=1;i<1000000;i++) {
String fp =  path + "/" + title + "(" + i + ").txt";
specItemDir = new File( fp );
if(specItemDir.exists()){
continue;
} else {
editTitle.setText(title + "(" + i + ")");
saveFile(fp, editArea.getText().toString());
if(closeNow) {
finish();
}
break;
}
} // while
} else {
specItemDir = new File( filepath );
if(specItemDir.exists()){
AlertDialog.Builder builder = new AlertDialog.Builder(NotepadActivity.this);
builder.setTitle(getString(R.string.dialog_title_ask));
builder.setMessage(getString(R.string.dialog_message_save_file));
builder.setPositiveButton(getString(R.string.dialog_button_save_file_yes),
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
saveFile(filepath, editArea.getText().toString());
if(closeNow) {
finish();
}
}
});
builder.setNegativeButton(getString(R.string.cancel), null);
builder.show();
} else {
saveFile(filepath, editArea.getText().toString());
if(closeNow) {
finish();
}
}
}
}
}
} else {
// mFilePath exist
 saveFile(mFilePath, editArea.getText().toString());
if(closeNow) {
finish();
}
}
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
if(keyCode==KeyEvent.KEYCODE_BACK) {
if(closeActivity()) {
return true;
}
}
return super.onKeyDown(keyCode, event);
	}

private boolean closeActivity() {
if(bChanged) {
String contents = editArea.getText().toString();
if( contents.length()!=0 || mFilePath!=null) {
AlertDialog.Builder builder = new AlertDialog.Builder(NotepadActivity.this);
builder.setTitle(getString(R.string.dialog_title_ask));
builder.setMessage(getString(R.string.dialog_message_save_file_with_exit));
builder.setPositiveButton(getString(R.string.dialog_button_save_file_yes_2),
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
clickedSaveFile(true);
}
});
builder.setNegativeButton(getString(R.string.dialog_button_not_save_file), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
finish();
}
});
builder.setNeutralButton(getString(R.string.cancel), null);
builder.show();
return true;
}
}
return false;
}

}// end class
