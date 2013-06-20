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
			mSubmitLayout = initButtonPair(R.string.resset, R.string.submit, new OnClickListener() {
				@Override
				public void onClick(View view) {
					int id = view.getId();
					WebView webView = mAisParser.getWebview();
					switch (id) {
					case ID_SUBMIT:
						summit(webView);
						break;
					case ID_RESET:
						reset(webView);
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
	 * 交卷按钮
	 */
	private void summit(final WebView webView) {
		Dialog.Confirm(mContext, R.string.confirm_submit_course, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				webView.loadUrl("javascript:submitTest()");
			}
		});
	}

	/**
	 * 重做按钮
	 */
	private void reset(final WebView webView) {
		Dialog.Confirm(mContext, R.string.confirm_reset_course, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				webView.loadUrl("javascript:resetTest()");
			}
		});
	}
}
