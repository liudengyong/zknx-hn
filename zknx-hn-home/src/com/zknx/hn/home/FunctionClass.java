package com.zknx.hn.home;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class FunctionClass {
	
	public static List<RelativeLayout> GetMainFunctionList(LayoutInflater inflater, OnClickListener listener) {
		return initMainFunctionList(inflater, listener);
	}
	
	static List<RelativeLayout> initMainFunctionList(LayoutInflater inflater, OnClickListener listener) {
		
		List<RelativeLayout> mainFunctionList = new ArrayList<RelativeLayout>();
		
		mainFunctionList.add(createMainFunctionBtn(inflater, UIConst.FUNCTION_CLASSES[0], UIConst.FUNCTION_CLASS_ID_TV, listener));
		mainFunctionList.add(createMainFunctionBtn(inflater, UIConst.FUNCTION_CLASSES[1], UIConst.FUNCTION_CLASS_ID_ZKNX, listener));
		mainFunctionList.add(createMainFunctionBtn(inflater, UIConst.FUNCTION_CLASSES[2], UIConst.FUNCTION_CLASS_ID_PARTY, listener));
		
		mainFunctionList.add(createMainFunctionBtn(inflater, "…Ë÷√", UIConst.FUNCTION_ID_SETTING, listener));
		
		return mainFunctionList;
	}
	
	static RelativeLayout createMainFunctionBtn(LayoutInflater inflater, String function, int id, OnClickListener listener) {
		
		RelativeLayout btnLayout = (RelativeLayout)inflater.inflate(R.layout.func_class_btn, null);
		
		Button btn = (Button)btnLayout.findViewById(R.id.btn_main_tobe);

		btn.setText(function);
		btn.setId(id);
		btn.setOnClickListener(listener);
		
		return btnLayout;
	}
}
