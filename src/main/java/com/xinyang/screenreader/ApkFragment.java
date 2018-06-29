package com.xinyang.screenreader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.czy.manager.Https;
import net.czy.manager.HttpsDelegate;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ApkFragment extends Fragment {
private List<Map<String,Object> > items;
private SimpleAdapter adapter;

	public ApkFragment() {
		// TODO Auto-generated constructor stub
	}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

View v = inflater.inflate(R.layout.apk_fragment, container, false);
		// TODO Auto-generated constructor stub

items = new ArrayList<Map<String,Object> >();

ListView listView =  (ListView) v.findViewById( R.id.listView);
adapter = new SimpleAdapter(getActivity(), items, R.layout.apk_list_item, new String[]{"name","version","developer"}, new int[]{R.id.text_view_name,R.id.text_view_version,R.id.text_view_developer});
listView.setAdapter(adapter);

listView.setOnItemClickListener( new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> ada, View v, int position, long id) {
if(position>=0 && position < items.size()) {
Map<String,Object> item = items.get(position);
if(item!=null) {
Intent intent = new Intent( getActivity(), ApkActivity.class);
Bundle bundle = new Bundle();
for(Map.Entry<String, Object> entry : item.entrySet()) {
bundle.putString( entry.getKey(), (String)entry.getValue() );
}
intent.putExtras(bundle);
startActivityForResult(intent, 2);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
}
}
}
});

return v;
}

@Override
public void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
}

public class ApkHttpsDelegate extends HttpsDelegate {
@Override
public void callbackFunction_m(int how, int resultCode, Map<String,Object> item) {

}
@Override
public void callbackFunctionList_m(int how, int resultCode, List<Map<String,Object>> l) {
if(resultCode!=0){
items.clear();
for(Map<String,Object> m : l) {
items.add(m);
}//for
adapter.notifyDataSetChanged();
}else{
}
}
@Override
public void onFailed_m(int errorCode) {
}	
}

@Override
public void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
Https https = new Https();
https.getText_t("", new ApkHttpsDelegate(), 6, "com/xinyang/screenreader/ApkFragment$ApkHttpsDelegate");
}

}//end class
