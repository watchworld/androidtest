package com.xinyang.screenreader;

import android.widget.EditText;
import java.math.BigDecimal;
import java.math.RoundingMode;
import android.widget.Toast;
import com.czy.virtual.VirtualScreenActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import java.io.File;
import android.os.Environment;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

public class NotepadListActivity extends Activity {
private List<Map<String,Object> > items;
private SimpleAdapter adapter;
private String url;
private String packageName;
private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notepad_list_activity);

items = new ArrayList<Map<String,Object> >();
ListView listView =  (ListView) findViewById( R.id.listView);
adapter = new SimpleAdapter(this, items, R.layout.notepad_info_list_item, new String[]{"name","size"}, new int[]{R.id.text_view_name,R.id.text_view_size});
listView.setAdapter(adapter);

listView.setOnItemClickListener( new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> ada, View v,final int position, long id) {
if(position>=0 && position<items.size()) {
final Map<String,Object> item = items.get(position);
if(item!=null) {
AlertDialog.Builder builder = new AlertDialog.Builder(NotepadListActivity.this);
builder.setItems(new String[]{getString(R.string.dialog_item_notepad_look), getString(R.string.dialog_item_notepad_edit), getString(R.string.dialog_item_notepad_rename), getString(R.string.dialog_item_notepad_delete), getString(R.string.dialog_item_notepad_shared)},
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
switch(which) {
case 0: { //look
        try {
String text = NotepadActivity.read((String) item.get("path"));
if(text.isEmpty()) {
Toast.makeText(getApplicationContext(), R.string.toast_file_empty, Toast.LENGTH_SHORT).show();
} else {
Intent vsActivity = new Intent( NotepadListActivity.this, VirtualScreenActivity.class);
            vsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
String[] arr = text.split("\n");
ArrayList<String> ls = new ArrayList<String>();
for(String s : arr) {
ls.add(s);
}
vsActivity.putStringArrayListExtra("all", ls);
startActivity(vsActivity);
}
        } catch (Exception e) {
Toast.makeText(getApplicationContext(), R.string.toast_open_file_error, Toast.LENGTH_SHORT).show();
        }
}
break;
case 1: { // edit
Intent intent = new Intent( NotepadListActivity.this, NotepadActivity.class);
Bundle bundle = new Bundle();
bundle.putString("filename", (String) item.get("name"));
bundle.putString("filepath", (String) item.get("path"));
intent.putExtras(bundle);
startActivityForResult(intent, 2);
//overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
break;
case 2:{ // rename
final EditText editLine = new EditText(NotepadListActivity.this);
String title = (String) item.get("name");
if(title.length()>4 ) {
title = title.substring(0, title.length()-4);
}
editLine.setText(title);
new AlertDialog.Builder( NotepadListActivity.this)
.setTitle(getString(R.string.dialog_title_notepad_rename))
.setView(editLine)
.setPositiveButton(getString(R.string.dialog_button_rename_file_yes),
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
if(items!=null && item!=null) {
final String filename = editLine.getText().toString();
if(filename.isEmpty()) {
Toast.makeText(NotepadListActivity.this, R.string.toast_file_name_not_empty, Toast.LENGTH_SHORT).show();  
} else {
final File f = new File( (String) item.get("path") );
if(f.exists()){
String path = Environment.getExternalStorageDirectory() + "/notepad/";
final File nf = new File(path + filename + ".txt");
if(nf.exists()){
new AlertDialog.Builder( NotepadListActivity.this)
.setTitle(getString(R.string.dialog_title_ask))
.setMessage(getString(R.string.dialog_message_save_file))
.setPositiveButton(getString(R.string.dialog_button_save_file_yes),
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
if(f.renameTo(nf)) {
String fname = filename + ".txt";
for(int i=0;i<items.size();i++) {
Map<String,Object> m = items.get(i);
if(fname.equals((String)m.get("name"))) {
items.remove(i);
break;
}
}
item.put("name", fname);
item.put("path", f.getPath());
adapter.notifyDataSetChanged();
} else {
Toast.makeText(NotepadListActivity.this, R.string.toast_file_rename_failed, Toast.LENGTH_SHORT).show();
}
}
})
.setNegativeButton(getString(R.string.cancel), null)
.show();
} else {
if(f.renameTo(nf)) {
item.put("name", filename + ".txt");
item.put("path", f.getPath());
adapter.notifyDataSetChanged();
} else {
Toast.makeText(NotepadListActivity.this, R.string.toast_file_rename_failed, Toast.LENGTH_SHORT).show();
}
}
}
}
}
}
})
.setNegativeButton(getString(R.string.cancel), null)
.show();
}
break;
case 3: //delete
new AlertDialog.Builder( NotepadListActivity.this)
.setTitle(getString(R.string.dialog_title_ask))
.setMessage(getString(R.string.dialog_message_delete_file_ask))
.setPositiveButton(getString(R.string.dialog_button_delete_file_yes),
new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
if(items!=null && item!=null) {
File f = new File( (String) item.get("path") );
if(f.exists()){
f.delete();
}
items.remove(position);
adapter.notifyDataSetChanged();
}
}
})
.setNegativeButton(getString(R.string.cancel), null)
.show();
break;
}
}
});
builder.setNegativeButton(getString(R.string.cancel), null);
builder.show();
}
}
}
});

Button btnBack = (Button) findViewById(R.id.back_button);
btnBack.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
finish();
}
});

Button btnNewFile = (Button) findViewById(R.id.btn_new_file);
btnNewFile.setOnClickListener(new OnClickListener(){
@Override
public void onClick(View v) {
Intent intent = new Intent( NotepadListActivity.this, NotepadActivity.class);
startActivityForResult(intent, 1);
//overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
});

}

private void initialListFiles() {
if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
if( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
 }  else{
reloadFolder();
}
}else{
reloadFolder();
}
}

@Override
protected void onStart() {
super.onStart();
initialListFiles();
}

@Override
protected void onResume() {
	super.onResume();
}

/*
*
*/
private void reloadFolder() {
if(items!=null) {
items.clear();
String path = Environment.getExternalStorageDirectory() + "/notepad";
File specItemDir = new File( path );
if(!specItemDir.exists()){
	specItemDir.mkdir();
}
if(!specItemDir.exists()){
} else {
final File[] files = specItemDir.listFiles();
if(files!=null) {
for(File f : files) {
Map<String,Object> item = new HashMap<String,Object>();
String strSize = "";
long size = f.length();
if(size >= 1024*1024) {
BigDecimal bg = new BigDecimal((Double.valueOf(size)/(1024*1024))).setScale(2, RoundingMode.UP);
strSize = bg.doubleValue() + "MB";
} else if(size >= 1024 ) {
BigDecimal bg = new BigDecimal((Double.valueOf(size)/1024)).setScale(2, RoundingMode.UP);
strSize = bg.doubleValue() + "KB";
} else {
strSize = size + getString(R.string.file_b);
}
item.put("name", f.getName());
item.put("path", f.getPath());
item.put("size", strSize);
items.add(item);
} // for
adapter.notifyDataSetChanged();
}
}
}
}

}// end class
