package com.zknx.hn.common;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

public class Label {
	/**
	 * ²úÉúTextView
	 * @return
	 */
	public static TextView Get(Context context, String text) {
		TextView tv = new TextView(context);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(18);
		return tv;
	}
}
