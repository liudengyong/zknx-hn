package com.zknx.hn.functions;

import com.zknx.hn.R;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.Dialog.ConfirmListener;
import com.zknx.hn.common.UIConst;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BestCourse extends AisView {
	
	private LinearLayout mSubmitLayout;

	public BestCourse(LayoutInflater inflater, LinearLayout frameRoot) {
		super(inflater, frameRoot, UIConst.FUNCTION_ID_BEST_COUSE, R.layout.func_frame_split);
	}
	
	/**
	 * ���Ǹ��࣬�Ӷ�ʵ���Զ���View (����ͨAIS��ͼ��ͬ���μ���ͼҪ�ύ�Ծ��н�������Ҫ����ʵ��)
	 * @param class1_id
	 */
	@Override
	LinearLayout getCutomBottom() {
		
		initCouseView();
		
		return mSubmitLayout;
	}

	/**
	 * ��ʼ��������ͼ
	 */
	private void initCouseView() {
		if (mSubmitLayout == null) {
			
			OnClickListener mOnClickSubmit = new OnClickListener() {

				@Override
				public void onClick(View view) {
					summit();
				}
			};
			
			mSubmitLayout = (LinearLayout)mInflater.inflate(R.layout.best_course_submit, null);
			
			mSubmitLayout.findViewById(R.id.best_couse_submit).setOnClickListener(mOnClickSubmit);
		} else {
			// �������븸��
			ViewParent parent = mSubmitLayout.getParent();
			
			if (parent != null) {
				if (parent instanceof LinearLayout) {
					((LinearLayout)parent).removeAllViews();
				} else if (parent instanceof RelativeLayout) {
					((RelativeLayout)parent).removeAllViews();
				}
			}
		}
	}
	
	/**
	 * ����ť
	 */
	private void summit() {
		Dialog.Confirm(mContext, R.string.confirm_submit_course, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO interface ������
			}
		});
	}
}
