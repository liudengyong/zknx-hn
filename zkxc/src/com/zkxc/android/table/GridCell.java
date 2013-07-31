package com.zkxc.android.table;

import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zkxc.android.R;
import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.common.Converter;
import com.zkxc.android.common.Debug;
import com.zkxc.android.common.MessageBox;
import com.zkxc.android.table.GridLayout.CellTag;
import com.zkxc.android.table.Input.Type;
import com.zkxc.android.table.controller.DateTimeView;
import com.zkxc.android.table.controller.EditTextEx;
import com.zkxc.android.table.controller.LocationView;
import com.zkxc.android.table.controller.MediaPicker;
import com.zkxc.android.table.controller.MediaPicker.MediaListener;
import com.zkxc.android.table.controller.Sum;
import com.zkxc.android.table.controller.SumListener;

public class GridCell {

	public static final int DEFAULT_FONT_SIZE = 24;
	public static final int DEFAULT_FONT_COLOR = Color.BLACK; // TODO AAA 调整默认字体颜色
	public static final int GVIDVIEW_SIZE_SCALE = 2;
	
	private static final int CELL_BORDER_ZISE = 2;

	static LinearLayout GetCellView(int curRowIndex, int curColumnIndex, Parser parser, Map<String, Object> parentMap, Map<String, Object> record, Context context, NamedNodeMap attrMap)
	{
		int rowSpan = Converter.GetInt(attrMap, "rowSpan");
		int colSpan = Converter.GetInt(attrMap, "colSpan");
		
		// 隐藏的单元格
		if (rowSpan == 0 || colSpan == 0) return null;
		
		LinearLayout ly = new LinearLayout(context);
		ly.setOrientation(LinearLayout.VERTICAL);
		ly.setBackgroundColor(DEFAULT_FONT_COLOR);
		ly.setPadding(CELL_BORDER_ZISE, CELL_BORDER_ZISE, CELL_BORDER_ZISE, CELL_BORDER_ZISE);
		
		// 合并的单元格
		CellTag tag = null;
		if (rowSpan > 1 || colSpan > 1)
			tag = new CellTag(curRowIndex, curColumnIndex, rowSpan, colSpan);
		else
			tag = new CellTag(curRowIndex, curColumnIndex);

		String filed = Converter.GetData(attrMap, "filed");
		String parent = Converter.GetData(attrMap, "parent");

		tag.setFiled(filed);
		tag.setParent(parent);

		ly.setTag(R.id.tag_cell_tag, tag);

		//======================== 是否某单元格parent ==========================
		boolean isParent = (parentMap.get(filed) != null);
		if (isParent)
			ly.setTag(R.id.tag_cell_parent, "isParent");
		//======================== 是否某单元格parent ==========================

		Type   type = Converter.GetInputType(attrMap);
		String name = Converter.GetData(attrMap, "name");
		
        // 显示记录或者采集
		// 标签, 控件不显示名字
		// if (name != null && name.length() > 0)
		//	ly.addView(getLabelByName(name, context));

		// 采集控件
		if (type != Type.LABEL)
		{
			// 是某单元格的parent，则不初始化记录
			Object recordValue = null;
			if (!isParent && record != null)
				recordValue = record.get(filed);

			ly.addView(getCotrollerByType(attrMap, context, recordValue));
		}
		else
			ly.addView(getLabelByName(name, context));
		
		// 补空 的单元格，显示表格
		if (ly.getChildCount() == 0)
		{
			ly.addView(getLabelByName(" ", context));
		}
        
		return ly;
	}
	
	private static View getLabelByName(String name, Context mContext)
	{
		TextView label = new TextView(mContext, null, R.style.LLTextView);
		label.setText(name);
        label.setTextSize(DEFAULT_FONT_SIZE); 
        label.setBackgroundColor(Color.LTGRAY);
        label.setGravity(Gravity.CENTER);

        label.setTextColor(DEFAULT_FONT_COLOR);
        
        label.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
        
        return label;
	}
	
