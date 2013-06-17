package com.zknx.hn.common;

import android.widget.LinearLayout;

public class UIConst {
	
	public static final int FUNCTION_CLASS_ID_TV    = 100;
	public static final String[] FUNCTION_CLASSES = {"电视节目", "中科农信", "红星党建"};
	public static final String[] FUNCTIONS_TV = {"com.android.phone"}; // 电视节目由第三方实现
	
	public static final int FUNCTION_CLASS_ID_ZKNX       = 200;
	public static final int FUNCTION_ID_MARKET           = FUNCTION_CLASS_ID_ZKNX + 1;
	public static final int FUNCTION_ID_CUSTOM_PRODUCT   = FUNCTION_CLASS_ID_ZKNX + 2;
	public static final int FUNCTION_ID_SUPPLY_DEMAND    = FUNCTION_CLASS_ID_ZKNX + 3;
	public static final int FUNCTION_ID_MY_GROUP         = FUNCTION_CLASS_ID_ZKNX + 4;
	public static final int FUNCTION_ID_ARGRI_TECH       = FUNCTION_CLASS_ID_ZKNX + 5;
	public static final int FUNCTION_ID_EXPERT_GUIDE     = FUNCTION_CLASS_ID_ZKNX + 6;
	public static final int FUNCTION_ID_MY_SUPPLY_DEMAND = FUNCTION_CLASS_ID_ZKNX + 7;
	public static final int FUNCTION_ID_EXPERT_FERTILIZE = FUNCTION_CLASS_ID_ZKNX + 8;
	public static final String[] FUNCTIONS_ZKNX = {"市场行情", "自选产品", "供求信息", "我的商圈", 
		                                           "农业技术", "专家指导", "我的供求", "专家施肥"};
	
	public static final int FUNCTION_CLASS_ID_PARTY      = 300;
	public static final int FUNCTION_ID_CUR_POLITICS     = FUNCTION_CLASS_ID_PARTY + 1;
	public static final int FUNCTION_ID_BEST_COUSE       = FUNCTION_CLASS_ID_PARTY + 2;
	public static final int FUNCTION_ID_VANGUARD_PARTY   = FUNCTION_CLASS_ID_PARTY + 3;
	public static final int FUNCTION_ID_CLASS_EXPERIENCE = FUNCTION_CLASS_ID_PARTY + 4;
	public static final int FUNCTION_ID_MODEL            = FUNCTION_CLASS_ID_PARTY + 5;
	public static final int FUNCTION_ID_HAPPAY           = FUNCTION_CLASS_ID_PARTY + 6;
	public static final int FUNCTION_ID_LAW              = FUNCTION_CLASS_ID_PARTY + 7;
	public static final int FUNCTION_ID_POLICY           = FUNCTION_CLASS_ID_PARTY + 8;
	public static final String[] FUNCTIONS_PARTY = {"时政要闻", "精选课件", "优秀党员", "党建模范",
		                                            "致富模范", "快乐农家", "法律法规", "惠农政策"};
	
	public static final int FUNCTION_CLASS_ID_SETTING    = 400;
	public static final int FUNCTION_ID_SETTING          = FUNCTION_CLASS_ID_SETTING + 1;
	
	// 注册账号地址
	public static final String REG_ADDRESS = "http://218.106.254.101:8099/register.aspx";
	
	// 下载超时设置限制，默认下载超时，最大五分钟
	public static final int MAX_TIMEOUT = 300;
	
	public enum L_LAYOUT_TYPE {FULL, WRAP, H_WRAP, W_WRAP, W_WEIGHT_1, H_WEIGHT_1}

    public static LinearLayout.LayoutParams GetLayoutParams(L_LAYOUT_TYPE type) {
    	
    	int width = 0, height = 0;
    	
    	switch (type) {
    	case FULL:
    		width  = LinearLayout.LayoutParams.MATCH_PARENT;
    		height = LinearLayout.LayoutParams.MATCH_PARENT;
    		break;
    	case WRAP:
    		width  = LinearLayout.LayoutParams.WRAP_CONTENT;
    		height = LinearLayout.LayoutParams.WRAP_CONTENT;
    		break;
    	case H_WRAP:
    		width  = LinearLayout.LayoutParams.MATCH_PARENT;
    		height = LinearLayout.LayoutParams.WRAP_CONTENT;
    		break;
    	case W_WRAP:
    		width  = LinearLayout.LayoutParams.WRAP_CONTENT;
    		height = LinearLayout.LayoutParams.MATCH_PARENT;
    		break;
    	case W_WEIGHT_1:
    		width  = 0;
    		height = LinearLayout.LayoutParams.MATCH_PARENT;
    		break;
    	case H_WEIGHT_1:
    		width  = LinearLayout.LayoutParams.MATCH_PARENT;
    		height = 0;
    		break;
    	}
    	
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
    	
    	if (width == 0 || height == 0)
    		params.weight = 1;
        
    	return params;
    }
}
