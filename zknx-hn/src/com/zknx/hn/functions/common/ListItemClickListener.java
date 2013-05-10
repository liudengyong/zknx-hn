package com.zknx.hn.functions.common;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ListItemClickListener implements OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		// FIXME 列表较多时，滚动显示有问题
		/*
		// 清除选择状态
		if (parent.getTag() != null)
            ((View)parent.getTag()).setBackgroundDrawable(null);

        // 设置选择状态
        //view.setBackgroundResource(R.drawable.home_function_btn_pressed);
        view.setBackgroundResource(R.drawable.list_item_selected);
        
		// 记录上一次设置选择状态的Item
        parent.setTag(view);
        */
	}
}
