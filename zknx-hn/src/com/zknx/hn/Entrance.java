package com.zknx.hn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Entrance extends Activity {
	
	private static final String KEY_FUNCTION = "funtion";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        if (intent != null) {
        	Bundle extras = intent.getExtras();
        	int function = extras.getInt(KEY_FUNCTION);
        	
        	// 检查功能id正确后启动
        	if (function > 0) {
        		Function.StartFunctionActivity(this, function);
        		finish();
        		return;
        	}
        }

        Toast.makeText(this, "中科农信启动错误：参数无效", Toast.LENGTH_LONG).show();

        finish();
	}
}
