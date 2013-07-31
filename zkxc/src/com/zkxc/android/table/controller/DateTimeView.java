package com.zkxc.android.table.controller;

import java.util.Calendar;

import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.table.GridCell;
import com.zkxc.android.table.Input.Type;
import com.zkxc.android.R;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressLint("ViewConstructor")
public class DateTimeView extends TextView {
	
	Context mContext;
	Type mType;
	
	TimePickerDialog mTimePickerDialog;
	DatePickerDialog mDatePickerDialog;
	
	public DateTimeView(Context context, Type type, String record)
	{
		super(context);
		
		mType = type;
		
		// 初始化排版风格
		initLayoutStyle();
		
		if (record != null)
		{
			initView(record);
		}
		else
		{
			if (mType == Type.DATE)
				this.setText("获取日期");
			else if (mType == Type.TIME)
				this.setText("获取时间");
			
			this.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 初始化视图 
					initView(null);
				}
			});
		}
		
		this.setEnabled(!AppZkxc.mSyncedRecord);
	}
	
	 /**  
	  * 初始化排版风格
	  */
	void initLayoutStyle()
	{
        setTextSize(GridCell.DEFAULT_FONT_SIZE); 
        setBackgroundColor(Color.LTGRAY);
        setGravity(Gravity.CENTER);

        setTextColor(GridCell.DEFAULT_FONT_COLOR);
        
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
	}
	
	 /**  
	  * 初始化视图 
	  */
	void initView(String record)
	{
		if (record == null)
		{
			Calendar calendar = Calendar.getInstance();
			
			if (mType == Type.DATE) 
			{
				setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
			} 
			else 
			{
				setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
			}
		}
		else
		{
			setText(record.toString());
		}
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showDateTimeDialog();
			}
		});
		
		setBackgroundColor(Color.LTGRAY);
		
		setTag(R.id.tag_view, this);
	}
	
	public String formatDate(int year, int monthOfYear, int dayOfMonth)
	{
		return String.format("%04d-%02d-%02d", year, monthOfYear, dayOfMonth);
	}
	
	public String formatTime(int hourOfDay, int minute)
	{
		// 不能有分隔符:
		return String.format("%02d：%02d", hourOfDay, minute);
	}
	
	protected void setTime(int hourOfDay, int minute) {
		setText(formatTime(hourOfDay, minute));
	}

	protected void setDate(int year, int monthOfYear, int dayOfMonth) {
		setText(formatDate(year, monthOfYear, dayOfMonth));
	}

	// 弹出设置日期或时间对话框
	void showDateTimeDialog()
	{
		Calendar calendar = Calendar.getInstance();
		
		if (mType == Type.DATE)
		{
			if (mDatePickerDialog == null)
			{
				mDatePickerDialog = new DatePickerDialog(AppZkxc.mActivityTable, new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
						setDate(year, monthOfYear, dayOfMonth);
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
			}
			
			mDatePickerDialog.show();
		} 
		else if (mType == Type.TIME)
		{
			if (mTimePickerDialog == null)
			{
				mTimePickerDialog = new TimePickerDialog(AppZkxc.mActivityTable, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						setTime(hourOfDay, minute);
					}
				}, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
			}
			
			mTimePickerDialog.show();
		}
	}
	
	public String getRecord()
	{
		return (String) getText();
	}
}
