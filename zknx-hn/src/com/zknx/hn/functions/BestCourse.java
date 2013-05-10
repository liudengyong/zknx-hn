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
	 * 覆盖父类，从而实现自定义View (与普通AIS视图不同，课件视图要提交试卷，有交互，需要单独实现)
	 * @param class1_id
	 */
	@Override
	LinearLayout getCutomBottom() {
		
		initCouseView();
		
		return mSubmitLayout;
	}

	/**
	 * 初始化交卷视图
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
			// 首先脱离父类
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
	 * 交卷按钮
	 */
	private void summit() {
		Dialog.Confirm(mContext, R.string.confirm_submit_course, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO interface 处理交卷？
			}
		});
	}
}
