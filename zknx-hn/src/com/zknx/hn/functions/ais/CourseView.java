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
	 * ����TextView
	 * @return
	 */
	private static TextView Label(Context context, String text) {
		TextView tv = new TextView(context);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(18);
		return tv;
	}

	/* ��ʼ����webview ��ͼ */
	public static void InitView(LinearLayout contentLayout, AisDoc aisDoc) {
		// һ����Ŀ����
		
		Context context = contentLayout.getContext();
		
		for (int i = 0; i < aisDoc.getQuestionCount(); ++i) {
			
			// ��Ŀ���==============================
			LinearLayout row1 = new LinearLayout(context);
			//row1.setOrientation(HORIZONTAL); Ĭ��ˮƽ�Ű�
			row1.addView(Label(context, (i + 1 ) + "."));
			
			// ��
			row1.setTag(KEY_ANWSER, aisDoc.getQuestionAnswer(i));
			
			ImageView image = new ImageView(context);
			byte[] data = aisDoc.getQuestionBitmapData(i);
			if (data == null)
				continue;
			
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			image.setImageBitmap(bm);
			
			row1.addView(image);

			contentLayout.addView(row1, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			// ��Ŀ���==============================
			
			// ��Ŀ����==============================
			LinearLayout row2 = new LinearLayout(context);

			String tagAnswer = "  ����(" + aisDoc.getQuestionGrade(i) + "��)��";
			row2.addView(Label(context, tagAnswer));

			for (char anwser : ANWSERS) {
				row2.addView(Label(context, "" + anwser));

				CheckBox checkbox = new CheckBox(context);
				checkbox.setId((i * 100) + anwser);
				//checkbox.setButtonDrawable(R.drawable.checkbox);
				row2.addView(checkbox);
			}

			contentLayout.addView(row2, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			// ��Ŀ����==============================
		}
	}
}
