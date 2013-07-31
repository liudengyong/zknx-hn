package com.zkxc.android.act;

import com.zkxc.android.act.ActTable.Request;
import com.zkxc.android.common.Font;
import com.zkxc.android.common.Launcher;
import com.zkxc.android.common.MessageBox;
import com.zkxc.android.data.DataMan;
import com.zkxc.android.table.GridLayout;
import com.zkxc.android.table.GridLayout.CellTag;
import com.zkxc.android.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class ActHome extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*Debug();*/
        
        // 初始化下载超时
        AppZkxc.GetTimeout(this);
        
        if (!DataMan.InitDataFolder())
        {
        	Toast.makeText(this, R.string.failed_load_sd, Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.home);
        
        SetFont();
        
        SetListner();

        // 调试
        DataMan.Debug(this);
    	if (AppZkxc.mDebug) {
    		
    		// 拷贝默认html到sd卡
    		DataMan.Debug(this);
    		
    		// 默认登录到采集表页面
	        if (AppZkxc.mUserInfo == null || AppZkxc.mUserInfo.userId == null) {
		    	AppZkxc.mPenddingAct = Request.PENDDING_ACT_TABLE;
				Launcher.StartActivity(ActHome.this, ActLogon.class);
	        }
    	}
	}
    
	void Debug()
	{
		GridLayout mView = new GridLayout(this, null);
        
        // 设置 行和列数
        int rowCount = 8;
        int colCount = 8;
        
        TextView tv;
        TextView tv2;
        LinearLayout ly;
        
        for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex)
        {
        	for (int colIndex = 0; colIndex < colCount; ++colIndex)
        	{
        		ly = new LinearLayout(this);
        		ly.setOrientation(LinearLayout.VERTICAL);
        		ly.setBackgroundColor(Color.BLUE);
        		//ly.setGravity(Gravity.CENTER_HORIZONTAL);
        		
        		tv = new TextView(this);
                tv.setText("行" + rowIndex + "列" + colIndex);
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundColor(Color.LTGRAY);
                tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
                
                tv2 = new TextView(this);
                tv2.setText("行" + rowIndex + "列" + colIndex);
                tv2.setGravity(Gravity.CENTER);
                tv2.setBackgroundColor(Color.LTGRAY);
                tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
                
                if (rowIndex == 0 && colIndex == 0)
                {
	                CellTag cellTag = new CellTag(rowIndex, colIndex, 2, 2);
	                ly.setTag(cellTag);
                }
                else if (rowIndex == 1 && colIndex == 0)
                {
                	CellTag cellTag = new CellTag(rowIndex, colIndex, 0, 0);
                	ly.setTag(cellTag);
                }
                else if (rowIndex == 0 && colIndex == 1)
                {
                	CellTag cellTag = new CellTag(rowIndex, colIndex, 0, 0);
                	ly.setTag(cellTag);
                }
                else if (rowIndex == 1 && colIndex == 1)
                {
                	CellTag cellTag = new CellTag(rowIndex, colIndex, 0, 0);
                	ly.setTag(cellTag);
                }
                else
                {
                	CellTag cellTag = new CellTag(rowIndex, colIndex);
                    ly.setTag(cellTag);
                }
                //LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
            	
            	//param.gravity = Gravity.CENTER;
                ly.addView(tv);
                ly.addView(tv2);
                mView.addView(ly);
        	}
        }
        
        setContentView(mView);
	}
	
	void SetFont()
    {
    	Typeface face = Font.GetHupo();
    	
    	TextView tv = (TextView)findViewById(R.id.act_collection);
    	tv.setTypeface(face);
    	
    	tv = (TextView)findViewById(R.id.act_map);
    	tv.setTypeface(face);
    	
    	tv = (TextView)findViewById(R.id.act_protection_zone);
    	tv.setTypeface(face);
    	
    	tv = (TextView)findViewById(R.id.act_setting);
    	tv.setTypeface(face);
    }
    
    void SetListner()
    {
    	OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId())
				{
				case R.id.act_collection:
					if (AppZkxc.mUserInfo != null && AppZkxc.mUserInfo.userId != null)
						Launcher.StartActivity(ActHome.this, ActTable.class);
					else
					{
						AppZkxc.mPenddingAct = Request.PENDDING_ACT_TABLE;
						Launcher.StartActivity(ActHome.this, ActLogon.class);
					}

					break;
				case R.id.act_map:
					if (AppZkxc.mUserInfo != null && AppZkxc.mUserInfo.userId != null)
						Launcher.StartActivity(ActHome.this, ActMapTrack.class);
					else
					{
						AppZkxc.mPenddingAct = Request.PENDDING_ACT_TRACK;
						Launcher.StartActivity(ActHome.this, ActLogon.class);
					}
					break;
				case R.id.act_protection_zone:
					Launcher.StartActivity(ActHome.this, ActProtectionZone.class);
					break;
				case R.id.act_setting:
					Launcher.StartActivity(ActHome.this, ActSetting.class);
					break;
				}
			}
    	};
    	
    	TextView tv = (TextView)this.findViewById(R.id.act_collection);
		tv.setOnClickListener(l);
    	
    	tv = (TextView)findViewById(R.id.act_map);
    	tv.setOnClickListener(l);
    	
    	tv = (TextView)findViewById(R.id.act_protection_zone);
    	tv.setOnClickListener(l);
    	
    	tv = (TextView)findViewById(R.id.act_setting);
    	tv.setOnClickListener(l);
    }
    
    void backPressed()
    {
    	AppZkxc.mUserInfo = null;
    	
    	super.onBackPressed();
    }
    
    @Override
    public void onBackPressed()
    {
    	DialogInterface.OnClickListener yesListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				backPressed();
			}
    	};
    	
    	MessageBox.YesOrNo(this, "确认退出系统吗？", yesListener);
    }
}
