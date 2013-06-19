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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BestCourse extends AisView {
	
	private LinearLayout mSubmitLayout;
	
	private final static int ID_SUBMIT = R.id.common_btn_pair_right;
	private final static int ID_RESET = R.id.common_btn_pair_left;

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
			};

			mSubmitLayout = (LinearLayout)mInflater.inflate(R.layout.common_btn_pair, null);
			
			Button btn = (Button) mSubmitLayout.findViewById(ID_SUBMIT);
			btn.setText("����");
			btn.setOnClickListener(mOnClickSubmit);
			
			btn = (Button) mSubmitLayout.findViewById(ID_RESET);
			btn.setText("����");
			btn.setOnClickListener(mOnClickSubmit);
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
	private void summit(final WebView webView) {
		Dialog.Confirm(mContext, R.string.confirm_submit_course, new ConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				webView.loadUrl("javascript:submitTest()");
			}
		});
	}

	/**
	 * ������ť
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
