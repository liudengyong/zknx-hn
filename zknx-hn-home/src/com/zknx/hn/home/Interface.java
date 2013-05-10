package com.zknx.hn.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Interface {

    // 包名
    public static final String ZKNX_PACKAGE_NAME = "com.zknx.hn";
    // 类名
    public static final String ENTRANCE_CLASS_NAME = "com.zknx.hn.Entrance";
    // 参数key
    public static final String FUNCTION_KEY = "funtion";
    
    // 中科农信栏目
	public static final int FUNCTION_CLASS_ID_ZKNX       = 200;
	// 市场行情
	public static final int FUNCTION_ID_MARKET           = FUNCTION_CLASS_ID_ZKNX + 1;
	// 自选产品
	public static final int FUNCTION_ID_CUSTOM_PRODUCT   = FUNCTION_CLASS_ID_ZKNX + 2;
	// 供求信息
	public static final int FUNCTION_ID_SUPPLY_DEMAND    = FUNCTION_CLASS_ID_ZKNX + 3;
	// 我的商圈
	public static final int FUNCTION_ID_MY_GROUP         = FUNCTION_CLASS_ID_ZKNX + 4;
	// 农业技术
	public static final int FUNCTION_ID_ARGRI_TECH       = FUNCTION_CLASS_ID_ZKNX + 5;
	// 专家指导
	public static final int FUNCTION_ID_EXPERT_GUIDE     = FUNCTION_CLASS_ID_ZKNX + 6;
	// 我的供求
	public static final int FUNCTION_ID_MY_SUPPLY_DEMAND = FUNCTION_CLASS_ID_ZKNX + 7;
	// 专家施肥
	public static final int FUNCTION_ID_EXPERT_FERTILIZE = FUNCTION_CLASS_ID_ZKNX + 8;

	// 红星党建栏目
	public static final int FUNCTION_CLASS_ID_PARTY      = 300;
	// 时政要闻
	public static final int FUNCTION_ID_CUR_POLITICS     = FUNCTION_CLASS_ID_PARTY + 1;
	// 精选课件
	public static final int FUNCTION_ID_BEST_COUSE       = FUNCTION_CLASS_ID_PARTY + 2;
	// 先锋党员
	public static final int FUNCTION_ID_VANGUARD_PARTY   = FUNCTION_CLASS_ID_PARTY + 3;
	// 典型经验
	public static final int FUNCTION_ID_CLASS_EXPERIENCE = FUNCTION_CLASS_ID_PARTY + 4;
	// 致富模范
	public static final int FUNCTION_ID_MODEL            = FUNCTION_CLASS_ID_PARTY + 5;
	// 快乐农家
	public static final int FUNCTION_ID_HAPPAY           = FUNCTION_CLASS_ID_PARTY + 6;
	// 法律法规
	public static final int FUNCTION_ID_LAW              = FUNCTION_CLASS_ID_PARTY + 7;
	// 惠农政策
	public static final int FUNCTION_ID_POLICY           = FUNCTION_CLASS_ID_PARTY + 8;
	
	// 设置
	public static final int FUNCTION_CLASS_ID_SETTING    = 400;
	public static final int FUNCTION_ID_SETTING          = FUNCTION_CLASS_ID_SETTING + 1;

    public static void StartZknxActivity(Context context, int function) {

    	Intent intent = new Intent();

    	intent.setClassName(ZKNX_PACKAGE_NAME, ENTRANCE_CLASS_NAME);
    	intent.putExtra(FUNCTION_KEY, function);

    	try {
    		context.startActivity(intent);
    	} catch (ActivityNotFoundException e) {
    		Toast.makeText(context, "启动中科农信错误，请联系开发人员", Toast.LENGTH_LONG).show();
    	}
    }
}