	private static View getCotrollerByType(NamedNodeMap attrMap, final Context mContext, Object recordValue)
	{
		View view;
		Type type = Converter.GetInputType(attrMap);
		
		switch (type)
        {
        case EDIT:
        {
	        EditTextEx editbox = new EditTextEx(mContext, Converter.GetData(attrMap, "short_input")); 
	        
	        if (recordValue != null)
	        	editbox.setText(recordValue.toString());
	        
	        view = editbox;
	        view.setTag(R.id.tag_view, view);
        }
        break;
        case CKECKBOX:
        {
	        CheckBox checkbox = new CheckBox(mContext);
	        checkbox.setText(Converter.GetData(attrMap, "name"));
	        //checkbox.setBackgroundResource(R.drawable.info_background);
	        //checkbox.setButtonDrawable(R.drawable.info_background);
	        checkbox.setTextSize(DEFAULT_FONT_SIZE);
	        checkbox.setTextColor(android.graphics.Color.WHITE);
	        checkbox.setGravity(Gravity.CENTER);
	        checkbox.setBackgroundColor(Color.LTGRAY);
	        
	        checkbox.setGravity(Gravity.CENTER);
	        
	        if (recordValue != null && recordValue.toString().equals("true"))
	        	checkbox.setChecked(true);
	        
	        view = checkbox;
	        view.setTag(R.id.tag_view, view);

    		view.setEnabled(!AppZkxc.mSyncedRecord);
        }
        break;
        case LOCATION:
        {
        	// 有无值都统一构造
        	LocationView location = new LocationView(mContext, recordValue);
        	//location.getView().setMinimumWidth(360);
	        view = location.getView();
        	
    		// 设置单元格view统一背景颜色
    		view.setBackgroundColor(Color.LTGRAY);
        }
        break;
        case AUDIO:
        case PICTURE:
        case VIDEO:
        {
        	MediaPicker media = new MediaPicker(mContext, (MediaListener)mContext, type, recordValue); 
	        view = media.getView();
	        
    		// 设置单元格view统一背景颜色
    		view.setBackgroundColor(Color.LTGRAY);
        }
        break;
        case DATE:
        case TIME:
        {
        	DateTimeView date = new DateTimeView(mContext, type, 
        			recordValue != null ? recordValue.toString() : null);
	        view = date;
	        
    		// 设置单元格view统一背景颜色
    		view.setBackgroundColor(Color.LTGRAY);
        }
        break;
        case LIST:
        {
        	final String data = Converter.GetData(attrMap, "data");
        	String lable = null;
        	
        	// 初始化历史数据
			if (recordValue != null)
			{
				Debug.Log("List : " + recordValue);
				lable = recordValue.toString();
			}
			else if (data != null && data.split(",") != null)
			{
				lable = data.split(",")[0];
			}
			
        	final TextView list = (TextView) getLabelByName(lable, mContext);
        	
        	list.setBackgroundResource(R.drawable.btn_dropdown_normal);
        	
        	list.setOnClickListener(new OnClickListener()
        	{
				@Override
				public void onClick(View v) {
					if (data != null)
					{
						MessageBox.ShowList(mContext, list, data);
					}
				}
        	});
        	
        	view = list;
        	view.setTag(R.id.tag_view, view);
        	
        	view.setEnabled(!AppZkxc.mSyncedRecord);
        }
        break;
        case BIO: 
        {
        	TextView tv = new TextView(mContext, null, R.style.LLTextView); 
            tv.setText(Converter.GetData(attrMap, "spiceName")); 
            tv.setTextSize(DEFAULT_FONT_SIZE); 
            
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(DEFAULT_FONT_COLOR);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            
            view = tv;
            view.setTag(R.id.tag_view, view);
            
    		// 设置单元格view统一背景颜色
    		view.setBackgroundColor(Color.LTGRAY);
    		
    		view.setEnabled(!AppZkxc.mSyncedRecord);
        }
        break;
        case LABEL:
        {
        	TextView tv = new TextView(mContext, null, R.style.LLTextView); 
            tv.setText(Converter.GetData(attrMap, "data")); 
            tv.setBackgroundResource(R.drawable.textfield_disabled);
            view = tv;
        }
        break;
        case SUM:
        {
        	Sum tv = new Sum(mContext, null, R.style.ZkTextView); 
            //tv.setText(ctrlAttr.data);
            tv.setText("0"); // TODO 000(待测试) 合计实现
            tv.setWidth(80);
            tv.setGravity(Gravity.CENTER);
            
            // 需要合计的单元格
            String src = Converter.GetData(attrMap, "src");
            if (src != null && src.length() > 0)
            {
            	String token[] = src.split(",");
            	if (token != null && token.length > 0)
            	{
            		tv.setTag(R.id.tag_sum_src, token);
            	}
            }
            
            //tv.setBackgroundResource(R.drawable.textfield_disabled);
            view = tv;
            
    		// 设置单元格view统一背景颜色
    		view.setBackgroundColor(Color.LTGRAY);
        }
        break;
        default:
        {
            TextView unknown = new TextView(mContext, null, R.style.LLTextView);
            unknown.setText("未知"); 
            unknown.setGravity(Gravity.CENTER);
            view = unknown;
            
    		// 设置单元格view统一背景颜色
    		view.setBackgroundColor(Color.LTGRAY);
        }
        break;
        }
		
		// 设置key为tag，便于sum控件合计，保存记录和上传记录
		
		String key = Converter.GetData(attrMap, "filed");
		if (key != null)
			view.setTag(R.id.tag_key, key);
		
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
		
		return view;
	}
	
