package com.zknx.hn.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

@SuppressLint("ViewConstructor")
public class Radio extends RadioGroup {
	
	RadioButton[] mButtons; 

	public Radio(Context context, String[] list, int checked) {
		super(context);
		
		// 水平排版
		setOrientation(HORIZONTAL);
		
		// 文字居中
		setGravity(/*Gravity.CENTER_HORIZONTAL | */Gravity.CENTER_VERTICAL);
		
		init(list, checked);
	}
	
	/**
	 * 初始化Radio按钮
	 * @param list
	 */
	private void init(String[] list, int checked) {
		if (list == null || list.length == 0) {
			Debug.Log("Radio 初始化错误");
			return;
		}

		mButtons = new RadioButton[list.length];
		
		for (int i = 0; i < list.length; ++i) {
			mButtons[i] = new RadioButton(getContext());
			mButtons[i].setText(list[i]);
			mButtons[i].setId(i); // 当前id
			if (i == checked)
				mButtons[i].setChecked(true);
			else
				mButtons[i].setChecked(false);

			addView(mButtons[i]);
		}
		
		/*
		setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mButtons != null) {
				}
			}
		});
		*/
	}
}
