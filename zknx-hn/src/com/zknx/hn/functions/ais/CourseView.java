package com.zknx.hn.functions.ais;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;

public class CourseView {

	protected final static char[] ANWSERS = {'A', 'B', 'C', 'D'};
	private static final int KEY_ANWSER = 1;

	/**
	 * 产生TextView
	 * @return
	 */
	private static TextView Label(Context context, String text) {
		TextView tv = new TextView(context);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(18);
		return tv;
	}

	/* 初始化非webview 视图 */
	public static void InitView(LinearLayout contentLayout, AisDoc aisDoc) {
		// 一个题目两行
		
		Context context = contentLayout.getContext();
		
		for (int i = 0; i < aisDoc.getQuestionCount(); ++i) {
			
			// 题目题干==============================
			LinearLayout row1 = new LinearLayout(context);
			//row1.setOrientation(HORIZONTAL); 默认水平排版
			row1.addView(Label(context, (i + 1 ) + "."));
			
			// 答案
			row1.setTag(KEY_ANWSER, aisDoc.getQuestionAnswer(i));
			
			ImageView image = new ImageView(context);
			byte[] data = aisDoc.getQuestionBitmapData(i);
			if (data == null)
				continue;
			
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			image.setImageBitmap(bm);
			
			row1.addView(image);

			contentLayout.addView(row1, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			// 题目题干==============================
			
			// 题目答题==============================
			LinearLayout row2 = new LinearLayout(context);

			String tagAnswer = "  答题(" + aisDoc.getQuestionGrade(i) + "分)：";
			row2.addView(Label(context, tagAnswer));

			for (char anwser : ANWSERS) {
				row2.addView(Label(context, "" + anwser));

				CheckBox checkbox = new CheckBox(context);
				checkbox.setId((i * 100) + anwser);
				//checkbox.setButtonDrawable(R.drawable.checkbox);
				row2.addView(checkbox);
			}

			contentLayout.addView(row2, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			// 题目答题==============================
		}
	}
}
