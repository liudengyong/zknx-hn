package com.zknx.hn.home;

import java.util.ArrayList;
import java.util.List;

import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.R;

import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Functions {
	
	public static List<LinearLayout> GetSubFunctionList(LayoutInflater inflater, OnClickListener listener) {
		int id = UIConst.FUNCTION_CLASS_ID_ZKNX;
		List<LinearLayout> subFunctionList = new ArrayList<LinearLayout>();

		subFunctionList.add(createBtnGroup(inflater, UIConst.FUNCTIONS_ZKNX, id, listener));
		
		id = UIConst.FUNCTION_CLASS_ID_PARTY;
		subFunctionList.add(createBtnGroup(inflater, UIConst.FUNCTIONS_PARTY, id, listener));
		
		id = UIConst.FUNCTION_CLASS_ID_POLICY;
		subFunctionList.add(createBtnGroup(inflater, UIConst.FUNCTIONS_POLICY, id, listener));
		
		return subFunctionList;
	}
	
	static LinearLayout createBtnGroup(LayoutInflater inflater, String[] functions, int id, OnClickListener listener) {
		
		LinearLayout group = (LinearLayout)inflater.inflate(R.layout.func_list, null);
		
		for (String function : functions) {
			group.addView(createSubFunctionBtn(inflater, function, ++id, listener), UIConst.GetLayoutParams(L_LAYOUT_TYPE.W_WEIGHT_1));
		}
		
		return group;
	}
	
	static RelativeLayout createSubFunctionBtn(LayoutInflater inflater, String function, int id, OnClickListener listener) {
		
		RelativeLayout btnLayout = (RelativeLayout)inflater.inflate(R.layout.func_btn, null);
		
		//btnLayout.setBackgroundResource(R.drawable.btn_bg);
		
		Button btn = (Button)btnLayout.findViewById(R.id.btn_sub_tobe);

		btn.setText(function);
		btn.setId(id);
		btn.setOnClickListener(listener);
		
		return btnLayout;
	}
}
