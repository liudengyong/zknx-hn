package com.zkxc.android.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkxc.android.R;
import com.zkxc.android.data.RecordMan;

public class CmnListAdapter extends BaseAdapter {
	
	int mSelected = -1;
	private Context mContext;

	private Vector<ListItemName> mItems = new Vector<ListItemName>();

	public CmnListAdapter(Context context) {
		mContext = context;
	}

	public void sortByName(boolean nagtive) {
		Comparator<ListItemName> ct = new AbcComparator(nagtive);
		Collections.sort(mItems, ct);
	}

	public void addItem(ListItemName it) {
		mItems.add(it);
	}
	
	public void addItem(Map<String, Object> record) {
		Object recordTime = record.get(RecordMan.RECORD_TIME);
		
		if (recordTime == null)
			return;
		
    	String dateHuman = Converter.DateToHuman(recordTime.toString());
    	
    	Debug.Log("addItem, record = " + record.get(RecordMan.PENDING).toString());
    	
		// 保存日期，用于计算修改时保存文件名
    	if (record.get(RecordMan.PENDING).toString() == "1")
    	{
    		dateHuman = dateHuman + "（未同步）";
    	}
    	else
    	{
    		if (record.get(RecordMan.RECORD_USER) != null)
    			dateHuman = dateHuman + "（" + record.get(RecordMan.RECORD_USER).toString() + "）";
    	}

		ListItemName it = new ListItemName(dateHuman, null, record);
		mItems.add(it);
	}

	@Override
	public ListItemName getItem(int pos) {
		if (pos >= getCount() || pos < 0) return null;
		
		return mItems.elementAt(pos);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public ListItemName getSelectedTableItem() {
		if (mSelected == -1 || mSelected >= mItems.size())
			return null;
		
		return mItems.elementAt(mSelected);
	}
	
	public int getItemSelectedIndex()
	{
		return mSelected;
	}
	
	public void setSelectedPosition(int position) {
		mSelected = position;
		notifyDataSetInvalidated();
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {

		LayoutInflater inflate = (LayoutInflater)mContext.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);

		if (view == null)
			view = (LinearLayout)inflate.inflate(R.layout.cmn_list_item, null);
		
		TextView name = (TextView)view.findViewById(R.id.tab_list_item_name);

		name.setText(getItem(position).name);
		
		if (mSelected == position)
		{
			//view.setBackgroundColor(Color.BLUE);
			view.setBackgroundColor(Color.rgb(255, 144, 0)); // #FF9000
			//view.setBackgroundResource(R.drawable.highlight_selected);
		}
		else
			view.setBackgroundColor(Color.TRANSPARENT);

		return view;
	}

	public static class ListItemName 
	{
		public ListItemName(String _name, String _id, Object _tag)
		{
			name = _name;
			id = _id;
			tag = _tag;
		}
		
		public Object getTag()
		{
			return tag;
		}
		
		public String name;
		public String id;
		
		Object tag;
	}

	private class AbcComparator implements Comparator<ListItemName> {

		boolean mIsNagtive;
		
		public AbcComparator(boolean nagtive) {
			mIsNagtive = nagtive;
		}

		@Override
		public int compare(ListItemName obj1, ListItemName obj2) {
			String e1 = obj1.name;
			String e2 = obj2.name;
			
			if (mIsNagtive)
				return e1.compareTo(e2);
			else
				return e2.compareTo(e1);
		}
	}
}