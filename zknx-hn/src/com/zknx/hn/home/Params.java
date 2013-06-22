package com.zknx.hn.home;

import com.zknx.hn.R;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.functions.*;
import com.zknx.hn.functions.common.FunctionView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class Params {
	
	static final String KEY_TITLE = "title";
	static final String KEY_FUNCTION_ID = "function_id";
	static final String KEY_FUNCTION_CLASS = "function_class";
	static final String KEY_FRAME_ID= "frame_id";
	
	public static final int FUNCTION_CLASS_ZKNX = 1;
	public static final int FUNCTION_CLASS_PARTY = 2;
	
	public static Bundle GetExtras(int id) {
		return InitExtras(id);
	}

	public static String GetTitle(Bundle extras) {
		return extras.getString(KEY_TITLE);
	}

	/**
	 * 获取功能id
	 * @param extras
	 * @return
	 */
	public static int GetFunction(Bundle extras) {
		return extras.getInt(KEY_FUNCTION_ID);
	}
	
	/**
	 * 获取功能分类，中科农信或者红星党建
	 * @param extras
	 * @return
	 */
	public static int GetFunctionClass(Bundle extras) {
		return extras.getInt(KEY_FUNCTION_CLASS);
	}

	/**
	 * 获取视图类实例，用于初始化功能视图
	 * @param extras
	 * @return
	 */
	public static FunctionView GetFunctionView(int function_id, LayoutInflater inflater, LinearLayout frameRoot) {
		switch (function_id) {
		case UIConst.FUNCTION_ID_MARKET:
			return new Market(inflater, frameRoot, R.layout.func_frame_triple);
		case UIConst.FUNCTION_ID_CUSTOM_PRODUCT:
			return new MyProduct(inflater, frameRoot, R.layout.func_frame_split);
		case UIConst.FUNCTION_ID_SUPPLY_DEMAND:
			return new SupplyDemand(inflater, frameRoot, R.layout.func_frame_triple);
		case UIConst.FUNCTION_ID_MY_SUPPLY_DEMAND:
			return new MySupplyDemand(inflater, frameRoot, R.layout.func_frame_triple);
		case UIConst.FUNCTION_ID_MY_GROUP:
			return new MyGroup(inflater, frameRoot, R.layout.func_frame_triple);
		// 两区显示
		case UIConst.FUNCTION_ID_MODEL:
		case UIConst.FUNCTION_ID_VANGUARD_PARTY:
		case UIConst.FUNCTION_ID_EXPERT_FERTILIZE:
		case UIConst.FUNCTION_ID_BEST_COUSE: // 精选课件
			return new AisView(inflater, frameRoot, function_id, R.layout.func_frame_split);
		// 三区显示
		case UIConst.FUNCTION_ID_POLICY:
		case UIConst.FUNCTION_ID_ARGRI_TECH:
		case UIConst.FUNCTION_ID_CUR_POLITICS:
		case UIConst.FUNCTION_ID_CLASS_EXPERIENCE:
		case UIConst.FUNCTION_ID_HAPPAY:
		case UIConst.FUNCTION_ID_LAW:
			return new AisView(inflater, frameRoot, function_id, R.layout.func_frame_triple);
		case UIConst.FUNCTION_ID_EXPERT_GUIDE: // 专家指导
			return new Expert(inflater, frameRoot);
		case UIConst.FUNCTION_ID_SETTING:
			return new Setting(inflater, frameRoot, R.layout.func_frame_split);
		default:
			return null; // 返回错误
		}
	}
	
	/**
	 * 初始化参数
	 * @param id
	 * @return
	 */
	private static Bundle InitExtras(int id) {
		Bundle extras = new Bundle();
		
		// 功能ID
		extras.putInt(KEY_FUNCTION_ID, id);
		
		int index = id - UIConst.FUNCTION_CLASS_ID_PARTY - 1;
		
		// 标题
		if (index >= 0 && index < UIConst.FUNCTIONS_PARTY.length) {
			extras.putInt(KEY_FUNCTION_CLASS, FUNCTION_CLASS_PARTY);
			extras.putString(KEY_TITLE, UIConst.FUNCTIONS_PARTY[index]);
		}
		else {
			index = id - UIConst.FUNCTION_CLASS_ID_ZKNX - 1;
			
			if (index >= 0 && index < UIConst.FUNCTIONS_ZKNX.length) {
				extras.putInt(KEY_FUNCTION_CLASS, FUNCTION_CLASS_ZKNX);
				extras.putString(KEY_TITLE, UIConst.FUNCTIONS_ZKNX[index]);
			}
			else if (id == UIConst.FUNCTION_ID_SETTING)
				extras.putString(KEY_TITLE, "设置");;
		}
		
		return extras;
	}
}
