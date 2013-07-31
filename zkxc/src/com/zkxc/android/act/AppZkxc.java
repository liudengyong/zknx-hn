package com.zkxc.android.act;

import com.zkxc.android.R;
import com.zkxc.android.table.controller.EditTextEx;
import com.zkxc.android.table.controller.InputView;
import com.zkxc.android.table.controller.MediaPicker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;

public class AppZkxc extends Application {
	
	// TODO 000 调试开关
	public static boolean mDebug = false;

	// 定位相关常量
	public static final String TOKEN_GPS = "GPS";
	public static final String TOKEN_WIFI = "WIFI";
	public static final String TOKEN_LOCATION_FAILED = "NO";
	
	public static Activity mActivityTable = null;
	
	// 保存GPS信息，WIFI位置信息
	private static String mLocation = null;
	
	public static void SetLocation(String token, String lat, String lng)
	{
		if (token.equals(TOKEN_WIFI))
		{
			String[] location = GetLocation();
			
			// 如果有GPS定位值，则不更新WIFI定位值
			if (location != null && location.length > 0 && !location[0].equals(TOKEN_GPS))
			{
				mLocation = TOKEN_WIFI + "," + lat + "," + lng;
				return;
			}
		}
		
		mLocation = token + "," + lat + "," + lng;
	}
	
	public static String[] GetLocation()
	{
		if (mLocation != null)
		{
			String[] token = mLocation.split(",");
			if (token != null && token.length == 3)
				return token;
		}
		
		return null;
	}
	
	public static int mTimeout = -1;
	
	public static class UserInfo {
		public String userId;
		public String addrId;
		public String protectionZoneId;
	}
	
	public static UserInfo mUserInfo;
	public static MediaPicker mCurMediaPicker;
	public static int mPenddingAct;
	
	// 快捷输入法
    public static boolean IsShortInputOn = true; // TODO ZZZ 系统设置打开快输？
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public static void SetUsrInfo(String userId, String addrId, String protectionZoneId)
	{
		if (mUserInfo == null)
			mUserInfo = new UserInfo();
		
		mUserInfo.userId = userId;
		mUserInfo.addrId = addrId;
		mUserInfo.protectionZoneId = protectionZoneId;
	}
	
	public static int GetTimeout(Context context)
	{
		if (mTimeout == -1 && context != null)
			mTimeout = ActSetting.GetTimeOut(context);
		
		// TODO ZZZ 下载超时设置限制，默认下载超时，最大五分钟
        if (mTimeout > 0 && mTimeout < 300)
        	mTimeout = ActSetting.DEFAULT_TIMEOUT;
		
		return mTimeout;
	}
	
