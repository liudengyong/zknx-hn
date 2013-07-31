package com.zkxc.android.act;

import com.zkxc.android.act.frame.Frame;
import com.zkxc.android.common.Debug;
import com.zkxc.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class ActSetting extends Activity {

	CheckBox mOpenLast;
	
	static String KEY_IS_OPEN_LAST_TAB = "open_last";
	static String KEY_LAST_TAB_ID = "last_tab_id";
	static String KEY_LAST_TAB_NAME = "last_tab_name";
	
	static String KEY_TIMEOUT = "timeout";
	
	public final static int DEFAULT_TIMEOUT = 20;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.setting);
        
        Frame.InitFrame(this, getString(R.string.system_tool));
        
        mOpenLast = (CheckBox)findViewById(R.id.ckb_open_last);
        mOpenLast.setChecked(IsOpenLastTab(this));
        
        mOpenLast.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean checked) {
				setOpenLastTab(ActSetting.this, checked);
			}
        });
        
        EditText editText = (EditText)findViewById(R.id.edit_setting_timeout);
        
        editText.setText(Integer.toString(GetTimeOut(this)));
        editText.addTextChangedListener(new TextWatcher() {   
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {   
            }   
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,   
                    int after) {   
            }   
            @Override  
            public void afterTextChanged(Editable s) {
            	if (s != null && s.length() > 0)
                {
            		try {
            			setTimeout(ActSetting.this, Integer.parseInt(s.toString()));
            		}
            		// 防止格式化错误
            		catch (Exception e)
            		{
            			e.printStackTrace();
            		}
                }
            }   
        });
	}
    
    static SharedPreferences GetPerf(Context context)
    {
    	return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    static Editor GetPerfEditor(Context context)
    {
    	return GetPerf(context).edit();
    }

	public static boolean IsOpenLastTab(Context context) 
	{
		return GetPerf(context).getBoolean(KEY_IS_OPEN_LAST_TAB, false);
	}
	
	public static int GetTimeOut(Context context) 
	{
		return GetPerf(context).getInt(KEY_TIMEOUT, DEFAULT_TIMEOUT);
	}
	
	static void setOpenLastTab(Context context, boolean checked)
	{
		Editor ed = GetPerfEditor(context);
		ed.putBoolean(KEY_IS_OPEN_LAST_TAB, checked);
		ed.commit();
	}
	
	static void setTimeout(Context context, int timeout)
	{
		Debug.Log("设置下载超时 ： " + timeout);

		AppZkxc.mTimeout = timeout;

		Editor ed = GetPerfEditor(context);
		ed.putInt(KEY_TIMEOUT, timeout);
		ed.commit();
	}

	public static String GetLastTabId(Context context)
	{
		return GetPerf(context).getString(KEY_LAST_TAB_ID, null);
	}
	
	public static String GetLastTabName(Context context)
	{
		return GetPerf(context).getString(KEY_LAST_TAB_NAME, null);
	}
	
	public static void SetLastTab(Context context, String tabId, String tabName)
	{
		Editor ed = GetPerfEditor(context);
		ed.putString(KEY_LAST_TAB_ID, tabId);
		ed.putString(KEY_LAST_TAB_NAME, tabName);
		ed.commit();
	}
}