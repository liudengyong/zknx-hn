package com.zknx.hn.common;

import com.zknx.hn.data.DataMan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class Checkbox extends LinearLayout {
	
	private TextView mTv;
	private CheckBox mCheckbox;

	public Checkbox(Context context, String label) {
		super(context);
		
		// ��ʼ����ǩ
		mTv = Label.Get(context, label);
		
		// ��ʼ����ѡ��
		mCheckbox = new CheckBox(context);
		
		// �����Ű�
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		
		addView(mTv, params);
		addView(mCheckbox);
	}
	
	/**
	 * �Ƿ�ѡ��
	 * @return
	 */
	public boolean isChecked() {
		return mCheckbox.isChecked() ? mCheckbox != null : false;
	}
	
	/**
	 * ������ʽ����ǩ�����С����ɫ��checkbox����ͼƬ
	 */
	public void setStyle(int fontSize, int fontColor, int checkboxBg) {
		if (fontSize != DataMan.INVALID_ID) {
			mTv.setTextSize(fontSize);
		}
		
		if (fontColor != DataMan.INVALID_ID) {
			mTv.setTextColor(fontColor);
		}
		
		if (checkboxBg != DataMan.INVALID_ID) {
			mCheckbox.setButtonDrawable(checkboxBg);
		}
	}
}
