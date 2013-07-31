package com.zkxc.android.table.controller;

import java.util.Vector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class Sum extends TextView {
	
	Vector<SumGetValue> mSums;
	
	public interface SumGetValue {
		// TODO ZZZ 暂不支持合计控件嵌套合计数据
		float getValue(); // TODO ZZZ 合计为float格式
	}

	public Sum(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mSums = new Vector<SumGetValue>();
	}
	
	public void add(SumGetValue sumGetValue)
	{
		mSums.add(sumGetValue);
	}

	public void ReCompute() {
		float sum = 0;
		
		for (SumGetValue sumGetValue : mSums)
		{
			sum += sumGetValue.getValue();
		}
		
		setText(Float.toString(sum));
	}
}
