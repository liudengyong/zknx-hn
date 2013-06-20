package com.zknx.hn.functions.common;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FunctionView {
	
	public Context mContext;
	public LayoutInflater mInflater;
	public LinearLayout[] mContentFrame;
	
	public FunctionView(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		mInflater     = inflater;
		
		mContext = mInflater.getContext();
		
		initFrame(frameRoot, frameResId);
	}
	
	void initFrame(LinearLayout frameRoot, int frameResId) {

		if (frameResId == 0) {
			Debug.Log("���ش���:InitFrame");
			return;
		}

		LinearLayout frame = (LinearLayout)mInflater.inflate(frameResId, frameRoot);

		if (frameResId == R.layout.func_frame_split) {
			mContentFrame = new LinearLayout[2];
			mContentFrame[0] = (LinearLayout)frame.findViewById(R.id.split_content_1);
			mContentFrame[1] = (LinearLayout)frame.findViewById(R.id.split_content_2);
		} else if (frameResId == R.layout.func_frame_triple) {
			mContentFrame = new LinearLayout[3];
			mContentFrame[0] = (LinearLayout)frame.findViewById(R.id.triple_content_1);
			mContentFrame[1] = (LinearLayout)frame.findViewById(R.id.triple_content_2);
			mContentFrame[2] = (LinearLayout)frame.findViewById(R.id.triple_content_3);
		} else {
			Debug.Log("���ش���:InitFrame2");
			return;
		}
	}
	
	/**
	 * ��ʼ���������
	 * @param title
	 * @param content
	 * @param bottomCustom
	 * @param root
	 */
	protected void initContent(String title, View content, LinearLayout bottomCustom, LinearLayout root) {

		RelativeLayout layout = (RelativeLayout)mInflater.inflate(R.layout.common_frame, null);
		TextView titleTextView = (TextView)layout.findViewById(R.id.common_frame_title);
		LinearLayout contentLayout = (LinearLayout)layout.findViewById(R.id.common_frame_content);
		LinearLayout bottomLayout = (LinearLayout)layout.findViewById(R.id.common_frame_custom_bottom);
		
		// ����
		if (title != null)
			titleTextView.setText(title);
		else
			titleTextView.setVisibility(View.GONE);
		
		// ����
		if (content != null)
			contentLayout.addView(content, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
		else
			contentLayout.setVisibility(View.GONE);
		
		// �ײ�������ͼ
		if (bottomCustom != null)
			bottomLayout.addView(bottomCustom, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
		else
			bottomLayout.setVisibility(View.GONE);

		// �������Frame
		root.removeAllViews();
		root.addView(layout, UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
	}
	
	/**
	 * ��ʼ��û�еײ����ƵĿ������
	 * @param title
	 * @param content
	 * @param root
	 */
	protected void initContent(String title, View content, LinearLayout root) {
		initContent(title, content, null, root);
	}
	
	/**
	 * ��ȡ��ť��ͼ
	 * @return
	 */
	protected LinearLayout initButton(String text, OnClickListener listener) {

		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.common_btn, null);
		Button btn = (Button) layout.findViewById(R.id.common_btn);
		btn.setText(text);
		//createInfoBtn.setBackgroundResource(R.drawable.button_class);
		//createInfoBtn.setLayoutParams(UIConst.GetLayoutParams(L_LAYOUT_TYPE.FULL));
		btn.setOnClickListener(listener);

		return layout;
	}
	
	/**
	 * ���ɲ���ʼ����ť��
	 * @param clickListener
	 * @param left
	 * @param right
	 * @return
	 */
	protected LinearLayout initButtonPair(int left, int right, OnClickListener listener) {

		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.common_btn_pair, null);
		Button btn = (Button) layout.findViewById(R.id.common_btn_pair_left);
		btn.setText(left);
		btn.setOnClickListener(listener);
		
		btn = (Button) layout.findViewById(R.id.common_btn_pair_right);
		btn.setText(right);
		btn.setOnClickListener(listener);

		return layout;
	}
}
