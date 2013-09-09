package com.zknx.hn.home;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
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

	public static Bundle GetExtras(int id) {
		return InitExtras(id);
	}

	public static String GetTitle(Bundle extras) {
		return extras.getString(KEY_TITLE);
	}

	/**
	 * ��ȡ����id
	 * @param extras
	 * @return
	 */
	public static int GetFunction(Bundle extras) {
		return extras.getInt(KEY_FUNCTION_ID);
	}
	
	/**
	 * ��ȡ���ܷ��࣬�п�ũ�Ż��ߺ��ǵ���
	 * @param extras
	 * @return
	 */
	public static int GetFunctionClass(Bundle extras) {
		return extras.getInt(KEY_FUNCTION_CLASS);
	}

	/**
	 * ��ȡ��ͼ��ʵ�������ڳ�ʼ��������ͼ
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
		// ������ʾ
		case UIConst.FUNCTION_ID_MODEL:
		case UIConst.FUNCTION_ID_VANGUARD_PARTY:
		case UIConst.FUNCTION_ID_EXPERT_FERTILIZE:
		case UIConst.FUNCTION_ID_BEST_COUSE: // ��ѡ�μ�
			return new AisView(inflater, frameRoot, function_id, R.layout.func_frame_split);
		// ������ʾ
		case UIConst.FUNCTION_ID_POLICY:
		case UIConst.FUNCTION_ID_ARGRI_TECH:
		case UIConst.FUNCTION_ID_CUR_POLITICS:
		case UIConst.FUNCTION_ID_CLASS_EXPERIENCE:
		case UIConst.FUNCTION_ID_HAPPAY:
		case UIConst.FUNCTION_ID_LAW:
			return new AisView(inflater, frameRoot, function_id, R.layout.func_frame_triple);
		case UIConst.FUNCTION_ID_EXPERT_GUIDE: // ר��ָ��
			return new Expert(inflater, frameRoot);
	    // ==== ����Ӧ�� ====
		case UIConst.FUNCTION_ID_PARTRY_OPEN:
		case UIConst.FUNCTION_ID_COUNTRY_POLICY:
		case UIConst.FUNCTION_ID_VILLIGE_OPEN:
		case UIConst.FUNCTION_ID_FINANCIAL_OPEN:
		case UIConst.FUNCTION_ID_BIRTH_CONTROL_OPEN:
		case UIConst.FUNCTION_ID_WORK_OPEN:
		case UIConst.FUNCTION_ID_WORK_INTRO:
		case UIConst.FUNCTION_ID_WORK_INFO:
		// ==== ����Ӧ�� ====
			return new AisView(inflater, frameRoot, function_id, R.layout.func_frame_split);
		default:
			return null; // ���ش���
		}
	}
	
	/**
	 * ��ʼ������
	 * @param id
	 * @return
	 */
	private static Bundle InitExtras(int id) {
		Bundle extras = new Bundle();
		
		int index   = (id % 100) - 1;   // ȡ������һ
		int classId = (id / 100) * 100; // ȡ��λ����
		
		// ����ID
		extras.putInt(KEY_FUNCTION_ID, id);
		// ������Ŀ
		extras.putInt(KEY_FUNCTION_CLASS, classId);

		switch (classId) {
		case UIConst.FUNCTION_CLASS_ID_ZKNX:
			extras.putString(KEY_TITLE, UIConst.FUNCTIONS_ZKNX[index]);
			break;
		case UIConst.FUNCTION_CLASS_ID_PARTY:
			extras.putString(KEY_TITLE, UIConst.FUNCTIONS_PARTY[index]);
			break;
		case UIConst.FUNCTION_CLASS_ID_POLICY:
			extras.putString(KEY_TITLE, UIConst.FUNCTIONS_POLICY[index]);
			break;
		default:
			Debug.Log("��������������function id " + id);
			break;
		}

		return extras;
	}
}
