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
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BestCourse extends AisView {

	private LinearLayout mSubmitLayout;

	private final static int ID_RESET = R.id.common_btn_pair_left;
	private final static int ID_SUBMIT = R.id.common_btn_pair_right;

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
			// 书吃喝交卷和重做按钮
			mSubmitLayout = initButtonPair(R.string.resset, R.string.submit, new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (view.getId()) {
					case ID_SUBMIT:
						invokeJsMethod(R.string.confirm_submit_course, "submitTest()");
						break;
					case ID_RESET:
						invokeJsMethod(R.string.confirm_reset_course, "resetTest()");
						break;
					}
				}
			});
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
	 * 调用javascript函数
	 * @param method
	 */
	private void invokeJsMethod(int msg, final String method) {
		Dialog.Confirm(mContext, msg, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				WebView webView = mAisParser.getWebview();
				if (webView != null)
					webView.loadUrl("javascript:" + method);
			}
		});
	}
}
