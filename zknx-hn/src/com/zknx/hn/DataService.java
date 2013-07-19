package com.zknx.hn;

import com.zknx.hn.data.DataMan;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class DataService extends Service {
	
	// 检查间隔
	private static final int SECOND = 1000; // 秒

	// 数据绑定
	private final IBinder mBinder = new DataBinder();
	
	// 用户保存回调用的消息处理Handler，同时也用于数据更新（定时器）
	private Handler mHandler;
	// 有新数据提示消息
	public static final int MESSAGE_NEW_DATA = 2;
	// 有新留言
	public static final int MESSAGE_NEW_MESSAGE = 3;
	
	// 获取服务实例
	public class DataBinder extends Binder {
		DataService getService(Handler handler) {
			mHandler = handler;
			return DataService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}
	
	@Override
	public void onDestroy() { 
		super.onDestroy();
	}
	
	/**
	 * 开始检查广播数据
	 */
	public void startProcessBroadcastData() {
		new Thread(mProcessDataRunnable).start();
	}
	
	/**
	 * 开始检查新留言
	 */
	public void startGetNewMessage() {
		new Thread(mGetNewMessageRunnable).start();
	}

	// 数据更新Runnable
	private Runnable mProcessDataRunnable= new Runnable() {    
        public void run() {

        	// 每1分钟检查新数据
         	if (DataMan.CheckBroadcastData())
         		mHandler.sendEmptyMessage(MESSAGE_NEW_DATA);

        	mHandler.postDelayed(this, 60 * SECOND);
        }
    };
    
    // 检查新留言Runnable
 	private Runnable mGetNewMessageRunnable= new Runnable() {    
         public void run() {

        	// 每5秒检查新留言
         	String message = DataMan.GetNewMessages();
         	if (message != null)
         		mHandler.sendEmptyMessage(MESSAGE_NEW_MESSAGE);

         	mHandler.postDelayed(this, 5 * SECOND);
         }
     };
}
