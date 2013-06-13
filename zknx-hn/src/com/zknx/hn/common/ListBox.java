package com.zknx.hn.common;

import java.util.List;

import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.ListItemMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

@SuppressLint("ViewConstructor")
public class ListBox extends Spinner {
	
	ArrayAdapter<String> mAdapter;

	public ListBox(Context context, List<ListItemMap> list) {
		super(context);
		
		init(list);
	}
	
	/**
	 * 初始化
	 * @param list
	 */
	private void init(List<ListItemMap> list) {
		if (list == null || list.size() == 0) {
			Debug.Log("ListBox初始化错误");
			return;
		}

		//将可选内容与ArrayAdapter连接起来
		String[] array = new String[list.size()];
		
		int i = 0;
		for (ListItemMap map : list) {
			array[i++] = new String(map.getString(DataMan.KEY_NAME));
		}
		
		mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, array);
		//设置下拉列表的风格  
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		//将adapter 添加到spinner中
        setAdapter(mAdapter);
	}
}
