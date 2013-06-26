package com.zknx.hn.functions.ais;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.common.widget.Checkbox;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.common.widget.Label;
import com.zknx.hn.data.DataMan;

public class CourseView {

	protected final static char[] ANWSERS = {'A', 'B', 'C', 'D'};
	
	private final static int ROW_PADDING_LEFT = 12;

	/* 初始化非webview 视图 */
	public static void InitView(LayoutInflater inflater, LinearLayout contentLayout, AisDoc aisDoc) {
		// 一个题目两行
		
		Context context = contentLayout.getContext();
		
		int total = 0;
		int count = aisDoc.getQuestionCount();
		for (int i = 0; i < count; ++i) {
			total += aisDoc.getQuestionGrade(i);
		}
		
		LinearLayout courseLayout = (LinearLayout) inflater.inflate(R.layout.course_view, null);
		
		courseLayout.setTag("" + count);
		
		courseLayout.findViewById(R.id.course_view_result).setVisibility(View.GONE);
		((TextView)courseLayout.findViewById(R.id.course_view_total)).setText("总分：" + total + "分");
		
		for (int i = 0; i < count; ++i) {
			
			// 题目题干==============================
			LinearLayout row1 = new LinearLayout(context);
			//row1.setOrientation(HORIZONTAL); 默认水平排版
			row1.addView(Label.Get(context, (i + 1 ) + "."));
			
			// 答案
			row1.setTag(R.string.key_anwser, aisDoc.getQuestionAnswer(i));
			
			ImageView image = new ImageView(context);
			byte[] data = aisDoc.getQuestionBitmapData(i);
			if (data == null)
				continue;
			
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			image.setImageBitmap(bm);
			
			row1.addView(image);

			// 各题目间隔
			if (i != 0) {
				row1.setPadding(0, 20, 0, 0);
			}

			courseLayout.addView(row1, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
			// 题目题干==============================
			
			// 题目答题==============================
			LinearLayout row2 = new LinearLayout(context);

			LinearLayout.LayoutParams params = UIConst.GetLayoutParams(L_LAYOUT_TYPE.WRAP);
			params.gravity = Gravity.CENTER;
			
			String tagAnswer = "答题(" + aisDoc.getQuestionGrade(i) + "分)：";
			row2.addView(Label.Get(context, tagAnswer), params);
			row2.setPadding(ROW_PADDING_LEFT, 0, 0, 0);

			for (char anwser : ANWSERS) {
				Checkbox checkbox = new Checkbox(context, "" + anwser);
				checkbox.setId(GetAnwserId(i, anwser));
				//checkbox.setButtonDrawable(R.drawable.checkbox);
				row2.addView(checkbox);
			}
			
			ImageView resultIcon = new ImageView(context);
			//resultIcon.setImageResource(R.drawable.course_crect);
			resultIcon.setId(GetAnwserIconId(i));
			String rightAnwser = aisDoc.getQuestionAnswer(i);
			resultIcon.setTag(R.string.key_anwser, rightAnwser);
			resultIcon.setTag(R.string.key_anwser_grade, "" + aisDoc.getQuestionGrade(i));
			resultIcon.setScaleType(ScaleType.CENTER);
			resultIcon.setVisibility(View.GONE);
			
			row2.addView(resultIcon, UIConst.GetLayoutParams(L_LAYOUT_TYPE.W_WRAP));
			
			courseLayout.addView(row2);
			// 题目答题==============================
			
			// 题目解析==============================
			LinearLayout row3 = new LinearLayout(context);
			TextView note = Label.Get(context, "解析：" + aisDoc.getQuestionNote(i));
			note.setId(GetNoteId(i));
			note.setVisibility(View.GONE);
			row3.addView(note);
			row3.setPadding(ROW_PADDING_LEFT, 0, 0, 0);
			courseLayout.addView(row3);
			// 题目解析==============================
		}
		
		contentLayout.addView(courseLayout);
	}
	
	/**
	 * 答案id
	 */
	private static int GetAnwserId(int i, char anwser) {
		int index = anwser - ANWSERS[0];
		return (1000 + (i * 100) + index);
	}
	
	/**
	 * 答案图标id
	 */
	private static int GetAnwserIconId(int i) {
		return (2000 + i);
	}
	
	/**
	 * 解析id
	 */
	private static int GetNoteId(int i) {
		return (3000 + i);
	}
	
	/**
	 * 交卷和重做公用代码
	 * @param button
	 * @param reset
	 */
	public static void SubmitOrReset(View button, boolean reset) {
		LinearLayout courseLayout = GetCourseLayout(button);
		
		int count = 0;
		Object tag = courseLayout.getTag();
		if (tag == null || (count = DataMan.ParseInt(tag.toString())) <= 0) {
			Debug.Log("严重错误： courseLayout.getTag");
			return;
		}
		
		int resultPoint = 0;
		for (int i = 0; i < count; ++i) {
			// 答案图标
			View view = courseLayout.findViewById(GetAnwserIconId(i));
			if (view == null) {
				Debug.Log("严重错误： courseLayout.findViewById(GetAnwserIconId(i))");
				return;
			}
			
			if (reset) {
				view.setVisibility(View.GONE);
				
				for (char anwser : ANWSERS) {
					View checkbox = courseLayout.findViewById(GetAnwserId(i, anwser));
					if (checkbox != null)
						checkbox.setEnabled(true);
				}
			}
			else {
				
				// 答案tag
				tag = view.getTag(R.string.key_anwser);
				
				boolean checkedResult = true;
				for (char anwser : ANWSERS) {
					Checkbox checkbox = (Checkbox) courseLayout.findViewById(GetAnwserId(i, anwser));
					if (checkbox == null) {
						Debug.Log("严重错误： courseLayout.findViewById(GetAnwserId(i))");
						return;
					}
					
					boolean contains = tag.toString().contains("" + anwser);
					
					checkbox.setEnabled(false);
					
					// 是答案，选中了，或者不是答案，没有选
					if (contains && checkbox.isChecked() ||
						!contains && !checkbox.isChecked()) {
						continue;
					} else {
						checkedResult = false;
						break;
					}
				}
				
				if (checkedResult) {
					tag = view.getTag(R.string.key_anwser_grade);
					int rightPoint = DataMan.ParseInt(tag.toString());
					if (rightPoint == DataMan.INVALID_ID) {
						Debug.Log("严重错误：checkbox.getTag()");
						return;
					}
					
					resultPoint += rightPoint;
					
					((ImageView)view).setImageResource(R.drawable.course_crect);
				}
				else
					((ImageView)view).setImageResource(R.drawable.course_increct);
				
				view.setVisibility(View.VISIBLE);
			}
			
			TextView result = (TextView) courseLayout.findViewById(R.id.course_view_result);
			if (reset) 
				result.setVisibility(View.GONE);
			else {
				result.setText("得分：" + resultPoint + "分");
				if (resultPoint == 0)
					result.setTextColor(Color.RED);
				else
					result.setTextColor(Color.WHITE);
				result.setVisibility(View.VISIBLE);
			}
			
			// 解析
			view = courseLayout.findViewById(GetNoteId(i));
			
			if (view == null) {
				Debug.Log("严重错误： courseLayout.findViewById(GetNoteId(i))");
				continue;
			}
			
			if (reset) 
				view.setVisibility(View.GONE);
			else
				view.setVisibility(View.VISIBLE);
		}
		
		if (!reset) {
			Dialog.MessageBox(button.getContext(), "您本次得分：" + resultPoint + "分");
		}
	}
	
	/**
	 * 从交卷和重做按钮查找course视图
	 * @param button
	 * @return
	 */
	private static LinearLayout GetCourseLayout(View button) {
		
		RelativeLayout root = (RelativeLayout) button.getParent().getParent().getParent();

		return (LinearLayout)root.findViewById(R.id.course_view_layout);
	}
}
