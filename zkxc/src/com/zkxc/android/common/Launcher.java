package com.zkxc.android.common;

import android.app.Activity;
import android.content.Intent;

public class Launcher 
{
	public static void StartActivity(Activity act, Class<?> actName)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.setClass(act, actName);
		act.startActivity(intent);
	}
}
