package com.zknx.hn.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Interface {

    // ����
    public static final String ZKNX_PACKAGE_NAME = "com.zknx.hn";
    // ����
    public static final String ENTRANCE_CLASS_NAME = "com.zknx.hn.Entrance";
    // ����key
    public static final String FUNCTION_KEY = "funtion";
    
    // �п�ũ����Ŀ
	public static final int FUNCTION_CLASS_ID_ZKNX       = 200;
	// �г�����
	public static final int FUNCTION_ID_MARKET           = FUNCTION_CLASS_ID_ZKNX + 1;
	// ��ѡ��Ʒ
	public static final int FUNCTION_ID_CUSTOM_PRODUCT   = FUNCTION_CLASS_ID_ZKNX + 2;
	// ������Ϣ
	public static final int FUNCTION_ID_SUPPLY_DEMAND    = FUNCTION_CLASS_ID_ZKNX + 3;
	// �ҵ���Ȧ
	public static final int FUNCTION_ID_MY_GROUP         = FUNCTION_CLASS_ID_ZKNX + 4;
	// ũҵ����
	public static final int FUNCTION_ID_ARGRI_TECH       = FUNCTION_CLASS_ID_ZKNX + 5;
	// ר��ָ��
	public static final int FUNCTION_ID_EXPERT_GUIDE     = FUNCTION_CLASS_ID_ZKNX + 6;
	// �ҵĹ���
	public static final int FUNCTION_ID_MY_SUPPLY_DEMAND = FUNCTION_CLASS_ID_ZKNX + 7;
	// ר��ʩ��
	public static final int FUNCTION_ID_EXPERT_FERTILIZE = FUNCTION_CLASS_ID_ZKNX + 8;

	// ���ǵ�����Ŀ
	public static final int FUNCTION_CLASS_ID_PARTY      = 300;
	// ʱ��Ҫ��
	public static final int FUNCTION_ID_CUR_POLITICS     = FUNCTION_CLASS_ID_PARTY + 1;
	// ��ѡ�μ�
	public static final int FUNCTION_ID_BEST_COUSE       = FUNCTION_CLASS_ID_PARTY + 2;
	// �ȷ浳Ա
	public static final int FUNCTION_ID_VANGUARD_PARTY   = FUNCTION_CLASS_ID_PARTY + 3;
	// ���;���
	public static final int FUNCTION_ID_CLASS_EXPERIENCE = FUNCTION_CLASS_ID_PARTY + 4;
	// �¸�ģ��
	public static final int FUNCTION_ID_MODEL            = FUNCTION_CLASS_ID_PARTY + 5;
	// ����ũ��
	public static final int FUNCTION_ID_HAPPAY           = FUNCTION_CLASS_ID_PARTY + 6;
	// ���ɷ���
	public static final int FUNCTION_ID_LAW              = FUNCTION_CLASS_ID_PARTY + 7;
	// ��ũ����
	public static final int FUNCTION_ID_POLICY           = FUNCTION_CLASS_ID_PARTY + 8;
	
	// ����
	public static final int FUNCTION_CLASS_ID_SETTING    = 400;
	public static final int FUNCTION_ID_SETTING          = FUNCTION_CLASS_ID_SETTING + 1;

    public static void StartZknxActivity(Context context, int function) {

    	Intent intent = new Intent();

    	intent.setClassName(ZKNX_PACKAGE_NAME, ENTRANCE_CLASS_NAME);
    	intent.putExtra(FUNCTION_KEY, function);

    	try {
    		context.startActivity(intent);
    	} catch (ActivityNotFoundException e) {
    		Toast.makeText(context, "�����п�ũ�Ŵ�������ϵ������Ա", Toast.LENGTH_LONG).show();
    	}
    }
}
