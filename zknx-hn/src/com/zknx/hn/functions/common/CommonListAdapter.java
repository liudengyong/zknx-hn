package com.zknx.hn.functions.common;

import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.data.DataMan;

import android.content.Context;
import android.widget.SimpleAdapter;

public class CommonListAdapter extends SimpleAdapter {
	
	public CommonListAdapter(Context context, List<ListItemMap> data) {
		super(context, data, R.layout.common_list_item, new String[] {DataMan.KEY_NAME}, new int[] {R.id.list_item_text});
	}
	
	public CommonListAdapter(Context context, List<ListItemMap> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}
	
	@Override
	public ListItemMap getItem(int position) {
		if (getCount() > position)
			return (ListItemMap)super.getItem(position);
		else
			return null;
	}

	/**
	 * 获取item中的map的字符串
	 * @param position
	 * @param key
	 * @return
	 */
	public String getItemMapString(int position, String key) {
		ListItemMap map = getItem(position);
		
		return (map != null) ? map.getString(key) : null;
	}

	/**
	 * 获取item中的map的整型值
	 * @param position
	 * @param key
	 * @return
	 */
	public int getItemMapInt(int position, String key) {
		ListItemMap map = getItem(position);
		
		return (map != null) ? map.getInt(key) : DataMan.INVALID_ID;
	}
}
