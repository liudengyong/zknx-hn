package com.zknx.hn.common;

import com.zknx.hn.data.DataMan;

import android.content.Context;
import android.graphics.Typeface;

public class Font {

	static Typeface mFontHupo = null;
	
	public static Typeface GetHupo(Context context)
	{
		if (mFontHupo == null) {
			try {
				//mFontHupo = Typeface.createFromAsset (context.getAssets() , "STHUPO.TTF");
				mFontHupo = Typeface.createFromFile(DataMan.DataFile("fonts/STHUPO.TTF"));
			} catch (Exception exp) {
				return Typeface.defaultFromStyle(Typeface.BOLD);
			}
		}
		
		return mFontHupo;
	}
}