	public int getNetworkStatus()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService( Context.CONNECTIVITY_SERVICE );
		
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); 
	    NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
	    if (activeNetInfo != null) 
	    {
	    	if (activeNetInfo.isAvailable())
	    		return 1;
	    	//Toast.makeText( context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show(); 
	    }
	    
	    if (mobNetInfo != null) 
	    {
	    	if (mobNetInfo.isAvailable())
	    		return 2;
	    	//Toast.makeText( context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show(); 
	    }
	    
	    return -1;
	}
	
	final static int INPUT_VIEW_OFFSET_X = 10;
	final static int INPUT_VIEW_OFFSET_Y = 6;
	
	private static int mHalfScreenWidth  = -1;
	private static int mHalfScreenHeight = -1;
	private static WindowManager mWinMan = null;
	private static InputMethodManager mInputMan = null;
	private static WindowManager.LayoutParams mWmParams = null;

	public static boolean mSyncedRecord = false;
	
	public static InputMethodManager GetInputMan(Context context) {
		if (mInputMan == null)
			mInputMan = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		return mInputMan;
	}
	
	public static void InitWmParams() {
		
		if (mWmParams == null) {
			mWmParams = new WindowManager.LayoutParams();
			
	         /**
	         *以下都是WindowManager.LayoutParams的相关属性
	         * 具体用途可参考SDK文档
	         */
			mWmParams.type = LayoutParams.TYPE_PHONE;   //设置window type
			mWmParams.format = PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明

	        //设置Window flag
			mWmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL |
	                          LayoutParams.FLAG_NOT_FOCUSABLE;
	        /*
	         * 下面的flags属性的效果形同“锁定”。
	         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
	         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
	                               | LayoutParams.FLAG_NOT_FOCUSABLE
	                               | LayoutParams.FLAG_NOT_TOUCHABLE;
	        */
	        
	        
			mWmParams.gravity = Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
		}
	}
	
	/**
	 * rect 输入框的边界
	 */
    public static void ShowInputView(EditTextEx editTextEx, Rect rect) {
    	
    	Object tag = editTextEx.getTag(R.id.tag_short_input);
    	
    	if (tag == null || !(tag instanceof String[]))
    		return;
    	
    	String[] values = (String[])tag;
    	Context context = editTextEx.getContext();
    	
		// 获取输入法视图
		InputView inputView = InputView.GetInputView(context);
		
		// 防止重复添加
    	if (inputView.getTag() != null)
    		return;
    	
		if (mWinMan == null) {
			
			DisplayMetrics metric = new DisplayMetrics();
			
			mWinMan = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			
			mWinMan.getDefaultDisplay().getMetrics(metric);
			mHalfScreenWidth  = (metric.widthPixels  / 2); // 屏幕宽度（像素）
			mHalfScreenHeight = (metric.heightPixels / 2); // 屏幕高度（像素）
	        
			InitWmParams();
		}
		
		// 不弹出键盘
		editTextEx.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
		editTextEx.setFocusable(true);
		
		GetInputMan(context).hideSoftInputFromWindow(editTextEx.getWindowToken(), 0);
		
		// 更新数据
		inputView.setInputChangeLisener(editTextEx);
		inputView.setInputSource(values);
		
        // 设置悬浮窗口长宽数据
		mWmParams.width  = InputView.GetPanelWidth();
		mWmParams.height = inputView.GetPanelHeight();
		
		// 以屏幕左上角为原点，设置x、y初始值
		
		// 屏幕左侧
		if (rect.left < mHalfScreenWidth)
		{
			// InputView 应该在控件的右侧
			mWmParams.x = (rect.left + INPUT_VIEW_OFFSET_X);
		}
		else
		{
			// InputView 应该在控件的左侧
			mWmParams.x = (rect.left - mWmParams.width - INPUT_VIEW_OFFSET_X);
		}
		
		// 屏幕上方
		if (rect.top < mHalfScreenHeight)
		{
			// InputView 应该在控件的下方
			mWmParams.y = (rect.top + rect.height() + INPUT_VIEW_OFFSET_Y);
		}
		else
		{
			// InputView 应该在控件的上方
			mWmParams.y = (rect.top - mWmParams.height - INPUT_VIEW_OFFSET_Y);
		}
		
		/*
		Log.w("InputView", "(rect) x = " + rect.left + ", y = " + rect.top + 
				", width = " + rect.width() + ", height = " + rect.height());
		Log.w("InputView", "(mWmParams) x = " + mWmParams.x + ", y = " + mWmParams.y + 
				", width = " + mWmParams.width + ", height = " + mWmParams.height);
		*/

        //显示myFloatView图像
		inputView.setTag("已添加");
        mWinMan.addView(inputView, mWmParams);
    }
    
    public static void CloseInputView(Context context) {
		if (mWinMan != null) {
			// 防止重复删除，做标记
			View view = InputView.GetInputView(context);
			
			if (view.getTag() != null) {
				mWinMan.removeView(view);
				view.setTag(null);
			}
		}
	}

	public static void UpdateInputViewPos(InputView inputView, int x, int y) {
		if (mWinMan != null) {
			mWmParams.x = x;
			mWmParams.y = y;
			mWinMan.updateViewLayout(inputView, mWmParams);
		}
	}
}
