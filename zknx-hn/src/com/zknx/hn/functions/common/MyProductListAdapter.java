package com.zknx.hn.functions.common;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.zknx.hn.R;
import com.zknx.hn.data.ListItemMap;
import com.zknx.hn.data.DataMan;

public class MyProductListAdapter extends CommonListAdapter {
	
	private OnClickListener mClickRemove;
	
	public MyProductListAdapter(Context context, List<ListItemMap> list, OnClickListener clickRemove) {
		super(context, list, R.layout.my_product_list_item, 
				new String[] {DataMan.KEY_NAME}, 
				new int[] {	R.id.my_product_list_item_product, // ��ѡ��Ʒ����
				});
		
		mClickRemove = clickRemove;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		// ֻ���һ��
		if (convertView == null) {
			String product_id = getItemMapString(position, DataMan.KEY_PRODUCT_ID);
			
			View checkMyProduct = view.findViewById(R.id.my_product_list_item_cancel);
			
			// ��ť �����Ʒid
			checkMyProduct.setTag(product_id);
			checkMyProduct.setOnClickListener(mClickRemove);
			checkMyProduct.setBackgroundResource(R.drawable.button_remove);
		}

		return view;
	}
}