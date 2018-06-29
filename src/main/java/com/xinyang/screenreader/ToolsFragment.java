package com.xinyang.screenreader;

import android.widget.Toast;
import com.czy.tools.Compared;
import java.util.HashMap;
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
import com.msm.NimbleMenu;
import com.google.android.marvin.talkback8.TalkBackService;

public class ToolsFragment extends Fragment {
private List<Map<String,Object> > items;
private SimpleAdapter adapter;

	public ToolsFragment() {
		// TODO Auto-generated constructor stub
	}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
View v = inflater.inflate(R.layout.tools_fragment, container, false);
		// TODO Auto-generated constructor stub

items = new ArrayList<Map<String,Object> >();
Map<String, Object> item = new HashMap<String, Object>();
item.put("name", getString(R.string.list_item_text_notepad));
item.put("version", "VIP");
item.put("id", 1);
items.add(item);
//item = new HashMap<String, Object>();
//item.put("name", getString(R.string.list_item_text_count));
//item.put("version", "");
//item.put("id", 2);
//items.add(item);
item = new HashMap<String, Object>();
item.put("name", getString(R.string.list_item_text_pop_menu));
item.put("version", "");
item.put("id", 3);
items.add(item);
ListView listView =  (ListView) v.findViewById( R.id.listView);
adapter = new SimpleAdapter(getActivity(), items, R.layout.tools_list_item, new String[]{"name","version"}, new int[]{R.id.text_view_name,R.id.text_view_version});
listView.setAdapter(adapter);

listView.setOnItemClickListener( new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> ada, View v, int position, long id) {
if(position>=0 && position < items.size()) {
Map<String,Object> item = items.get(position);
if(item!=null) {
switch((Integer)item.get("id")) {
case 1: // notepad
Compared compared = new Compared();
if( compared.checkType(getActivity(),true) > 0 ) {
Intent intent = new Intent( getActivity(), NotepadListActivity.class);
startActivityForResult(intent, 1);
//getActivity().overridePendingTransition(R.anim.in_zoom,R.anim.out_zoom);
} else {
Toast.makeText(getActivity(), R.string.toast_check_vip_notepad, Toast.LENGTH_SHORT).show();
}
break;
case 2: // count
break;
case 3: // pop menu
NimbleMenu.showPopupWindow();
break;
default:
break;
}//switch
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

@Override
public void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
}

}//end class
