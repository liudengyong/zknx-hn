package com.zknx.hn.functions.common;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.zknx.hn.R;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;

public class CommonList {
	
	public static class CommonListParams {

		public CommonListParams(LayoutInflater _inflater, LinearLayout _root, SimpleAdapter _adapter, OnItemClickListener _item_listener) {
			this.inflater      = _inflater;
			this.root          = _root;
			this.adapter       = _adapter;
			this.item_listener = _item_listener;
		}

		LayoutInflater inflater;
		LinearLayout root;
		SimpleAdapter adapter;
		OnItemClickListener item_listener;
	}
	
	/**
	 * 普通列表的初始化
	 *  */
	public static ListView Init(CommonListParams params, String title, LinearLayout header, View bottom) {

		RelativeLayout commonFrame = (RelativeLayout)params.inflater.inflate(R.layout.common_frame, null);

		// 视图
		TextView titleTextView = (TextView)commonFrame.findViewById(R.id.common_frame_title);
		LinearLayout headerLayout  = (LinearLayout)commonFrame.findViewById(R.id.common_frame_custom);
		LinearLayout contentLayout = (LinearLayout)commonFrame.findViewById(R.id.common_frame_content);
		LinearLayout bottomLayout  = (LinearLayout)commonFrame.findViewById(R.id.common_frame_custom_bottom);

		// 隐藏底部定制
		if (bottom == null)
			bottomLayout.setVisibility(View.GONE);
		else
			bottomLayout.addView(bottom, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));

		// 标题
		if (title != null)
			titleTextView.setText(title);
		else {
			titleTextView.setVisibility(View.GONE); // 隐藏标题
			
			// 调整左右边框的top margin
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) commonFrame.findViewById(R.id.common_frame_divider_right).getLayoutParams();
			layoutParams.setMargins(0, 0, 0, 0);
			commonFrame.findViewById(R.id.common_frame_divider_right).setLayoutParams(layoutParams);
			
			layoutParams = (RelativeLayout.LayoutParams) commonFrame.findViewById(R.id.common_frame_divider_left).getLayoutParams();
			layoutParams.setMargins(0, 0, 0, 0);
			commonFrame.findViewById(R.id.common_frame_divider_left).setLayoutParams(layoutParams);
		}

		// 定制视图（比如分类按钮）
		if (header != null)
			headerLayout.addView(header, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
		else
			headerLayout.setVisibility(View.GONE);

		// 列表
		ListView listView = new ListView(params.inflater.getContext());
		listView.setDivider(params.inflater.getContext().getResources().getDrawable(R.drawable.list_divider));
		
		// 如果为空，说明调用者可能之后会设置adapter
		if (params.adapter != null)
			listView.setAdapter(params.adapter);

		// 列表是否有监听动作
		if (params.item_listener != null)
			listView.setOnItemClickListener(params.item_listener);
		/*else
			listView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
		*/

		// FIXME 默认选择列表第一项无效
		listView.setSelection(0);
		
		contentLayout.addView(listView, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
		
		// 避免重复添加，删除所有子视图
		params.root.removeAllViews();
		params.root.addView(commonFrame);

		return listView;
	}
	
	/**
	 * 有标题，有定制头的初始化，没有定制脚
	 * */
	public static ListView Init(CommonListParams params, String title, LinearLayout header) {
		return Init(params, title, header, null);
	}
	
	/**
	 * 没有标题，有定制视图的初始化（用于添加自定义title，市场和产品价格列表）
	 * */
	public static ListView Init(CommonListParams params, LinearLayout header) {
		return Init(params, null, header, null);
	}
	
	/**
	 * 没有定制视图，有标题的初始化
	 *  */
	public static ListView Init(CommonListParams params, String title) {
		return Init(params, title, null, null);
	}
	
	/**
	 * 没有定制视图，没有标题的初始化
	 *  */
	public static ListView Init(CommonListParams params) {
		return Init(params, null, null, null);
	}
}
