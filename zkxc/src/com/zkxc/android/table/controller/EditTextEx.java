package com.zkxc.android.table.controller;

import com.zkxc.android.R;
import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.table.GridLayout;
import com.zkxc.android.table.controller.InputView.InputChangeLisener;
import com.zkxc.android.table.controller.Sum.SumGetValue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ViewConstructor")
public class EditTextEx extends EditText implements InputChangeLisener, SumListener, SumGetValue {

	String[] mValues = null;
	Sum mSum;
	
	static int mIndexForDebug = 0;
	
	public EditTextEx(Context context, String inputValues) {
		super(context);
		
		// 调试
		if (AppZkxc.mDebug) {
			if (mIndexForDebug == 0)
				inputValues = "红080808083045809435,橙,黄";
			else if (mIndexForDebug == 1)
				inputValues = "红080808083045809435,橙,黄,绿";
			else
				inputValues = "红080808083045809435,橙,黄,绿,蓝,靛,紫";
			
			mIndexForDebug = (++mIndexForDebug) % 3;
		}
		
        // 快捷输入法 数据
        if (inputValues != null) {
        	mValues = inputValues.split(",");
        	if (checkShortInputData()) {
        		initActionListener();
        	}
        }
        
        // 合计
        addTextChangedListener(new TextWatcher() {   
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {   
            }   
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,   
                    int after) {
            }   
            @Override  
            public void afterTextChanged(Editable s) {

            	if (s.toString().contains(GridLayout.SEP_RECORD)) {
            		Context context = EditTextEx.this.getContext();
            		Toast.makeText(context, context.getString(R.string.congtains_reserved) + GridLayout.SEP_RECORD, Toast.LENGTH_SHORT).show();
            		
            		// 删除字符串中保留字
            		String src = s.toString();
            		src = src.replace(GridLayout.SEP_RECORD, "");
            		EditTextEx.this.setText(src);
            		return;
            	}
            	
                if (mSum != null)
                {
                	// 看是否可统计
                	try {
	                	Float.parseFloat(s.toString());
	                	mSum.ReCompute();
                	}
                	catch (NumberFormatException e)
                	{
                	}
                }
            }   
        });
        
        disableSyncedRecord();
	}
	
	private void disableSyncedRecord()
	{
		if (AppZkxc.mSyncedRecord)
		{
			this.setHint("");
			this.setBackgroundColor(Color.LTGRAY);
			this.setEnabled(false);
		}
		else
		{
	        this.setHint("请输入");
	        this.setBackgroundResource(R.drawable.edit_text);
		}
	}
	
	private void initActionListener() {
		
		// 输入法打开关闭后都可查找到输入数据
		setTag(R.id.tag_short_input, mValues);
		
		// 焦点监听
		setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				onFocusChanged(hasFocus);
			}
		});
		
		// 点击监听
        setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onFocusChanged(EditTextEx.this.isFocused());
			}
        });
	}

	private boolean checkShortInputData() {
		return (mValues != null && mValues.length > 0);
	}

	void onFocusChanged(boolean hasFocus) {
		
		Log.e("", "AppZkxc.IsShortInputOn = " + AppZkxc.IsShortInputOn);
		
		// 如果没有快输数据 返回
		// 如果 输入框禁用 返回
		if (!checkShortInputData() || !this.isEnabled())
			return;
		
		if (AppZkxc.IsShortInputOn) {
			if (hasFocus) {
				
				int[] location = new int[2] ;
				//view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
				getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
				
				int x = location[0]; // location [0]--->x坐标
				int y = location[1]; // location [1]--->y坐标
				
				Rect rect = new Rect(x, y, x + getWidth(), y + getHeight());
				AppZkxc.ShowInputView(this, rect);
				
			} else {
				AppZkxc.CloseInputView(getContext());
			}
		} else if (hasFocus) { // 快输关闭，但是焦点在当前输入框中
			setInputType(InputType.TYPE_NULL);
			AppZkxc.GetInputMan(getContext()).showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
			
			Log.e("", "SHOW_IMPLICIT");
		}
	}

	@Override
	public void OnInputChanged(String value) {
		// 判断输入框是否禁用
		if (this.isEnabled())
		{
			setText(value);
		}
	}

	// 合计
	@Override
	public void SetSumListener(Sum sum) {
		mSum = sum;
		mSum.add(this);
	}

	// 合计
	@Override
	public float getValue() {
		try {
        	return Float.parseFloat(getEditableText().toString());
    	}
    	catch (NumberFormatException e)
    	{
    	}
		
		return 0;
	}
}
