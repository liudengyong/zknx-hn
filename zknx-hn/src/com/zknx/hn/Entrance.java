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
        	
        	// ��鹦��id��ȷ������
        	if (function > 0) {
        		Function.StartFunctionActivity(this, function);
        		finish();
        		return;
        	}
        }

        Toast.makeText(this, "�п�ũ���������󣺲�����Ч", Toast.LENGTH_LONG).show();

        finish();
	}
}
