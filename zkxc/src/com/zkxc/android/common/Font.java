package com.zkxc.android.common;

import android.graphics.Typeface;
import android.os.Environment;

public class Font {

	public static Typeface GetHupo()
	{
		try {
			//return Typeface.createFromAsset (getAssets() , "fonts/hupo.ttf.png");
			return Typeface.createFromFile(Environment.getExternalStorageDirectory() + "/STHUPO.TTF");
		} catch (Exception exp) {
			return Typeface.defaultFromStyle(Typeface.BOLD);
		}
	}
}