	public static Sum GetSumView(LinearLayout cellLayout)
	{
		// 必然有两个children view，第二个是控件的view
		if (cellLayout.getChildCount() == 2)
		{
			View view = cellLayout.getChildAt(1);
			
			if (view instanceof Sum)
			{
				Object sumTag = view.getTag(R.id.tag_sum_src);
				
				if (sumTag != null)
				{
					return (Sum)view;
				}
			}
		}
		
		return null;
	}
	
	public static String GetCellParent(LinearLayout c)
	{
		CellTag tag = (CellTag)c.getTag(R.id.tag_cell_tag);
		if (tag != null)
			return tag.getParent();
		else
			return null;
	}

	public static String GetRecord(LinearLayout cellLayout)
	{
		Debug.Log("GetRecord, cellLayout.getChildCount : " + cellLayout.getChildCount());
		
		View view = null;
		// 必然有两个children view，第二个是控件的view
		if (cellLayout.getChildCount() == 2)
		{
			view = cellLayout.getChildAt(1);
		}
		else if (cellLayout.getChildCount() == 1)
		{
			view = cellLayout.getChildAt(0);
		}
		else
			return null;
		
		Object viewTag = view.getTag(R.id.tag_view);
		
		Debug.Log("GetRecord, viewTag : " + ((viewTag == null) ? "null" : viewTag.toString()));
		
		if (viewTag != null)
		{
			if (view instanceof EditText)
			{
				EditText child = (EditText)view;
				return child.getEditableText().toString();
			}
			else if (view instanceof CheckBox)
			{
				CheckBox child = (CheckBox)view;
				return child.isChecked() ? "true" : "false";
			}
			else if (view instanceof Spinner)
			{
				Spinner child = (Spinner)view;
				Debug.Log("GetRecord, List : " + child.getSelectedItem().toString());
				return child.getSelectedItem().toString();
			}
			else if (view instanceof TextView)
			{
				TextView child = (TextView)view;
				return child.getText().toString();
			}
			
			if (viewTag instanceof DateTimeView)
			{
				DateTimeView dateTime = (DateTimeView)viewTag;
				return dateTime.getRecord();
			}
			else if (viewTag instanceof LocationView)
			{
				LocationView child = (LocationView)viewTag;
				return child.getRecord();
			}
			else if (viewTag instanceof MediaPicker)
			{
				MediaPicker child = (MediaPicker)viewTag;
				return child.getRecord();
			}
		}
		
		return null;		
	}
	
	public static String GetLocationRecord(LinearLayout cellLayout)
	{
		if (cellLayout == null)
			return null;
		
		//Debug.Log("GetLocationRecord, cellLayout.getChildCount : " + cellLayout.getChildCount());
		
		View view = null;
		// 必然有两个children view，第二个是控件的view
		if (cellLayout.getChildCount() == 2)
		{
			view = cellLayout.getChildAt(1);
		}
		else if (cellLayout.getChildCount() == 1)
		{
			view = cellLayout.getChildAt(0);
		}
		else
			return null;
		
		Object viewTag = view.getTag(R.id.tag_view);
		
		//Debug.Log("GetLocationRecord, viewTag : " + ((viewTag == null) ? "null" : viewTag.toString()));
		
		if (viewTag != null)
		{
			if (viewTag instanceof LocationView)
			{
				LocationView child = (LocationView)viewTag;
				return child.getRecord();
			}
		}
		
		return null;		
	}


	public static void SetSumListener(LinearLayout cellLayout, Sum sum) 
	{
		// 必然有两个children view，第二个是控件的view
		if (cellLayout.getChildCount() == 2)
		{
			View view = cellLayout.getChildAt(1);
			
			// TODO AAA 目前只能监听输入框 ExitTextEx
			try {
				SumListener sumListener = (SumListener)view;
				sumListener.SetSumListener(sum);
			}
			// 转换错误则是不能统计数据控件
			catch (Exception e)
			{
			}
		}
	}
}
