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

	// �������ݵȴ�������
	private WaitDialog mWaitDialog;
	// �������������Ϣ
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

        // �����ظ�����
        // FIXME ���� unbindService
        if (mDataService == null) {
        	bindService(new Intent(this, DataService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

        if (DataMan.ShouldUpdateData()) {
        	// �����ȴ�������
        	mWaitDialog = WaitDialog.Show(this, null, "���ڸ�������", new WaitListener() {
				/**
				 * ��ʼ��¼
				 */
				@Override
				public void startWait() {
					DataMan.UpdateTodayData();
					mHandler.sendEmptyMessage(MESSAGE_UPDATE_DATA_COMPLETE);
				}
			});
        }
	}
	
	// Ϊ��̬Handler����ʵ��
	private static Function FunctionInstance;
	private static Handler mHandler = new Handler() {
	    /**
	     * ʵ����Ϣ����
	     */
	    @Override
	    public void handleMessage(Message msg) {
	    	FunctionInstance.processWaitMessage(msg.what);
	    }
	};

	/**
     * ��̨���ݷ���
     * @param id
     */
    private static DataService mDataService;
    
    // ֻ��һ����̬����ʵ������
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
	 * ��ʼ��ȫ�ֱ���
	 */
	private void initAppStatic() {
		App.mContext = getApplicationContext();
	}

	// ����������������Ϣ
	protected void processWaitMessage(int what) {
		switch (what) {
		case MESSAGE_UPDATE_DATA_COMPLETE:
		{
			// ���صȴ�������
			mWaitDialog.dismiss();
			// ���¸���
			initFunctionView(getIntent());
		}
		break;
		case DataService.MESSAGE_NEW_DATA:
		{
			// ��ʾ�Ƿ�������������
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

			showNotification("�յ�������");
		}
		break;
		}
	}
	
	/**
	  * ��״̬����ʾ֪ͨ
	  */
	 private void showNotification(String message){

	     // ����Notification�ĸ�������  
	     Notification notification = new Notification(R.drawable.icon_back,  
	    		 message, System.currentTimeMillis());

	     notification.flags |= Notification.FLAG_SHOW_LIGHTS;  
	     notification.defaults = Notification.DEFAULT_LIGHTS;

	     CharSequence contentTitle ="�п�ũ��"; // ֪ͨ������  
	     CharSequence contentText = message; // ֪ͨ������  

	     PendingIntent contentItent = PendingIntent.getActivity(this, 0, mSavedIntent, 0);  

	     notification.setLatestEventInfo(this, contentTitle, contentText, contentItent);  

	     // ��Notification���ݸ�NotificationManager  
	     ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(0, notification);  
	 }

	 //ɾ��֪ͨ   
	 private void clearNotification() {
	     // ������ɾ��֮ǰ���Ƕ����֪ͨ  
		 ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(0); 
	}

	/***
	 * �л�����ʱ����
	 * */
	@Override  
    protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		
		initFunctionView(intent);
    }
	
	/***
	 * ��ʼ�����н���
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
	 * ��ʼ�����ذ�ť
	 * */
	void intReturnHomeBtn() {
		ImageButton returnHome = (ImageButton)findViewById(R.id.function_btn_return_home);
		returnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
				/* XXX ��ǰҳ���˳� ����ת����ҳ��*/
				/*
				Intent intent = new Intent(Function.this, Home.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				*/
			}
		});
	}
	
	/**
	 * ���ñ���
	 * */
	void setTitle(Bundle extras) {
		
		TextView title = (TextView)findViewById(R.id.function_title_text);
		
		title.setText(Params.GetTitle(extras));
	}
	
	/**
	 * ��ʼ�����ܿ��
	 * */
	void intiFrame(Bundle extras) {
		
		LinearLayout frameRoot = (LinearLayout)findViewById(R.id.function_frame_content);
		LayoutInflater inflater = getLayoutInflater();

		mFunctionId = Params.GetFunction(extras);
		
		// ���֪ͨ����Ϣ
		if (mFunctionId == UIConst.FUNCTION_ID_MY_GROUP)
			clearNotification();

		// ��ʼ��������ͼ
		mFunctionView = Params.GetFunctionView(mFunctionId, inflater, frameRoot);
	}
	
	/**
	 * ��ʼ���ײ���ť
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
			// ����
			bottomBtns.setVisibility(View.GONE);
			return;
		}
		
		// �п�ũ�Ż��ߺ��ǵ���
		for (int i = 0; i < btns.getChildCount(); ++i) {
			RelativeLayout btnLayout = (RelativeLayout)btns.getChildAt(i);
			View btn = btnLayout.getChildAt(0);
			if (functionId == btn.getId()) {
				btn.setBackgroundResource(R.drawable.bottom_button_focus);
				btn.setEnabled(false); // ���õ�ǰ���ܰ�ť������������
			}
		}

		// ��ʼ��������ͼ
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

	    	// ���ܲ���
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
    		Debug.Log("���ش���:startSubActivity");
    	}
    }

    // XXX ȷ����Щ������Ҫ��¼
    private static boolean isNeedLogin(int id) {

    	// ����¼״̬������Ѿ���¼�������ظ�
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
