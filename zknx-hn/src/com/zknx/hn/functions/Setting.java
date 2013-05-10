package com.zknx.hn.functions;

import com.zknx.hn.App;
import com.zknx.hn.R;
import com.zknx.hn.functions.common.FunctionView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Setting extends FunctionView {

	static String KEY_TIMEOUT = "timeout";
	
	public final static int DEFAULT_TIMEOUT = 20;
	
	public Setting(LayoutInflater inflater, LinearLayout frameRoot, int frameResId) {
		super(inflater, frameRoot, frameResId);

		initContent("通用", initView(), mContentFrame[0]);
		
		initContent("中科农信", null, mContentFrame[1]);
	}
	
    private LinearLayout initView() {
        
        LinearLayout settingLayout = (LinearLayout)mInflater.inflate(R.layout.setting, null);
        
        EditText editText = (EditText)settingLayout.findViewById(R.id.edit_setting_timeout);
        
        editText.setText(Integer.toString(GetTimeOut(mContext)));
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
            			setTimeout(mContext, Integer.parseInt(s.toString()));
            		}
            		// 防止格式化错误
            		catch (Exception e)
            		{
            			e.printStackTrace();
            		}
                }
            }   
        });
        
        return settingLayout;
	}
    
    static SharedPreferences GetPerf(Context context)
    {
    	return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    static Editor GetPerfEditor(Context context)
    {
    	return GetPerf(context).edit();
    }

	public static int GetTimeOut(Context context) 
	{
		return GetPerf(context).getInt(KEY_TIMEOUT, DEFAULT_TIMEOUT);
	}
	
	static void setTimeout(Context context, int timeout)
	{
		App.SetTimeout(timeout);

		Editor ed = GetPerfEditor(context);
		ed.putInt(KEY_TIMEOUT, timeout);
		ed.commit();
	}
}