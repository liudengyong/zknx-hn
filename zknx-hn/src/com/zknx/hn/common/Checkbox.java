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
		
		// 初始化标签
		mTv = Label.Get(context, label);
		
		// 初始化复选框
		mCheckbox = new CheckBox(context);
		
		// 居中排版
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		
		addView(mTv, params);
		addView(mCheckbox);
	}
	
	/**
	 * 是否选中
	 * @return
	 */
	public boolean isChecked() {
		return mCheckbox.isChecked() ? mCheckbox != null : false;
	}
	
	/**
	 * 设置样式：标签字体大小，颜色，checkbox背景图片
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
