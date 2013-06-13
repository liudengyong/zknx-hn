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
	 * ��ʼ��
	 * @param list
	 */
	private void init(List<ListItemMap> list) {
		if (list == null || list.size() == 0) {
			Debug.Log("ListBox��ʼ������");
			return;
		}

		//����ѡ������ArrayAdapter��������
		String[] array = new String[list.size()];
		
		int i = 0;
		for (ListItemMap map : list) {
			array[i++] = new String(map.getString(DataMan.KEY_NAME));
		}
		
		mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, array);
		//���������б�ķ��  
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		//��adapter ��ӵ�spinner��
        setAdapter(mAdapter);
	}
}
