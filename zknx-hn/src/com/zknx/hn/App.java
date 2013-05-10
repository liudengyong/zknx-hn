package com.zknx.hn;

import com.zknx.hn.common.UIConst;
import com.zknx.hn.functions.Setting;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class App extends Application {
	
	// XXX ���Կ���
	public static boolean mDebug = true;

	// ���糬ʱ
	private static int mTimeout = -1;
	
	// Ӧ�ó���Context
	public static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
	 * ��ȡ�������س�ʱ
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
	 * �����������س�ʱ
	 */
	public static void SetTimeout(int timeout) {
		mTimeout = timeout;
	}
	
	/**
	 * ��ȡ����״̬
	 * @return
	 *  0  ��ʾ mContext δ��ʼ��
	 *  1  ��ʾ ��ǰ�п�������
	 *  2  ��ʾ ��ǰ�ƶ��������
	 * -1 ��ʾ �޿�������
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
