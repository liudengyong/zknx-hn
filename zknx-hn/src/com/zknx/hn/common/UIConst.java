package com.zknx.hn.common;

import android.widget.LinearLayout;

public class UIConst {
	
	public static final int FUNCTION_CLASS_ID_TV    = 100;
	public static final String[] FUNCTION_CLASSES = {"���ӽ�Ŀ", "�п�ũ��", "���ǵ���"};
	public static final String[] FUNCTIONS_TV = {"com.android.phone"}; // ���ӽ�Ŀ�ɵ�����ʵ��
	
	public static final int FUNCTION_CLASS_ID_ZKNX       = 200;
	public static final int FUNCTION_ID_MARKET           = FUNCTION_CLASS_ID_ZKNX + 1;
	public static final int FUNCTION_ID_CUSTOM_PRODUCT   = FUNCTION_CLASS_ID_ZKNX + 2;
	public static final int FUNCTION_ID_SUPPLY_DEMAND    = FUNCTION_CLASS_ID_ZKNX + 3;
	public static final int FUNCTION_ID_MY_GROUP         = FUNCTION_CLASS_ID_ZKNX + 4;
	public static final int FUNCTION_ID_ARGRI_TECH       = FUNCTION_CLASS_ID_ZKNX + 5;
	public static final int FUNCTION_ID_EXPERT_GUIDE     = FUNCTION_CLASS_ID_ZKNX + 6;
	public static final int FUNCTION_ID_MY_SUPPLY_DEMAND = FUNCTION_CLASS_ID_ZKNX + 7;
	public static final int FUNCTION_ID_EXPERT_FERTILIZE = FUNCTION_CLASS_ID_ZKNX + 8;
	public static final String[] FUNCTIONS_ZKNX = {"�г�����", "��ѡ��Ʒ", "������Ϣ", "�ҵ���Ȧ", 
		                                           "ũҵ����", "ר��ָ��", "�ҵĹ���", "ר��ʩ��"};
	
	public static final int FUNCTION_CLASS_ID_PARTY      = 300;
	public static final int FUNCTION_ID_CUR_POLITICS     = FUNCTION_CLASS_ID_PARTY + 1;
	public static final int FUNCTION_ID_BEST_COUSE       = FUNCTION_CLASS_ID_PARTY + 2;
	public static final int FUNCTION_ID_VANGUARD_PARTY   = FUNCTION_CLASS_ID_PARTY + 3;
	public static final int FUNCTION_ID_CLASS_EXPERIENCE = FUNCTION_CLASS_ID_PARTY + 4;
	public static final int FUNCTION_ID_MODEL            = FUNCTION_CLASS_ID_PARTY + 5;
	public static final int FUNCTION_ID_HAPPAY           = FUNCTION_CLASS_ID_PARTY + 6;
	public static final int FUNCTION_ID_LAW              = FUNCTION_CLASS_ID_PARTY + 7;
	public static final int FUNCTION_ID_POLICY           = FUNCTION_CLASS_ID_PARTY + 8;
	public static final String[] FUNCTIONS_PARTY = {"ʱ��Ҫ��", "��ѡ�μ�", "���㵳Ա", "����ģ��",
		                                            "�¸�ģ��", "����ũ��", "���ɷ���", "��ũ����"};
	
	public static final int FUNCTION_CLASS_ID_SETTING    = 400;
	public static final int FUNCTION_ID_SETTING          = FUNCTION_CLASS_ID_SETTING + 1;
	
	// ע���˺ŵ�ַ
	public static final String REG_ADDRESS = "http://218.106.254.101:8099/register.aspx";
	
	// ���س�ʱ�������ƣ�Ĭ�����س�ʱ����������
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
