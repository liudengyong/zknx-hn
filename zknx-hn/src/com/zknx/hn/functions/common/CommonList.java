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
	 * ��ͨ�б�ĳ�ʼ��
	 *  */
	public static ListView Init(CommonListParams params, String title, LinearLayout header, View bottom) {

		RelativeLayout commonFrame = (RelativeLayout)params.inflater.inflate(R.layout.common_frame, null);

		// ��ͼ
		TextView titleTextView = (TextView)commonFrame.findViewById(R.id.common_frame_title);
		LinearLayout headerLayout  = (LinearLayout)commonFrame.findViewById(R.id.common_frame_custom);
		LinearLayout contentLayout = (LinearLayout)commonFrame.findViewById(R.id.common_frame_content);
		LinearLayout bottomLayout  = (LinearLayout)commonFrame.findViewById(R.id.common_frame_custom_bottom);

		// ���صײ�����
		if (bottom == null)
			bottomLayout.setVisibility(View.GONE);
		else
			bottomLayout.addView(bottom, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));

		// ����
		if (title != null)
			titleTextView.setText(title);
		else {
			titleTextView.setVisibility(View.GONE); // ���ر���
			
			// �������ұ߿��top margin
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) commonFrame.findViewById(R.id.common_frame_divider_right).getLayoutParams();
			layoutParams.setMargins(0, 0, 0, 0);
			commonFrame.findViewById(R.id.common_frame_divider_right).setLayoutParams(layoutParams);
			
			layoutParams = (RelativeLayout.LayoutParams) commonFrame.findViewById(R.id.common_frame_divider_left).getLayoutParams();
			layoutParams.setMargins(0, 0, 0, 0);
			commonFrame.findViewById(R.id.common_frame_divider_left).setLayoutParams(layoutParams);
		}

		// ������ͼ��������ఴť��
		if (header != null)
			headerLayout.addView(header, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
		else
			headerLayout.setVisibility(View.GONE);

		// �б�
		ListView listView = new ListView(params.inflater.getContext());
		listView.setDivider(params.inflater.getContext().getResources().getDrawable(R.drawable.list_divider));
		
		// ���Ϊ�գ�˵�������߿���֮�������adapter
		if (params.adapter != null)
			listView.setAdapter(params.adapter);

		// �б��Ƿ��м�������
		if (params.item_listener != null)
			listView.setOnItemClickListener(params.item_listener);
		/*else
			listView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
		*/

		// FIXME Ĭ��ѡ���б��һ����Ч
		listView.setSelection(0);
		
		contentLayout.addView(listView, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
		
		// �����ظ���ӣ�ɾ����������ͼ
		params.root.removeAllViews();
		params.root.addView(commonFrame);

		return listView;
	}
	
	/**
	 * �б��⣬�ж���ͷ�ĳ�ʼ����û�ж��ƽ�
	 * */
	public static ListView Init(CommonListParams params, String title, LinearLayout header) {
		return Init(params, title, header, null);
	}
	
	/**
	 * û�б��⣬�ж�����ͼ�ĳ�ʼ������������Զ���title���г��Ͳ�Ʒ�۸��б�
	 * */
	public static ListView Init(CommonListParams params, LinearLayout header) {
		return Init(params, null, header, null);
	}
	
	/**
	 * û�ж�����ͼ���б���ĳ�ʼ��
	 *  */
	public static ListView Init(CommonListParams params, String title) {
		return Init(params, title, null, null);
	}
	
	/**
	 * û�ж�����ͼ��û�б���ĳ�ʼ��
	 *  */
	public static ListView Init(CommonListParams params) {
		return Init(params, null, null, null);
	}
}
