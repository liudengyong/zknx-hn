package com.zknx.hn;

import com.zknx.hn.common.UIConst;
import com.zknx.hn.functions.Setting;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class App extends Application {
	
	// XXX 调试开关
	public static boolean mDebug = true;

	// 网络超时
	private static int mTimeout = -1;
	
	// 应用程序Context
	public static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
	 * 获取网络下载超时
	 * @return
	 */
	public static int GetTimeout() {
		if (mTimeout == -1 && mContext != null)
			mTimeout = Setting.GetTimeOut(mContext);
		
        if (mTimeout > 0 && mTimeout < UIConst.MAX_TIMEOUT)
        	mTimeout = Setting.DEFAULT_TIMEOUT;
		
		return mTimeout;
	}
	
	/**
	 * 设置网络下载超时
	 */
	public static void SetTimeout(int timeout) {
		mTimeout = timeout;
	}
	
	/**
	 * 获取网络状态
	 * @return
	 *  0  表示 mContext 未初始化
	 *  1  表示 当前有可用网络
	 *  2  表示 当前移动网络可用
	 * -1 表示 无可用网络
	 */
	public static int GetNetworkStatus()
	{
		if (mContext == null)
			return 0;

		ConnectivityManager connectivityMan = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

	    NetworkInfo activeNetInfo = connectivityMan.getActiveNetworkInfo();
	    NetworkInfo mobileNetInfo = connectivityMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if (activeNetInfo != null && activeNetInfo.isAvailable()) {
	    	return 1;
	    } else if (mobileNetInfo != null && mobileNetInfo.isAvailable()) {
	    	return 2;
	    } else {
	    	return -1;
	    }
	}
}
