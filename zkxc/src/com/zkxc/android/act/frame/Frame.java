package com.zkxc.android.act.frame;

import com.zkxc.android.act.ActHome;
import com.zkxc.android.act.ActLogon;
import com.zkxc.android.act.ActMapTrack;
import com.zkxc.android.act.ActProtectionZone;
import com.zkxc.android.act.ActSetting;
import com.zkxc.android.act.ActTable;
import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.act.ActTable.Request;
import com.zkxc.android.common.Font;
import com.zkxc.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Frame {

	static Activity mActivity;
	
	public static void InitFrame(Activity act, String title_sec)
	{
		mActivity = act;
		
		SetHomeBtnListner(act);
		SetTitle(act, title_sec);
	}
	
	private static void SetHomeBtnListner(final Activity act) {
		final Class<?>[] activityClass = new Class<?>[3];
		
		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(final View view) 
			{
				// 轨迹采集时切换时提示确认
				if (Frame.mActivity instanceof ActMapTrack)
				{
					final ActMapTrack mapTrack = (ActMapTrack)Frame.mActivity;
					// 是否在记录轨迹
					if (mapTrack.isRecording())
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(Frame.mActivity);
						builder.setMessage("当前处于记录轨迹的状态，确认要停止记录吗？")
						.setCancelable(false)
						.setPositiveButton("停止记录", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mapTrack.stopRecord();
								action(activityClass, view);
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
						return;
					}
				}
				
				action(activityClass, view);
			}
		};

		Typeface face = Font.GetHupo();
		
		Button btn = (Button)act.findViewById(R.id.btn_home);
		btn.setOnClickListener(l);
		btn.setTypeface(face);

		if (act instanceof ActTable)
		{
			activityClass[0] = ActMapTrack.class;
			activityClass[1] = ActProtectionZone.class;
			activityClass[2] = ActSetting.class;
			
			btn = (Button)act.findViewById(R.id.btn_act_1);
			btn.setOnClickListener(l);
			btn.setText(R.string.map_track);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_2);
			btn.setOnClickListener(l);
			btn.setText(R.string.nature_protection_zone);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_3);
			btn.setOnClickListener(l);
			btn.setText(R.string.system_tool);
			btn.setTypeface(face);
		}
		else if (act instanceof ActMapTrack)
		{
			activityClass[0] = ActTable.class;
			activityClass[1] = ActProtectionZone.class;
			activityClass[2] = ActSetting.class;
			
			btn = (Button)act.findViewById(R.id.btn_act_1);
			btn.setOnClickListener(l);
			btn.setText(R.string.data_colleection);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_2);
			btn.setOnClickListener(l);
			btn.setText(R.string.nature_protection_zone);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_3);
			btn.setOnClickListener(l);
			btn.setText(R.string.system_tool);
			btn.setTypeface(face);
		}
		else if (act instanceof ActProtectionZone)
		{
			activityClass[0] = ActTable.class;
			activityClass[1] = ActMapTrack.class;
			activityClass[2] = ActSetting.class;
			
			btn = (Button)act.findViewById(R.id.btn_act_1);
			btn.setOnClickListener(l);
			btn.setText(R.string.data_colleection);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_2);
			btn.setOnClickListener(l);
			btn.setText(R.string.map_track);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_3);
			btn.setOnClickListener(l);
			btn.setText(R.string.system_tool);
			btn.setTypeface(face);
		} 
		else if (act instanceof ActSetting)
		{
			activityClass[0] = ActTable.class;
			activityClass[1] = ActMapTrack.class;
			activityClass[2] = ActProtectionZone.class;
			
			btn = (Button)act.findViewById(R.id.btn_act_1);
			btn.setOnClickListener(l);
			btn.setText(R.string.data_colleection);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_2);
			btn.setOnClickListener(l);
			btn.setText(R.string.map_track);
			btn.setTypeface(face);
			
			btn = (Button)act.findViewById(R.id.btn_act_3);
			btn.setOnClickListener(l);
			btn.setText(R.string.nature_protection_zone);
			btn.setTypeface(face);
		}
	}
	
	static void action(Class<?>[] activityClass, View view)
	{
		Class<?> targetActivityClass = null;
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.btn_home:
			targetActivityClass = ActHome.class;
		break;
		case R.id.btn_act_1:
			targetActivityClass = activityClass[0];
			break;
		case R.id.btn_act_2:
			targetActivityClass = activityClass[1];
			break;
		case R.id.btn_act_3:
			targetActivityClass = activityClass[2];
			break;
		}
		
		if (targetActivityClass != null)
		{
			intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			
			// 是否需要登录
			if (AppZkxc.mUserInfo == null || AppZkxc.mUserInfo.userId == null)
			{
				// 只有采集表和体地图需要登录
				if (targetActivityClass == ActTable.class)
				{
					targetActivityClass = ActLogon.class;
					AppZkxc.mPenddingAct = Request.PENDDING_ACT_TABLE;
				}
				else if (targetActivityClass == ActMapTrack.class)
				{
					targetActivityClass = ActLogon.class;
					AppZkxc.mPenddingAct = Request.PENDDING_ACT_TRACK;
				}
			}

			intent.setClass(Frame.mActivity, targetActivityClass);
			Frame.mActivity.startActivity(intent);
			
			// 登录时不销毁当前活动
			if (targetActivityClass != ActLogon.class)
				Frame.mActivity.finish();
		}
	}

	private static void SetTitle(Activity act, String title_sec) 
	{
		Typeface face = Font.GetHupo();
		TextView tv = (TextView)act.findViewById(R.id.tx_title_second);

		tv.setTypeface(face);
		tv.setText(title_sec);

		tv = (TextView)act.findViewById(R.id.tx_title);
		tv.setTypeface(face);
	}
}
