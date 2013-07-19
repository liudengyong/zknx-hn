package com.zknx.hn;

import java.util.List;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;
import com.zknx.hn.common.UIConst;
import com.zknx.hn.common.UIConst.L_LAYOUT_TYPE;
import com.zknx.hn.common.widget.Dialog;
import com.zknx.hn.common.widget.WaitDialog;
import com.zknx.hn.common.widget.Dialog.ConfirmListener;
import com.zknx.hn.common.widget.WaitDialog.WaitListener;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.UserMan;
import com.zknx.hn.functions.common.FunctionView;
import com.zknx.hn.home.Functions;
import com.zknx.hn.home.Params;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Function extends Activity {

	FunctionView mFunctionView;

	// 更新数据等待进度条
	private WaitDialog mWaitDialog;
	// 更新数据完成消息
	private static final int MESSAGE_UPDATE_DATA_COMPLETE = 1;
	
	private int mFunctionId = DataMan.INVALID_ID;
	private Intent mSavedIntent;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FunctionInstance = this;
        
        mSavedIntent = getIntent();

        initFunctionView(getIntent());

        initAppStatic();

        // 避免重复启动
        // FIXME 服务 unbindService
        if (mDataService == null) {
        	bindService(new Intent(this, DataService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

        if (DataMan.ShouldUpdateData()) {
        	// 创建等待进度条
        	mWaitDialog = WaitDialog.Show(this, null, "正在更新数据", new WaitListener() {
				/**
				 * 开始登录
				 */
				@Override
				public void startWait() {
					DataMan.UpdateTodayData();
					mHandler.sendEmptyMessage(MESSAGE_UPDATE_DATA_COMPLETE);
				}
			});
        }
	}
	
	// 为静态Handler保存实例
	private static Function FunctionInstance;
	private static Handler mHandler = new Handler() {
	    /**
	     * 实现消息处理
	     */
	    @Override
	    public void handleMessage(Message msg) {
	    	FunctionInstance.processWaitMessage(msg.what);
	    }
	};

	/**
     * 后台数据服务
     * @param id
     */
    private static DataService mDataService;
    
    // 只有一个静态连接实例存在
    private static ServiceConnection mConnection = new ServiceConnection() {
    	@Override
    	public void onServiceConnected(ComponentName name, IBinder service) {
    		mDataService = ((DataService.DataBinder)service).getService();
    		mDataService.initHandler(mHandler);
    		mDataService.processBroadcastData();
    		mDataService.getNewMessage();
    	}
    	@Override
    	public void onServiceDisconnected(ComponentName name) {
    		mDataService = null;	
    	}
    };
	
	/**
	 * 初始化全局变量
	 */
	private void initAppStatic() {
		App.mContext = getApplicationContext();
	}

	// 处理更新数据完成消息
	protected void processWaitMessage(int what) {
		switch (what) {
		case MESSAGE_UPDATE_DATA_COMPLETE:
		{
			// 隐藏等待进度条
			mWaitDialog.dismiss();
			// 重新更新
			initFunctionView(getIntent());
		}
		break;
		case DataService.MESSAGE_NEW_DATA:
		{
			// 提示是否立即更新数据
			Dialog.Confirm(this, R.string.new_data_tip, new ConfirmListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					initFunctionView(getIntent());
				}
			});
		}
		break;
		case DataService.MESSAGE_NEW_MESSAGE:
		{
			List<RunningAppProcessInfo> appProcesses = ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses(); 
			if (appProcesses != null) {
				for (RunningAppProcessInfo appProcess : appProcesses)
					// The name of the process that this object is associated with.
					if (appProcess.processName.equals("com.zknx.hn") &&
						appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						return;
					}
			}

			showNotification("收到新留言");
		}
		break;
		}
	}
	
	/**
	  * 在状态栏显示通知
	  */
	 private void showNotification(String message){

	     // 定义Notification的各种属性  
	     Notification notification = new Notification(R.drawable.icon_back,  
	    		 message, System.currentTimeMillis());

	     notification.flags |= Notification.FLAG_SHOW_LIGHTS;  
	     notification.defaults = Notification.DEFAULT_LIGHTS;

	     CharSequence contentTitle ="中科农信"; // 通知栏标题  
	     CharSequence contentText = message; // 通知栏内容  

	     PendingIntent contentItent = PendingIntent.getActivity(this, 0, mSavedIntent, 0);  

	     notification.setLatestEventInfo(this, contentTitle, contentText, contentItent);  

	     // 把Notification传递给NotificationManager  
	     ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(0, notification);  
	 }

	 //删除通知   
	 private void clearNotification() {
	     // 启动后删除之前我们定义的通知  
		 ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(0); 
	}

	/***
	 * 切换功能时调用
	 * */
	@Override  
    protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		
		initFunctionView(intent);
    }
	
	/***
	 * 初始化所有界面
	 */
	private void initFunctionView(Intent intent) {
		
		if (intent != null) {
			
			setContentView(R.layout.activity_function);
			
			Bundle extras = intent.getExtras();
	        
	        intReturnHomeBtn();
	        
	        setTitle(extras);
	        
	        intiFrame(extras);
	        
	        intiBottomBtns(extras);
		}
	}

	/**
	 * 初始化返回按钮
	 * */
	void intReturnHomeBtn() {
		ImageButton returnHome = (ImageButton)findViewById(R.id.function_btn_return_home);
		returnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
				/* XXX 当前页面退出 ，跳转到主页？*/
				/*
				Intent intent = new Intent(Function.this, Home.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				*/
			}
		});
	}
	
	/**
	 * 设置标题
	 * */
	void setTitle(Bundle extras) {
		
		TextView title = (TextView)findViewById(R.id.function_title_text);
		
		title.setText(Params.GetTitle(extras));
	}
	
	/**
	 * 初始化功能框架
	 * */
	void intiFrame(Bundle extras) {
		
		LinearLayout frameRoot = (LinearLayout)findViewById(R.id.function_frame_content);
		LayoutInflater inflater = getLayoutInflater();

		mFunctionId = Params.GetFunction(extras);
		
		// 清除通知栏消息
		if (mFunctionId == UIConst.FUNCTION_ID_MY_GROUP)
			clearNotification();

		// 初始化功能视图
		mFunctionView = Params.GetFunctionView(mFunctionId, inflater, frameRoot);
	}
	
	/**
	 * 初始化底部按钮
	 * */
	void intiBottomBtns(Bundle extras) {
		
		LinearLayout bottomBtns = (LinearLayout)findViewById(R.id.function_frame_btns);
		LayoutInflater inflater = getLayoutInflater();
		int functionId = Params.GetFunction(extras);
		int functionClass = Params.GetFunctionClass(extras);
		
		LinearLayout btns = null;
		List<LinearLayout> mFunctionList = Functions.GetSubFunctionList(inflater, mOnClickListener);
		
		if (functionClass == Params.FUNCTION_CLASS_ZKNX)
			btns = mFunctionList.get(0);
		else if (functionClass == Params.FUNCTION_CLASS_PARTY)
			btns = mFunctionList.get(1);
		else {
			// 设置
			bottomBtns.setVisibility(View.GONE);
			return;
		}
		
		// 中科农信或者红星党建
		for (int i = 0; i < btns.getChildCount(); ++i) {
			RelativeLayout btnLayout = (RelativeLayout)btns.getChildAt(i);
			View btn = btnLayout.getChildAt(0);
			if (functionId == btn.getId()) {
				btn.setBackgroundResource(R.drawable.bottom_button_focus);
				btn.setEnabled(false); // 设置当前功能按钮背景，并禁用
			}
		}

		// 初始化功能视图
		bottomBtns.addView(btns, UIConst.GetLayoutParams(L_LAYOUT_TYPE.H_WRAP));
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			StartFunctionActivity(Function.this, view.getId());
		}
    };
    
    public static void StartFunctionActivity(Context context, int id) {

    	Bundle extras = Params.GetExtras(id);
    	if (extras != null) {
	    	Intent intent = new Intent();

	    	// 功能参数
	    	intent.replaceExtras(extras);
	    	intent.setClass(context, Function.class);

	    	if (isNeedLogin(id)) {
	    		//intent.setClass(context, Login.class);
	    		UserMan.SetUserInfo("userId", "userName", "addrId", "address", "phone");
	    	}
	    	
	    	context.startActivity(intent);
	    	//overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	    	//overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    	} else {
    		Debug.Log("严重错误:startSubActivity");
    	}
    }

    // XXX 确定哪些功能需要登录
    private static boolean isNeedLogin(int id) {

    	// 检查登录状态，如果已经登录则无需重复
    	if (UserMan.GetUserId() != null)
    		return false;

    	switch (id) {
    	case UIConst.FUNCTION_ID_MY_GROUP:
    	case UIConst.FUNCTION_ID_CUSTOM_PRODUCT:
    	case UIConst.FUNCTION_ID_MY_SUPPLY_DEMAND:
    		return true;
    	}
    	
    	return false;
    }
}
